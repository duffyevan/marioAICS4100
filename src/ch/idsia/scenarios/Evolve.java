package ch.idsia.scenarios;

import ch.idsia.ai.Evolvable;
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.ai.EvolvableAgent;
import ch.idsia.ai.agents.ai.Neural.NeuralAgent;
import ch.idsia.ai.agents.ai.RightwardsEvolvableAgent;
import ch.idsia.ai.agents.ai.SimpleMLPAgent;
import ch.idsia.ai.ea.ES;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import wox.serial.Easy;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 4, 2009
 * Time: 4:33:25 PM
 */
public class Evolve {

    final static int generations = 10000;
    final static int populationSize = 100;
    final static int generationToShow = 100;
    final static int STARGING_DIFFICULTY = 1;

    final static boolean visualizeAll = false;

    public static void main(String[] args) {
        EvaluationOptions options = new CmdLineOptions(args);
        options.setNumberOfTrials(1);
        options.setPauseWorld(true);
        List<Agent> bestAgents = new ArrayList<Agent>();
        DecimalFormat df = new DecimalFormat("0000");
        for (int difficulty = STARGING_DIFFICULTY; difficulty < 11; difficulty++)
        {
            System.out.println("New Evolve phase with difficulty = " + difficulty + " started.");
            Evolvable initial = null;
            initial = new NeuralAgent();

//            try { // uncomment this to load the initial from a file
//                initial = new NeuralAgent(new String(Files.readAllBytes(Paths.get("JSON_NN/gen215_d1best.json")),"UTF-8"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


            options.setLevelDifficulty(difficulty);
            options.setAgent((Agent)initial);

            Task task = new ProgressTask(options);

            options.setMaxFPS(true);
            options.setVisualization(false);

            ES es = new ES (task, initial, populationSize);
            for (int gen = 0; gen < generations; gen++) {
                es.nextGeneration();
                double bestResult = es.getBestFitnesses()[0];
//                LOGGER.println("Generation " + gen + " best " + bestResult, LOGGER.VERBOSE_MODE.INFO);
                System.out.println("Generation " + gen + " best " + bestResult);
                if (visualizeAll) {
                    options.setVisualization(true);
                    options.setMaxFPS(false);
                } else {
                    options.setVisualization(gen % generationToShow == 0 || bestResult > 4000);
                    options.setMaxFPS(true);
                }

                // DONE log to JSON file
                try {
                    PrintWriter bestLog = new PrintWriter("JSON_NN/gen" + gen + "_d" + difficulty + "best.json", "UTF-8");
                    bestLog.write(((NeuralAgent) es.getBests()[0]).getNeuralNet().toJSON());
                    bestLog.close();
                } catch (java.io.IOException e){
                    e.printStackTrace();
                }
                Agent a = (Agent) es.getBests()[0];
                a.setName(((Agent)initial).getName() + df.format(gen));
//                AgentsPool.setCurrentAgent(a);
                bestAgents.add(a);
                double result = task.evaluate(a)[0];
//                LOGGER.println("trying: " + result, LOGGER.VERBOSE_MODE.INFO);
                options.setVisualization(false);
                options.setMaxFPS(true);
                Easy.save (es.getBests()[0], "evolved.xml");
//                if (result > 4000)
//                    break; // Go to next difficulty.
            }
        }
        /*// TODO: log dir / log dump dir option
        // TODO: reduce number of different
        // TODO: -fq 30, -ld 1:15, 8 
        //LOGGER.println("Saving bests... ", LOGGER.VERBOSE_MODE.INFO);

        options.setVisualization(true);
        int i = 0;
        for (Agent bestAgent : bestAgents) {
            Easy.save(bestAgent, "bestAgent" +  df.format(i++) + ".xml");
        }

        LOGGER.println("Saved! Press return key to continue...", LOGGER.VERBOSE_MODE.INFO);
        try {System.in.read();        } catch (IOException e) {            e.printStackTrace();        }

//        for (Agent bestAgent : bestAgents) {
//            task.evaluate(bestAgent);
//        }


        LOGGER.save("log.txt");*/
        System.exit(0);
    }

    /**
     * Displays a single neural network loaded from a file
     * @param filename The file to load from
     * @param fast whether is should be max fps or not
     */
    static void run (String filename, boolean fast){
        NeuralAgent n = null;
        try {
            n = new NeuralAgent(new String(Files.readAllBytes(Paths.get(filename)),"UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        options.setLevelDifficulty(1);
        options.setAgent((Agent)n);
        options.setNumberOfTrials(1);
        options.setPauseWorld(true);
        options.setMaxFPS(fast);
        Task task = new ProgressTask(options);
        task.evaluate(n);

    }
}
