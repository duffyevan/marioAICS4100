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

    protected String name = "Neural Agent";
    private NeuralNetwork neuralNet = new NeuralNetwork();


    @Override
    public Evolvable getNewInstance() {
        return this.copy();
    }

    @Override
    public Evolvable copy() {
        //make new copy of neural net
        NeuralNetwork newNeuralNet = neuralNet.copy();
        return new NeuralAgent(newNeuralNet);
    }

    public NeuralAgent(NeuralNetwork nn) {
        this.neuralNet = nn;
    }

    public NeuralAgent(String json){
        this.neuralNet = FileParser.loadNetwork(json);
    }

    public NeuralAgent() {
        init();
    }

    protected void init(){

    }

    public void reset() {

    }

    @Override
    public void mutate() { // fixme
        neuralNet.mutate();
    }

    public boolean[] getAction(Environment observation) { // FIXME
        return neuralNet.getActionsFromScene(observation.getLevelSceneObservation(), observation.getEnemiesObservation());
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

    public NeuralNetwork getNeuralNet() {
        return neuralNet;
    }
}
