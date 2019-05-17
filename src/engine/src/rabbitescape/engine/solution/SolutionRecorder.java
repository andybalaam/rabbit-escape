package rabbitescape.engine.solution;

import java.util.ArrayList;
import java.util.List;

public class SolutionRecorder implements SolutionRecorderTemplate
{
    private List<CommandAction> commandInProgress;
    private final List<SolutionCommand> solutionInProgress;

    public SolutionRecorder()
    {
        commandInProgress = new ArrayList<CommandAction>();
        solutionInProgress = new ArrayList<SolutionCommand>();
    }

    @Override
    public void append( CommandAction a )
    {
        commandInProgress.add( a );
    }

    @Override
    public void append( SolutionCommand newCmd )
    {
        int prevCmdIndex = solutionInProgress.size() - 1;
        SolutionCommand prevCmd =   prevCmdIndex >= 0
                                  ? solutionInProgress.get( prevCmdIndex )
                                  : null ;
        SolutionCommand combCmd =
            SolutionCommand.tryToSimplify( prevCmd, newCmd );
        if( null == combCmd)
        {
            solutionInProgress.add( newCmd );
        }
        else
        { // Replace previous with combined
            solutionInProgress.set( prevCmdIndex, combCmd );
        }
    }

    @Override
    public void append( Solution solution )
    {
        for ( SolutionCommand command : solution.commands )
        {
            append( command );
        }
    }

    @Override
    public void appendStepEnd()
    {
        CommandAction[] aA = new CommandAction[commandInProgress.size()];
        aA = commandInProgress.toArray( aA );
        append( new SolutionCommand( aA ) );
        // Prepare to collect actions in the next step.
        commandInProgress = new ArrayList<CommandAction>();
    }

    @Override
    public String getRecord()
    {
        SolutionCommand[] cA = new SolutionCommand[solutionInProgress.size()];
        Solution s = new Solution( solutionInProgress.toArray( cA ) );
        return SolutionParser.serialise( s );
    }
}
