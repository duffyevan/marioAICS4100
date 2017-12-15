package ch.idsia.ai.agents.ai.Neural;

import org.json.JSONObject;

public class FileParser {
    public static NeuralNetwork loadNetwork(String json){
        JSONObject object = new JSONObject(json);
        NeuralNetwork n = new NeuralNetwork();
        for (int neuronID = 0; neuronID < 21; neuronID++){
            for (String edge : object.getJSONObject("" + neuronID).keySet()) {
                if (edge.equals("t")) {
                    n.getNeuronFromID(neuronID).setThreshold(Float.parseFloat((String) object.getJSONObject("" + neuronID).get(edge)));
                } else {
                    n.getNeuronFromID(neuronID).setWeight(Integer.parseInt(edge), Float.parseFloat((String) object.getJSONObject("" + neuronID).get(edge)));
                }
            }
        }
        return n;
    }

}
