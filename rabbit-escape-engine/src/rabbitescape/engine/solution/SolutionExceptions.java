package rabbitescape.engine.solution;

import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.err.RabbitEscapeException;

public class SolutionExceptions
{
    /**
     * Base class for everything that can go wrong when running a solution.
     * The member variables get filled in as we roll up the stack through
     * the instruction we are on, and the solution we are running.
     */
    public static abstract class ProblemRunningSolution
        extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public int solutionId = -1;
        public int instructionIndex = -1;
    }

    /**
     * Indicates that a world was not in the state we asserted it should
     * be in, in our solution.
     */
    public static class UnexpectedState extends ProblemRunningSolution
    {
        private static final long serialVersionUID = 1L;

        public final CompletionState expected;
        public final CompletionState actual;

        public UnexpectedState(
            CompletionState expected, CompletionState actual )
        {
            this.expected = expected;
            this.actual = actual;
        }
    }

    /**
     * Special subclass of UnexpectedState for when the expected state
     * was "WON" - a separate exception so we can give a clearer error message
     * in this common case.
     */
    public static class DidNotWin extends UnexpectedState
    {
        private static final long serialVersionUID = 1L;

        public DidNotWin( CompletionState actualState )
        {
            super( CompletionState.WON, actualState );
        }
    }

}
