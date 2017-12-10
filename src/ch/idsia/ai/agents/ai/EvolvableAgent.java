package ch.idsia.ai.agents.ai;

import ch.idsia.ai.Evolvable;
import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.environments.Environment;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 25, 2009
 * Time: 12:30:41 AM
 * Package: ch.idsia.ai.agents.ai;
 */
public class EvolvableAgent implements Agent, Evolvable {
    private final int numberOfActions = 100;
    private final float chanceToMutateFrame = 0.05f;
    private final float chanceToChangeGivenAction = 0.4f;

    private Random random = new Random();
    private int frameCounter = 0;
    private boolean actions[][] = new boolean[numberOfActions][Environment.numberOfButtons];
    protected String name = "Evolvable Agent";


    @Override
    public Evolvable getNewInstance() {
        return new EvolvableAgent();
    }

    @Override
    public Evolvable copy() {
        return new EvolvableAgent(actions);
    }

    public EvolvableAgent(String s) {
        setName(s);
    }

    public EvolvableAgent(boolean actions[][]) {
        this.actions = actions;
    }

    private EvolvableAgent() {
        for (boolean a[] : actions) {
            for (boolean b : a) { // randomly change frame
                b = random.nextBoolean();
            }
        }
    }

    public void reset() {
        frameCounter = 0;// Empty action
    }

    @Override
    public void mutate() {
        for (boolean a[] : actions) {
            if (random.nextFloat() < chanceToMutateFrame) {
                for (boolean b : a) { // randomly change frame
                    if (random.nextFloat() < chanceToChangeGivenAction) {
                        b = !b;
                    }
                }
            }
        }
    }

    public boolean[] getAction(Environment observation) {
        return actions[frameCounter++];
//        return new boolean[Environment.numberOfButtons]; // Empty action
    }

    public AGENT_TYPE getType() {
        return AGENT_TYPE.AI;
    }

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }
}
