package ch.idsia.ai.agents.ai.Neural;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Me on 12/12/2017.
 */
public class NeuralNetwork {

    private Random rand = new Random();

    private final int EDGE_NEURONS_NUM = 9;
    private final int MIDDLE_NEURONS_NUM= 7;
    private final int FINAL_NEURONS_NUM = 5;

    private final float CHANCE_TO_CHANGE_WEIGHTS = .1f;
    private final float CHANCE_TO_CHANGE_THRESH = .1f;

    private final float RANGE_CHANGE_FOR_WEIGHT = 1f;
    private final float RANGE_CHANGE_FOR_THRESH = 1f;

    private final float INIT_WEIGHT = 1.0f;

    private ArrayList<EdgeNeuron> edgeNeurons = new ArrayList<EdgeNeuron>();;
    private ArrayList<Neuron> middleNeurons = new ArrayList<Neuron>();;
    private ArrayList<Neuron> finalNeurons = new ArrayList<Neuron>();



    public NeuralNetwork(){

        int idCount = 0;
        //populate neruons
        for (int i = 0;i<EDGE_NEURONS_NUM;i++){
            edgeNeurons.add(new EdgeNeuron(idCount++, this));
        }
        for (int i = 0;i<MIDDLE_NEURONS_NUM;i++){
            middleNeurons.add(new Neuron(idCount++, this));
        }
        for (int i = 0;i<FINAL_NEURONS_NUM;i++){
            finalNeurons.add(new Neuron(idCount++, this));
        }

        //set up connections
        for (Neuron n : middleNeurons){
            for (EdgeNeuron en: edgeNeurons) {
                n.addConnection(en.id,INIT_WEIGHT);
            }
        }

        for (Neuron n : finalNeurons){
            for (Neuron mn: middleNeurons) {
                n.addConnection(mn.id,INIT_WEIGHT);
            }
        }
    }



    public void mutate(){
        for (int i = 0;i<EDGE_NEURONS_NUM;i++){
            if(rand.nextFloat()<CHANCE_TO_CHANGE_THRESH) {
                edgeNeurons.get(i).mutateThreshold(RANGE_CHANGE_FOR_THRESH);
            }

        }
        for (int i = 0;i<MIDDLE_NEURONS_NUM;i++){
            if(rand.nextFloat()<CHANCE_TO_CHANGE_THRESH) {
                middleNeurons.get(i).mutateThreshold(RANGE_CHANGE_FOR_THRESH);
            }
            if(rand.nextFloat()<CHANCE_TO_CHANGE_WEIGHTS){
                middleNeurons.get(i).mutateWeights(RANGE_CHANGE_FOR_WEIGHT);
            }
        }
        for (int i = 0;i<FINAL_NEURONS_NUM;i++){
            if(rand.nextFloat()<CHANCE_TO_CHANGE_THRESH) {
                finalNeurons.get(i).mutateThreshold(RANGE_CHANGE_FOR_THRESH);
            }
            if(rand.nextFloat()<CHANCE_TO_CHANGE_WEIGHTS){
                finalNeurons.get(i).mutateWeights(RANGE_CHANGE_FOR_WEIGHT);
            }
        }
    }

    public Neuron getNeuronFromID(int id){
        if(id<EDGE_NEURONS_NUM){
            return edgeNeurons.get(id);
        }else if(id<EDGE_NEURONS_NUM+MIDDLE_NEURONS_NUM){
            return middleNeurons.get(id-EDGE_NEURONS_NUM);
        }else{
            return finalNeurons.get(id-EDGE_NEURONS_NUM-MIDDLE_NEURONS_NUM);
        }
    }

    public NeuralNetwork copy(){
        ArrayList<EdgeNeuron> newEdgeNeurons = new ArrayList<EdgeNeuron>();;
        ArrayList<Neuron> newMiddleNeurons = new ArrayList<Neuron>();;
        ArrayList<Neuron> newFinalNeurons = new ArrayList<Neuron>();
        NeuralNetwork output = new NeuralNetwork(newEdgeNeurons,newMiddleNeurons,newFinalNeurons);
        //populate neruons
        for (int i = 0;i<EDGE_NEURONS_NUM;i++){
            newEdgeNeurons.add(edgeNeurons.get(i).copy(output));
        }
        for (int i = 0;i<MIDDLE_NEURONS_NUM;i++){
            newMiddleNeurons.add(middleNeurons.get(i).copy(output));
        }
        for (int i = 0;i<FINAL_NEURONS_NUM;i++){
            newFinalNeurons.add(finalNeurons.get(i).copy(output));
        }

        return output;
        //TODO: Heart of the error?
    }

    public NeuralNetwork(ArrayList<EdgeNeuron> edgeNeurons, ArrayList<Neuron> middleNeurons, ArrayList<Neuron> finalNeurons){

        this.edgeNeurons = edgeNeurons;
        this.middleNeurons = middleNeurons;
        this.finalNeurons = finalNeurons;

    }

    public boolean[] getActionsFromScene(byte[][] scene){

        boolean[] output = new boolean[FINAL_NEURONS_NUM];
        //populate edge neruons from surroundings
        edgeNeurons.get(0).setEdgeInput(scene[10][10]);
        edgeNeurons.get(1).setEdgeInput(scene[10][11]);
        edgeNeurons.get(2).setEdgeInput(scene[10][12]);
        edgeNeurons.get(3).setEdgeInput(scene[11][10]);
        edgeNeurons.get(4).setEdgeInput(scene[11][11]);
        edgeNeurons.get(5).setEdgeInput(scene[11][12]);
        edgeNeurons.get(6).setEdgeInput(scene[12][10]);
        edgeNeurons.get(7).setEdgeInput(scene[12][11]);
        edgeNeurons.get(8).setEdgeInput(scene[12][12]);

        //Fire final neruons into array
        for(int i = 0;i<FINAL_NEURONS_NUM;i++){
            output[i] = finalNeurons.get(i).fire();
        }

        return output;
    }
}
