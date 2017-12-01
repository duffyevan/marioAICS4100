package ch.idsia.ai.agents.ai;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.environments.Environment;

public class MyAIAgent implements Agent{
    private String name = "MyAIAgent";

    @Override
    public void reset() {
        // TODO
    }

    @Override
    public boolean[] getAction(Environment observation) {
        return new boolean[0]; // TODO
    }

    @Override
    public AGENT_TYPE getType() {
        return AGENT_TYPE.AI;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
