package ch.idsia.ai.agents.ai.Neural;

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
public class NeuralAgent implements Agent, Evolvable {
    private final int numberOfActions = 5000;
    private final float chanceToMutateFrame = 0.1f;
    private final float chanceToChangeGivenAction = 0.1f;

    protected Random random = new Random();
    private int frameCounter = 0;
    protected boolean actions[][] = new boolean[numberOfActions][Environment.numberOfButtons];
    protected String name = "Evolvable Agent";


    @Override
    public Evolvable getNewInstance() {
        return new NeuralAgent();
    }

    @Override
    public Evolvable copy() {
        boolean actionsCopy[][] = new boolean[numberOfActions][Environment.numberOfButtons];
        for (int i = 0; i < actions.length; i ++){
            for (int j = 0; j < actions[i].length; j++){
                actionsCopy[i][j] = actions[i][j];
            }
        }
        return new NeuralAgent(actionsCopy);
    }

    public NeuralAgent(String s) {
        setName(s);
    }

    public NeuralAgent(boolean actions[][]) {
        this.actions = actions;
    }

    public NeuralAgent() {
        init();
    }

    protected void init(){
        for (int i = 0; i < actions.length; i ++){
            for (int j = 0; j < actions[i].length; j++){
                actions[i][j] = random.nextBoolean();
            }
        }
    }

    public void reset() {
        frameCounter = 0;// Empty action
    }

    @Override
    public void mutate() {
        for (int i = 0; i < actions.length; i++) {
            if (random.nextFloat() < chanceToMutateFrame) {
                for (int j = 0; j < actions[i].length; j++) { // randomly change frame
                    if (random.nextFloat() < chanceToChangeGivenAction) {
                        actions[i][j] = !actions[i][j];
                    }
                }
            }
        }
    }

    public boolean[] getAction(Environment observation) {
        if (frameCounter >= numberOfActions)
            reset();
        return actions[frameCounter++];
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
