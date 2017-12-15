package ch.idsia.ai.agents.ai.Neural;

import java.util.HashMap;

/**
 * Created by Me on 12/12/2017.
 */
public class EdgeNeuron extends Neuron{

    @Deprecated
    @Override
    public void setWeights(HashMap<Integer, Float> weight) {
        super.setWeights(weight);
    }

    public EdgeNeuron(int id, NeuralNetwork owner){
        super(id, owner);
    }
    public float getEdgeInput() {
        return edgeInput;
    }

    public void setEdgeInput(float edgeInput) {
        this.edgeInput = edgeInput;
    }

    private float edgeInput;

    //propogates upwards
    @Override
    public boolean fire(){
        return edgeInput>threshold;
    }

    @Override
    public EdgeNeuron copy(NeuralNetwork owner) {
        return new EdgeNeuron(id,owner);
    }

    @Override
    public String toJSON() {
        return "\"" + id + "\":{\n" +
                "\"t\":\"" + threshold + "\"\n"+
                "}";
    }
}
