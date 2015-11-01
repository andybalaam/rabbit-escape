package rabbitescape.engine.solution;

import rabbitescape.engine.World.CompletionState;

public class TargetState implements ValidationInstruction
{
    private final CompletionState targetState;
    private final int solutionId;
    private final int instructionIndex;

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
    public void performOn( SandboxGame sandboxGame )
    {
        if ( sandboxGame.getWorld().completionState() != targetState )
        {
            throw new InvalidSolution( "Solution " + solutionId
                + " did not cause " + targetState
                + " at instruction " + instructionIndex );
        }
    }

    @Override
    public String toString()
    {
        return "TargetState( "
            + targetState.name()
            + ", " + solutionId
            + ", " + instructionIndex
            + " )";
    }

    @Override
    public boolean equals( Object otherObj )
    {
        if ( ! ( otherObj instanceof TargetState ) )
        {
            return false;
        }
        TargetState other = (TargetState)otherObj;

        return (
               targetState      == other.targetState
            && solutionId       == other.solutionId
            && instructionIndex == other.instructionIndex
        );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + instructionIndex;
        result = prime * result + solutionId;
        result = prime * result + targetState.hashCode();
        return result;
    }
}
