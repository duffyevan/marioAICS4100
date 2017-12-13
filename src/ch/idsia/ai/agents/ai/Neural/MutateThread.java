package ch.idsia.ai.agents.ai.Neural;

public class MutateThread implements Runnable {
    private NeuralNetwork neuralNet;
    public MutateThread(NeuralNetwork n){
        neuralNet = n;
    }

    @Override
    public void run() {
        neuralNet.mutate();
    }
}
