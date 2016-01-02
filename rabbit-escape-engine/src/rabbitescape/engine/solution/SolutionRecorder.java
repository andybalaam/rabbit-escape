package rabbitescape.engine.solution;

import java.util.ArrayList;
import java.util.List;

public class SolutionRecorder
{
    private List<CommandAction> commandInProgress;
    private List<SolutionCommand> solutionInProgress;
    
    public SolutionRecorder()
    {
        commandInProgress = new ArrayList<CommandAction>();
        solutionInProgress = new ArrayList<SolutionCommand>();
    }

    public void append( CommandAction a ) 
    {
        commandInProgress.add( a );
    }
    
    public void appendStepEnd()
    {
        CommandAction[] aA = new CommandAction[commandInProgress.size()];
        aA = commandInProgress.toArray( aA );
        solutionInProgress.add( new SolutionCommand( aA ) );

        commandInProgress = new ArrayList<CommandAction>();
    }

    public String getRecord()
    {
        SolutionCommand[] cA = new SolutionCommand[solutionInProgress.size()];
        Solution s = new Solution( solutionInProgress.toArray( cA ) );
        return SolutionParser.serialise( s );
    }
}
