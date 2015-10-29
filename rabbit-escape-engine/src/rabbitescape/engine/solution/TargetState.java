package rabbitescape.engine.solution;

import rabbitescape.engine.World;
import rabbitescape.engine.World.CompletionState;

public class TargetState implements ValidationInstruction
{
    private CompletionState targetState;
    private int solutionId;
    private int instructionIndex;

    public TargetState(
        CompletionState targetState,
        int solutionId,
        int instructionIndex )
    {
        this.targetState = targetState;
        this.solutionId = solutionId;
        this.instructionIndex = instructionIndex;
    }

    @Override
    public void performOn( World world )
    {
        if ( world.completionState() != targetState )
        {
            throw new InvalidSolution( "Solution " + solutionId
                + " did not cause " + targetState
                + " at instruction " + instructionIndex );
        }
    }
}
