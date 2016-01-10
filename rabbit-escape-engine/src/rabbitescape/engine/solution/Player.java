package rabbitescape.engine.solution;

import java.util.Iterator;
import java.util.List;

import rabbitescape.engine.util.Util;

public class Player
{
    
    final Iterator<SolutionCommand> commands;
    private SolutionCommand currentCommand;
    private int waiting;
    boolean played = false;
    
    public Player( Solution s )
    {
        List<SolutionCommand> cmdList = Util.list( s.commands );
        commands = cmdList.iterator();
        waiting = 0;
    }

    public boolean hasMoreSteps()
    {
        return waiting > 0 || commands.hasNext() ;
    }
    
    /**
     * 
     * @return true if there are non-wait actions this step.
     */
    public boolean stepAndCheckForActions()
    {
        if ( waiting > 0 )
        {
            waiting-- ;
            return false;
        }
        if( commands.hasNext() )
        {
            currentCommand = commands.next();
        }
        else
        {
            return false; // TODO Exception here
        }
        if( currentCommand.actions[0] instanceof WaitAction )
        {
            WaitAction w = (WaitAction)currentCommand.actions[0];
            waiting = w.steps -1 ;
            return false;
        }
        return true;
    }
    
    public CommandAction[] getActions()
    {
        CommandAction[] actions = currentCommand.actions;
        // Only substantive actions should be returned.
        Util.reAssert( !(actions[0] instanceof WaitAction) );
        return actions;
    }

}
