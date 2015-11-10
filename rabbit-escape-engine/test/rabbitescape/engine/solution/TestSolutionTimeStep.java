package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class TestSolutionTimeStep
{
    @Test
    public void Identical_empty_timesteps_are_equal()
    {
        SolutionTimeStep ts1 = new SolutionTimeStep();
        SolutionTimeStep ts2 = new SolutionTimeStep();

        assertThat( ts1, equalTo( ts2 ) );

        assertThat( ts1.hashCode(), equalTo( ts2.hashCode() ) );
    }

    @Test
    public void Identical_nonempty_timesteps_are_equal()
    {
        SolutionTimeStep ts1 = new SolutionTimeStep(
            new WaitAction( 3 ), new SelectAction( Token.Type.bridge )
        );

        SolutionTimeStep ts2 = new SolutionTimeStep(
            new WaitAction( 3 ), new SelectAction( Token.Type.bridge )
        );

        assertThat( ts1, equalTo( ts2 ) );

        assertThat( ts1.hashCode(), equalTo( ts2.hashCode() ) );
    }


    @Test
    public void Different_action_lists_make_them_unequal()
    {
        SolutionTimeStep s1 = makeSolutionTimeStep( 3 );
        SolutionTimeStep s2 = makeShortSolutionTimeStep( 3 );

        assertThat( s1, not( equalTo( s2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat( s1.hashCode(), not( equalTo( s2.hashCode() ) ) );
    }

    @Test
    public void Different_actions_make_them_unequal()
    {
        SolutionTimeStep s1 = makeSolutionTimeStep( 3 );
        SolutionTimeStep s2 = makeSolutionTimeStep( 2 );

        assertThat( s1, not( equalTo( s2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat( s1.hashCode(), not( equalTo( s2.hashCode() ) ) );
    }

    // ---

    private static SolutionTimeStep makeSolutionTimeStep( int waitTime )
    {
        return new SolutionTimeStep(
              new WaitAction( waitTime )
            , new PlaceTokenAction( 3, 2 )
            , new SelectAction( Token.Type.block )
            , new AssertStateAction( World.CompletionState.RUNNING )
        );
    }

    private static SolutionTimeStep makeShortSolutionTimeStep( int waitTime )
    {
        return new SolutionTimeStep(
              new WaitAction( waitTime )
            , new PlaceTokenAction( 3, 2 )
            , new SelectAction( Token.Type.block )
        );
    }
}
