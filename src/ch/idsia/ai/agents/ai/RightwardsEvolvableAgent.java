package ch.idsia.ai.agents.ai;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 25, 2009
 * Time: 12:30:41 AM
 * Package: ch.idsia.ai.agents.ai;
 */
public class RightwardsEvolvableAgent extends EvolvableAgent implements Agent
{

    private final float CHANCE_TO_INIT_RIGHT = .9f;
    private final float CHANCE_TO_INIT_LEFT = .05f;
public RightwardsEvolvableAgent(){
    for (int i = 0; i < actions.length; i ++){
        for (int j = 0; j < actions[i].length; j++){
            //special cases to get mario tend to go right
            if (j == Mario.KEY_LEFT){
                actions[i][j] = random.nextFloat() < CHANCE_TO_INIT_LEFT;
            }else if (j == Mario.KEY_RIGHT){
                actions[i][j] = random.nextFloat() < CHANCE_TO_INIT_RIGHT;
            }else{
                actions[i][j] = random.nextBoolean();

            }
        }
    }
}

}
