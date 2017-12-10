package ch.idsia.ai.agents.ai;

import ch.idsia.ai.Evolvable;
import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.environments.Environment;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 25, 2009
 * Time: 12:30:41 AM
 * Package: ch.idsia.ai.agents.ai;
 */
public class EvolvableAgent implements Agent,Evolvable
{
    protected boolean action[] = new boolean[Environment.numberOfButtons];
    protected String name = "Instance_of_BasicAIAgent._Change_this_name";


    @Override
    public Evolvable getNewInstance() {
        return null;
    }

    @Override
    public Evolvable copy() {
        return null;
    }

    public EvolvableAgent(String s)
    {
        setName(s);
    }

    public void reset()
    {
        action = new boolean[Environment.numberOfButtons];// Empty action
    }

    @Override
    public void mutate() {

    }

    public boolean[] getAction(Environment observation)
    {
        return new boolean[Environment.numberOfButtons]; // Empty action
    }

    public AGENT_TYPE getType()
    {
        return AGENT_TYPE.AI;
    }

    public String getName() {        return name;    }

    public void setName(String Name) { this.name = Name;    }
}
