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
    private final int TILES_AHEAD_TO_JUMP = 3;
    private final float MARIO_RUN_AWAY_DISTANCE = 2;
    private final int TILES_UP_TO_BLOCKS = 9;
    private final int TIMES_ADEAD_TO_SPOT_BLOCK = 10;
    private final int MARIO_CENTER_X = 11;//tested to be true
    private final double RANDOM_JUMP_CHANCE = .05;
    private final double PERCENT_CHANCE_JUMP_ON_ENEMY = 50;
    private final double FRAMES_WAIT_UNDER_PRIZE = 5;
    private final int FLOWER_ID = 15;
    private final int SHROOM_ID = 14;
    private final int LADDER_ID = 69; // FIXME this isnt the right ID I need to find the right one
    private final int MIN_FOLLOW_DISTANCE = 2;
    private final int TIME_TOO_LONG = 120;
    private final int FRAMES_TO_STOP_FLOWER = 0;

    private long frame = 0;//1 mean mario tries to go right, -1 means left, 0 means stay still
    private String name = "MyAIAgent";
    private boolean[] action;
    private boolean flame_toggle = false;
    private Random random = new Random();
    private int stopCounter = 0;
    private int previousEnemies = 0;
    private boolean wantsToOpenPrizes = true;
    private  int noOpenFrames;
    private int framesUnderPrize = 0;
    private boolean wasUnderAir = true;
    private int ignoreCounter = 0;

    private ArrayList<Entity> enemies = new ArrayList<>();
    private int timeTaken = 0;

    private int targetDir = 1;

    private class Entity {
        float x,y = 0;
        Entity(float x, float y){
            this.x = x;
            this.y = y;
        }
        double distanceTo(float x, float y){
            return sqrt(pow(x-this.x,2)+pow(y-this.y,2));
        }
        @Override
        public String toString() {
            return "Entity At (" + x + "," + y + ")";
        }
    }

    /**
     * Reset the Agent
     */
    @Override
    public void reset() {
        action = new boolean[Environment.numberOfButtons];
    }


    //based on target dir, move mario left or right
    public void moveMarioInCorrectDir(int targetDir){
        switch (targetDir){
            case 1: action[Mario.KEY_RIGHT] = true;
                action[Mario.KEY_LEFT] = false;
                break;
            case -1: action[Mario.KEY_LEFT] = true;
                action[Mario.KEY_RIGHT] = false;
                break;
            case 0: action[Mario.KEY_LEFT] = false;
                action[Mario.KEY_RIGHT] = false;
                break;
        }
    }

    private void jumpOverWalls(Environment observation, byte[][] scene){
        //if there is something a few tiles ahead of mario, jump
        if (observation.mayMarioJump()) {
            for (int i = 0; i < TILES_AHEAD_TO_JUMP; i++) {

//                if (scene[11+(i*targetDir)][13] != 0 || scene[11+(i*targetDir)][12] != 0) {//multiply in targetDir to check left or right(as necessary)
                if (scene[11][11+(i*targetDir)] != 0 || scene[10][11+(i*targetDir)] != 0) {//multiply in targetDir to check left or right(as necessary)
                    if(scene[12][11+(1*targetDir)] != 0) {//if there is a cranny to fall into, dont jump
                        action[Mario.KEY_JUMP] = true; // jump and move right
                        moveMarioInCorrectDir(targetDir);
                    }
                }
            }
        }else if (!observation.isMarioOnGround()){ // if mario may not jump and is not on the ground
            action[Mario.KEY_JUMP] = true; // hold the jump key for a higher jump
            moveMarioInCorrectDir(targetDir);        }
        else { // if mario is on the ground and may not jump
            moveMarioInCorrectDir(targetDir);        }
    }

    private void reactToEnemies(Environment observation, byte[][] scene){


        if (random.nextInt(100) < (enemies.size() - previousEnemies) * 100) {
            stopCounter = FRAMES_TO_STOP;
        }
        previousEnemies = enemies.size();


        decodeEnemies(observation);

        if (enemies.size() > 0){
            for (Entity e : enemies){
                if (e.distanceTo(11,11) < MARIO_RUN_AWAY_DISTANCE) {
                    int die = random.nextInt(100);
                    if (die > PERCENT_CHANCE_JUMP_ON_ENEMY) {
                        action[Mario.KEY_JUMP] = true; // try to jump over
                        moveMarioInCorrectDir(targetDir);
                    } else {
                        moveMarioInCorrectDir(-targetDir);
                    }
                }
            }
        }
    }
    //move so that mario reacts to enemies and walls
    private void smartMove(int targetDir, Environment observation, byte[][] scene){


    }

    private void startNoOpenTimer(){
        wantsToOpenPrizes = false;
         noOpenFrames = random.nextInt(70)-10;
        if(noOpenFrames < 0){
           noOpenFrames = 0;
        }
    }

    private void checkNoOpenTimer(){
        noOpenFrames--;
        if(noOpenFrames<=0){
            wantsToOpenPrizes = true;
        }
    }

    //stops and jumps to open ? blocks
    private void openPrizeBlocks(byte[][] scene){
        //if ? block above, stop moving and jump
        for (int i = 0;i<TILES_UP_TO_BLOCKS;i++) {//for a colum of blocks from the bottom of the map
            for (int j = 0; j<TIMES_ADEAD_TO_SPOT_BLOCK;j++) {
                if(wantsToOpenPrizes)
                if (scene[10 - i][6+j] == 21) {//if ? block above and a bit to the left or right
                    if(6+j<MARIO_CENTER_X){
                        targetDir = -1;
                    }else if (6+j>MARIO_CENTER_X){
                        targetDir = 1;
                    }else{
                        targetDir = 0;
                        if(framesUnderPrize > FRAMES_WAIT_UNDER_PRIZE) {//wait 1 frame of being under block to jump, otherwise he can jump wrongly
                            action[Mario.KEY_JUMP] = true;
                            startNoOpenTimer();
                            framesUnderPrize = 0;
                        }else{
                            framesUnderPrize++;
                        }
                    }
                    break;
                }
            }
        }

        checkNoOpenTimer();
    }

    //randomly jumps to add human element and to avoid some bugs where he gets locked
    private void randomJump(){
        if(random.nextFloat()<RANDOM_JUMP_CHANCE){
            action[Mario.KEY_JUMP] = true;
        }

    }

    private void randomRun(Environment observation){
        if(observation.getMarioMode() != 2 && enemies.size()<=0){
            action[Mario.KEY_SPEED] = true;
        }
    }

    private void manageFire(Environment observation) {
        if (observation.getMarioMode() == 2) { // if mario is in fire mode
            if(enemies.size()>0) {//if enemies on screen
                if (flame_toggle) // toggle pressed and not pressed
                    action[Mario.KEY_SPEED] = true; // press fire
                flame_toggle = !flame_toggle; // rapid fire fireballs rather than hold the button
            }
        }
    }

    /**
     * return true if there are any flowers on the screen
     * @param observation The environment from the engine
     * @return true if there are any flowers in the scene
     */
    private ArrayList<Entity> powerUpsOnScreen(Environment observation) {
        ArrayList<Entity> ret = new ArrayList<>();
        byte [][]enemyScene = observation.getEnemiesObservation();
        for (int y = 0; y < enemyScene.length; y++) {
            for (int x = 0; x < enemyScene[0].length; x++) {
                if (enemyScene[y][x] == FLOWER_ID || enemyScene[y][x] == SHROOM_ID){
                    ret.add(new Entity(x,y));
                }
            }
        }
        return ret;
    }


    /**
     * Get the actions to perform for this turn
     * @param observation the current state on the screen
     * @return the actions we wish to perform this frame
     */
    @Override
    public boolean[] getAction(Environment observation) {
//        System.out.println(enemySceneToString(observation));
        reset(); // clear out the action array (idk if this causes a memory leak, I assume java will take care of it)
        targetDir = 1;
        frame++;
        stopCounter--;
        byte[][] scene = observation.getLevelSceneObservation();

        //each of these functions is a task, starting with the lowest priority task the highest priority task
        randomRun(observation);

        randomJump();

        openPrizeBlocks(scene);

        chasePowerUps(observation);

        manageFire(observation);

        moveMarioInCorrectDir(targetDir);

        jumpOverWalls(observation,scene);

        reactToEnemies(observation,scene);



        if (stopCounter >= 0){
            action[Mario.KEY_RIGHT] = false;
            action[Mario.KEY_LEFT] = false;
        }

//        System.out.println(enemies);
        return action; // give back our array of actions for this frame.
    }

    private void jumpAtLadder(){

    }

    private void chasePowerUps(Environment observation){
        ArrayList<Entity> powerUps = powerUpsOnScreen(observation);
        if (ignoreCounter > 0)
            ignoreCounter--;
        if (powerUps.size() > 0 && ignoreCounter == 0) {
            if (timeTaken != TIME_TOO_LONG) {
                timeTaken++;
                if (11 > powerUps.get(0).y && observation.isMarioOnGround()){ // if were below it
                    if (observation.getLevelSceneObservation()[9][11] == 0 && observation.getLevelSceneObservation()[8][11] == 0 && observation.getLevelSceneObservation()[8][11-targetDir] == 0) { // if there's air above head
                        if (!wasUnderAir)
                            ignoreCounter = FRAMES_TO_STOP_FLOWER;
                        wasUnderAir = true;
                        targetDir = (powerUps.get(0).x > 11) ? 1 : -1; // move toward and jump
                        action[Mario.KEY_JUMP] = true;
                    } else {
                        wasUnderAir = false;
                        targetDir = (powerUps.get(0).x > 11) ? -1 : 1; // move away
                    }
                } else { // if were above or on the same level
                    targetDir = (powerUps.get(0).x > 11) ? 1 : -1; // move toward
                }
            }
        }
        else if (powerUps.size() == 0){
            timeTaken = 0;
        }
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
        enemies = new ArrayList<Entity>();
        byte [][]enemyScene = observation.getEnemiesObservation();
        for (int y = 0; y < enemyScene.length; y++) {
            for (int x = 0; x < enemyScene[0].length; x++) {
                if (enemyScene[y][x] == 2 || enemyScene[y][x] == 12){ // red guy is 2, piranah is 12
                    enemies.add(new Entity(x,y));
                }
            }
        }
    }

    private int getLadderX(Environment observation){
        for (int y = 0; y < observation.getEnemiesObservation().length; y++){
            for (int x = 0; x < observation.getEnemiesObservation()[0].length; x++){
                if (observation.getEnemiesObservation()[y][x] == LADDER_ID)
                    return x;
            }
        }
        return -1;
    }

    String enemySceneToString(Environment observation){
        byte [][]enemyScene = observation.getEnemiesObservation();
        String ret = "";
        for (byte [] c : enemyScene){
            for (byte b : c){
                ret += b + ",";
            }
            ret += "\n";
        }
        return ret;
    }
}
