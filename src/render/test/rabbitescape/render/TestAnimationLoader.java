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

        Animation animation = AnimationLoader.readAnimation( anim );

        assertThat(
            animationToString( animation ),
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

        Animation animation = AnimationLoader.readAnimation( anim );

        assertThat(
            animationToString( animation ),
            equalTo(
                new String[] {
                    "framez1 1 2",
                    "framey2 0 0",
                    "framex3 55 66"
                }
            )
        );
    }

    @Test
    public void Multiple_spaces_allowed_between_offsets() throws Exception
    {
        ByteArrayInputStream anim = animationAsStream(
            new String[] {
                "framez1  1  2",
                "framey2",
                "framex3 55      66",
            }
        );

        Animation animation = AnimationLoader.readAnimation( anim );

        assertThat(
            animationToString( animation ),
            equalTo(
                new String[] {
                    "framez1 1 2",
                    "framey2 0 0",
                    "framex3 55 66"
                }
            )
        );
    }

    @Test
    public void Offset_line_sets_all_offsets() throws Exception
    {
        ByteArrayInputStream anim = animationAsStream(
            new String[] {
                "offset 3 5",
                "framez1  1  2",
                "framey2",
                "framex3 55      66",
            }
        );

        Animation animation = AnimationLoader.readAnimation( anim );

        assertThat(
            animationToString( animation ),
            equalTo(
                new String[] {
                    "framez1 4 7",
                    "framey2 3 5",
                    "framex3 58 71"
                }
            )
        );
    }

    @Test
    public void Later_offset_line_accumulates() throws Exception
    {
        ByteArrayInputStream anim = animationAsStream(
            new String[] {
                "framez1  1  2",
                "offset 3 5",
                "framey2",
                "offset 0 10",
                "framex3 55      66",
            }
        );

        Animation animation = AnimationLoader.readAnimation( anim );

        assertThat(
            animationToString( animation ),
            equalTo(
                new String[] {
                    "framez1 1 2",
                    "framey2 3 5",
                    "framex3 58 81"
                }
            )
        );
    }

    @Test
    public void Filter_contents_of_ls_file_and_add_none()
    {
        assertThat(
            AnimationLoader.animationFilesInResource(
                "/rabbitescape/render/testls.txt" ),
            equalTo( new String[] { "<none>", "aa", "bb", "cc" } )
        );
    }

    // ---

    private ByteArrayInputStream animationAsStream( String[] animation )
        throws UnsupportedEncodingException
    {
        return new ByteArrayInputStream(
            join( "\n", animation ).getBytes( "UTF8" ) );
    }

    private String[] animationToString( Animation animation )
    {
        String[] ret = new String[ animation.size() ];

        for ( int i = 0; i < animation.size(); ++i )
        {
            ret[i] = frameToString( animation.get( i ) );
        }

        return ret;
    }

    private String frameToString( Frame frame )
    {
        return frame.name + " " + frame.offsetX + " " + frame.offsetY;
    }
}
