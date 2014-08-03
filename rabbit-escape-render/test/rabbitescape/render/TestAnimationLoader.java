package rabbitescape.render;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.util.Util.*;

import org.junit.Test;

public class TestAnimationLoader
{
    @Test
    public void Parse_just_frame_names() throws Exception
    {
        ByteArrayInputStream anim = animationAsStream(
            new String[] {
                "framez1",
                "framey2",
                "framex3",
            }
        );

        FrameNameAndOffset[] frames = AnimationLoader.readAnimation( anim );

        assertThat(
            animationToString( frames ),
            equalTo(
                new String[] {
                    "framez1 0 0",
                    "framey2 0 0",
                    "framex3 0 0"
                }
            )
        );
    }

    @Test
    public void Parse_names_and_offsets() throws Exception
    {
        ByteArrayInputStream anim = animationAsStream(
            new String[] {
                "framez1 1 2",
                "framey2",
                "framex3 55 66",
            }
        );

        FrameNameAndOffset[] frames = AnimationLoader.readAnimation( anim );

        assertThat(
            animationToString( frames ),
            equalTo(
                new String[] {
                    "framez1 1 2",
                    "framey2 0 0",
                    "framex3 55 66"
                }
            )
        );
    }

    private ByteArrayInputStream animationAsStream( String[] animation )
        throws UnsupportedEncodingException
    {
        return new ByteArrayInputStream(
            join( "\n", animation ).getBytes( "UTF8" ) );
    }

    private String[] animationToString( FrameNameAndOffset[] frames )
    {
        String[] ret = new String[ frames.length ];

        for ( int i = 0; i < frames.length; ++i )
        {
            ret[i] = frameToString( frames[i] );
        }

        return ret;
    }

    private String frameToString( FrameNameAndOffset frame )
    {
        return frame.name + " " + frame.offsetX + " " + frame.offsetY;
    }
}
