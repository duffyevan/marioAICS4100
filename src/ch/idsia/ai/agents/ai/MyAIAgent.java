package ch.idsia.ai.agents.ai;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class MyAIAgent implements Agent{
    private String name = "MyAIAgent";
    private boolean[] action;
    private boolean flame_toggle = false;

    /**
     * Reset the Agent
     */
    @Override
    public void reset() {
        action = new boolean[Environment.numberOfButtons];
    }

    /**
     * Get the actions to perform for this turn
     * @param observation the current state on the screen
     * @return the actions we wish to perform this frame
     */
    @Override
    public boolean[] getAction(Environment observation) {
        reset(); // clear out the action array (idk if this causes a memory leak, I assume java will take care of it)


        if (observation.getMarioMode() == 2) { // if mario is in fire mode
            if (flame_toggle) // toggle pressed and not pressed
                action[Mario.KEY_SPEED] = true; // press fire
            flame_toggle = !flame_toggle; // rapid fire fireballs rather than hold the button
        } else { // if not fire mario
            action[Mario.KEY_SPEED] = true; // just hold down the speed button to go faster
        }


        if (observation.mayMarioJump()){ // if mario can jump
            action[Mario.KEY_JUMP] = true; // jump and move right
            action[Mario.KEY_RIGHT] = true;
        }
        else if (!observation.isMarioOnGround()){ // if mario may not jump and is not on the ground
            action[Mario.KEY_JUMP] = true; // hold the jump key for a higher jump
            action[Mario.KEY_RIGHT] = true; // hold the right key to keep moving right
        }
        else { // if mario is on the ground and may not jump
            action[Mario.KEY_JUMP] = false; // dont jump
            action[Mario.KEY_RIGHT] = true; // keep moving right tho
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
}
