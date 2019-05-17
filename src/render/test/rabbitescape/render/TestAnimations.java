package rabbitescape.render;

import java.net.URL;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Rabbit.Type.*;
import static rabbitescape.engine.util.Util.*;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.Direction;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Thing;
import rabbitescape.engine.Token;
import rabbitescape.render.AnimationLoader;

public class TestAnimations
{
    @Test
    public void States_must_have_animations_and_frames_must_have_images()
    {
        Thing token = new Token( 2, 1, Token.Type.block );
        for ( State s: State.values() )
        {
            token.state = s;

            String reaName = token.stateName();
            if (
                reaName.equals( "nothing" )
                || reaName.equals( "water_region_empty" )
            )
            { // Special case: no rea required.
                continue;
            }

            // Exception here if an animation is missing.
            Animation a = AnimationLoader.load( reaName );

            checkFramesExist( reaName, a );
        }
    }

    @Test
    public void Rabbot_states_must_have_animations_and_frames_must_have_images()
    {
        Function<State, Boolean> isRabbitState = new Function<State, Boolean>()
        {
            @Override
            public Boolean apply( State t )
            {
                return t.name().startsWith( "RABBIT_" );
            }
        };

        Rabbit rabbit = new Rabbit( 1, 2, Direction.LEFT, RABBOT );

        for ( State s: filter(isRabbitState, list(State.values())) )
        {
            rabbit.state = s;

            // Exception here if an animation is missing.
            Animation a = AnimationLoader.load( rabbit.stateName() );

            checkFramesExist( rabbit.stateName(), a );
        }
    }

    private void checkFramesExist( String reaName, Animation a )
    {

        for ( Frame f: a )
        {
            String resourcePath =
                "/rabbitescape/ui/swing/images32/" +
                f.name + ".png";
            URL url = getClass().getResource( resourcePath );
            boolean fileExists = ( url != null );
            if ( !fileExists )
            {
                System.err.println( "Working Directory:"
                                   + System.getProperty( "user.dir" ) );
                System.err.println( "Missing frame:" + reaName + ":" + resourcePath );
            }
            assertThat( fileExists, is( true ) );
        }
    }
}
