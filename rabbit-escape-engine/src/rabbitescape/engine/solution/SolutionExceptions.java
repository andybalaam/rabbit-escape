package rabbitescape.engine.solution;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.err.RabbitEscapeException;

public class SolutionExceptions
{
    /**
     * Base class for everything that can go wrong when running a solution.
     * The member variables get filled in as we roll up the stack through
     * the command we are on, and the solution we are running.
     */
    public static abstract class ProblemRunningSolution
        extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public int solutionId = -1;
        public int commandIndex = -1;
        public String level = "<>";
        public String world = "";

        public ProblemRunningSolution()
        {
        }

        public ProblemRunningSolution( Throwable cause )
        {
            super( cause );
        }
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

    /**
     * The solution went on longer, but the world was at WON or LOST.
     */
    public static class RanPastEnd extends ProblemRunningSolution
    {
        private static final long serialVersionUID = 1L;

        public final CompletionState worldState;

        public RanPastEnd( CompletionState worldState )
        {
            this.worldState = worldState;
        }
    }

    /**
     * We used a token that we've already used up.
     */
    public static class UsedRunOutAbility extends ProblemRunningSolution
    {
        private static final long serialVersionUID = 1L;

        public final Token.Type ability;

        public UsedRunOutAbility( Token.Type ability )
        {
            this.ability = ability;
        }
    }

    /**
     * We used a token that was not available in this world.
     */
    public static class UsedMissingAbility extends ProblemRunningSolution
    {
        private static final long serialVersionUID = 1L;

        public final Token.Type ability;

        public UsedMissingAbility( Token.Type ability )
        {
            this.ability = ability;
        }
    }

    /**
     * We placed a token outside the size of this world.
     */
    public static class PlacedTokenOutsideWorld extends ProblemRunningSolution
    {
        private static final long serialVersionUID = 1L;

        public final int x;
        public final int y;
        public final int worldWidth;
        public final int worldHeight;

        public PlacedTokenOutsideWorld(
            int x,
            int y,
            int worldWidth,
            int worldHeight
        )
        {
            this.x = x;
            this.y = y;
            this.worldWidth = worldWidth;
            this.worldHeight = worldHeight;
        }
    }

    /**
     * We placed a token but the world wouldn't accept it (presumably because
     * there was a block at that position).
     */
    public static class FailedToPlaceToken extends ProblemRunningSolution
    {
        private static final long serialVersionUID = 1L;

        public final int x;
        public final int y;
        public final Token.Type type;

        public FailedToPlaceToken(
            int x,
            int y,
            Token.Type type
        )
        {
            this.x = x;
            this.y = y;
            this.type = type;
        }
    }

    /**
     * We had an until action, but the level never ended.
     */
    public static class UntilActionNeverEnded extends ProblemRunningSolution
    {
        private static final long serialVersionUID = 1L;

        public final World.CompletionState expectedState;

        public UntilActionNeverEnded( World.CompletionState expectedState )
        {
            this.expectedState = expectedState;
        }
    }

    /**
     * Some other exception was thrown.
     */
    public static class UnknownProblem extends ProblemRunningSolution
    {
        private static final long serialVersionUID = 1L;

        public UnknownProblem( Throwable cause )
        {
            super( cause );
        }
    }
}
