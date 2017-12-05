package ch.idsia.ai.agents.ai;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.pow;
import static java.lang.StrictMath.sqrt;

public class MyAIAgent implements Agent{
    private final int FRAMES_TO_STOP = 20;
    private final int TILE_WIDTH = 32;
    private final int TILES_AHEAD_TO_JUMP = 1;
    private final float MARIO_RUN_AWAY_DISTANCE = 50;

    private long frame = 0;
    private String name = "MyAIAgent";
    private boolean[] action;
    private boolean flame_toggle = false;
    private Random random = new Random();
    private int stopCounter = 0;
    private int previousEnemies = 0;
    private ArrayList<Enemy> enemies;

    private class Enemy {
        float x,y = 0;
        Enemy(float x, float y){
            this.x = x;
            this.y = y;
        }
        double distanceTo(float x, float y){
            return sqrt(pow(x-this.x,2)+pow(y-this.y,2));
        }
        @Override
        public String toString() {
            return "Enemy At (" + x + "," + y + ")";
        }
    }

    /**
     * Reset the Agent
     */
    @Override
    public void reset() {
        action = new boolean[Environment.numberOfButtons];
    }


    public void moveMarioInCorrectDir(int targetDir){
        switch (targetDir){
            case 1: action[Mario.KEY_RIGHT] = true;
                action[Mario.KEY_LEFT] = false;
                break;
            case -1: action[Mario.KEY_LEFT] = true;
                action[Mario.KEY_RIGHT] = false;
                break;
        }
    }

    /**
     * Get the actions to perform for this turn
     * @param observation the current state on the screen
     * @return the actions we wish to perform this frame
     */
    @Override
    public boolean[] getAction(Environment observation) {
        int targetDir = 0;//1 mean mario tries to go right, -1 means left, 0 means stay still


        reset(); // clear out the action array (idk if this causes a memory leak, I assume java will take care of it)
        frame++;
        stopCounter--;

        byte[][] scene = observation.getLevelSceneObservation();

        if (observation.getMarioMode() == 2) { // if mario is in fire mode
            if (flame_toggle) // toggle pressed and not pressed
                action[Mario.KEY_SPEED] = true; // press fire
            flame_toggle = !flame_toggle; // rapid fire fireballs rather than hold the button
        }

        moveMarioInCorrectDir(targetDir);

        //if there is something a few tiles ahead of mario, jump
        if (observation.mayMarioJump()) {
            for (int i = 0; i < TILES_AHEAD_TO_JUMP; i++) {

                    if (scene[11+(i*targetDir)][13] != 0 || scene[11+(i*targetDir)][12] != 0) {//multiply in targetDir to check left or right(as necessary)
                        action[Mario.KEY_JUMP] = true; // jump and move right
                        moveMarioInCorrectDir(targetDir);
                    }
            }
        }
        else if (!observation.isMarioOnGround()){ // if mario may not jump and is not on the ground
            action[Mario.KEY_JUMP] = true; // hold the jump key for a higher jump
            moveMarioInCorrectDir(targetDir);        }
        else { // if mario is on the ground and may not jump
            moveMarioInCorrectDir(targetDir);        }

        if (stopCounter >= 0){
            action[Mario.KEY_RIGHT] = false;
            action[Mario.KEY_LEFT] = false;
        }

        if (random.nextInt(100) < (observation.getEnemiesFloatPos().length - previousEnemies) * 100) {
            stopCounter = FRAMES_TO_STOP;
        }
        previousEnemies = observation.getEnemiesFloatPos().length;

//        if (observation.getMarioMode() != 2 && observation.getEnemiesFloatPos().length != 0){
//            action[Mario.KEY_JUMP] = true;
//        }
        decodeEnemies(observation);

        if (observation.getEnemiesFloatPos().length > 0){
            for (Enemy e : enemies){
                if (e.distanceTo(observation.getMarioFloatPos()[0],observation.getMarioFloatPos()[1]) < MARIO_RUN_AWAY_DISTANCE) {
                    int die = random.nextInt(20);
                    if (die > 18) {
                        action[Mario.KEY_JUMP] = true; // try to jump over
                        moveMarioInCorrectDir(targetDir);
                    } else {
                        action[Mario.KEY_LEFT] = true; // run away
                        moveMarioInCorrectDir(-targetDir);
                    }
                }
            }
        }

        return action; // give back our array of actions for this frame.
    }

    /**
     * Get the agent type
     * @return AI because this is an AI
     */
    @Override
    public AGENT_TYPE getType() {
        return AGENT_TYPE.AI;
    }

    /**
     * Return the name of this agent
     * @return name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the string name of this agent
     * @param name the name to set for this agent
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    void decodeEnemies(Environment observation){
        try {
            enemies = new ArrayList<Enemy>();
            for (int i = 1; i < observation.getEnemiesFloatPos().length; i += 2) {
                enemies.add(new Enemy(observation.getEnemiesFloatPos()[i], observation.getEnemiesFloatPos()[i + 1]));
            }
        } catch (ArrayIndexOutOfBoundsException e){
            enemies = new ArrayList<Enemy>();
        }
    }
}
