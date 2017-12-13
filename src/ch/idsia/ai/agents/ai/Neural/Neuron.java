package ch.idsia.ai.agents.ai.Neural;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Me on 12/12/2017.
 */
public class Neuron {
    Random rand = new Random();
    protected float threshold = 1f;
    protected HashMap<Integer,Float> weights = new HashMap<>();
    protected NeuralNetwork owner;
    int id;
    private final float CHANCE_TO_CHANGE_WEIGHT = 1f;



    public void mutateThreshold(float range){
        threshold = threshold + ((rand.nextFloat() * range)-(range/2));
    }

    public void mutateWeights(float range){
        for (Integer i:weights.keySet()){
            if(rand.nextFloat()<CHANCE_TO_CHANGE_WEIGHT){
                weights.put(i,weights.get(i)  + ((rand.nextFloat() * range)-(range/2)));
            }
        }
    }

    //for init
    public Neuron(int id, NeuralNetwork owner){
        this.id = id;
        this.owner = owner;
    }

    //for copying
    public Neuron(Neuron oldVer, NeuralNetwork newOwner){
        this.threshold = oldVer.threshold;
        this.weights = (HashMap)oldVer.weights.clone();
        this.id = oldVer.id;
        this.owner = newOwner;
    }

    public Neuron copy(NeuralNetwork owner){
        return new Neuron(this, owner);
    }

    public HashMap<Integer, Float> getWeight() {
        return weights;
    }

    public void setWeight(HashMap<Integer, Float> weight) {
        this.weights = weight;
    }

    //propogates upwards
    public boolean fire(){
        float totalWeight = 0.0f;

        for(Integer i:weights.keySet()){
            Neuron n = owner.getNeuronFromID(i);
            if(n.fire()){
                totalWeight += weights.get(n.id);
            }
        }

        return totalWeight >= threshold;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public void addConnection(Integer n, Float newWeight){
        weights.put(n,newWeight);
    }



}
