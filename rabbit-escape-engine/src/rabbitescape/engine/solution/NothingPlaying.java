package rabbitescape.engine.solution;

import rabbitescape.engine.World.CompletionState;

final class NothingPlaying extends SolutionInterpreter
{
    public NothingPlaying()
    {
        super( SolutionParser.parse("1") );
    }
    
    @Override
    public SolutionTimeStep next( CompletionState worldState )
    {
        return new SolutionTimeStep( ++commandIndex, new TimeStepAction[] {} );
    }
}