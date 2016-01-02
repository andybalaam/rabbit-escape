package rabbitescape.render;

import java.net.URL;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.render.AnimationLoader;

public class TestAnimations
{
    @Test
    public void States_must_have_animations_and_frames_must_have_images()
    {
        for ( State s: State.values() )
        {
            String reaName = s.toString().toLowerCase();
            if ( reaName.equals( "nothing" ) )
            { // Special case: no rea required.
                continue;
            }
            // Exception here if an animation is missing.
            Animation a = AnimationLoader.load( reaName );

            checkFramesExist( reaName, a );
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
