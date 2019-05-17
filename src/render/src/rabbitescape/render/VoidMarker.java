package rabbitescape.render;

import java.util.List;

import static rabbitescape.engine.VoidMarkerStyle.Style;
import rabbitescape.engine.World;
import rabbitescape.engine.util.Dimension;
import rabbitescape.engine.util.Position;

/**
 * Draws things to mark the void beyond the world's edge.
 */
public class VoidMarker
{
    private final static String[] vert_highlighter = {
        "void_highlighter_vert_dash_1",
        "void_highlighter_vert_dash_2",
        "void_highlighter_vert_dash_3",
        "void_highlighter_vert_dash_4"
    };

    private final static String[] horz_highlighter = {
        "void_highlighter_horz_dash_1",
        "void_highlighter_horz_dash_2",
        "void_highlighter_horz_dash_3",
        "void_highlighter_horz_dash_4"
    };

    private final static String[] corner_highlighter = {
        "void_highlighter_corner_dash_1",
        "void_highlighter_corner_dash_2",
        "void_highlighter_corner_dash_3",
        "void_highlighter_corner_dash_4"
    };

    private final static String[] top_torn = {
        "void_torn_top_1",
        "void_torn_top_2",
        "void_torn_top_3",
        "void_torn_top_4",
    };

    private final static String[] left_torn = {
        "void_torn_left_1",
        "void_torn_left_2",
        "void_torn_left_3",
        "void_torn_left_4",
    };

    private final static String[] right_torn = {
        "void_torn_right_1",
        "void_torn_right_2",
        "void_torn_right_3",
        "void_torn_right_4",
    };

    private final static String[] corner_torn = {
        "void_torn_top_left",
        "void_torn_top_right",
        "void_torn_bottom_right",
        "void_torn_bottom_left"
    };

    private final static String[] bottom_torn = {
        "void_torn_bottom_1",
        "void_torn_bottom_2",
        "void_torn_bottom_3",
        "void_torn_bottom_4",
    };



    /**
     * Adds sprites to mark the void.
     * @param sprites  The list of sprites to add to.
     */
    public static void mark( World world, List<Sprite> sprites, Style style )
    {
        switch( style )
        {
        case HIGHLIGHTER:
            highlighter( world, sprites );
            return ;
        case TORN_PAPER:
            torn( world, sprites );
            return ;
        default:
            mark( world, sprites, Style.HIGHLIGHTER );
            return;
        }
    }

    private static void highlighter( World world, List<Sprite> sprites )
    {
        // Top
        row( new Position( 0, -1 ), new Position( 1, 0 ), world.size.width,
             new Position( 0, 16 ),
             horz_highlighter, sprites
             );
        // Left
        row( new Position( -1, 0 ), new Position( 0, 1 ), world.size.height,
             new Position( 16, 0 ),
             vert_highlighter, sprites
            );
        // Bottom
        row( new Position( 0, world.size.height ),
             new Position( 1, 0 ), world.size.width,
             new Position( 0, 0 ),
             horz_highlighter, sprites
            );
        // Right
        row( new Position( world.size.width, 0 ),
             new Position( 0, 1 ), world.size.height,
             new Position( 0, 0 ),
             vert_highlighter, sprites
           );
        corners( world.size, new Position( 16, 16 ),
                 corner_highlighter, sprites );
    }

    private static void torn( World world, List<Sprite> sprites )
    {
        // Top
        row( new Position( 0, -2 ), new Position( 1, 0 ), world.size.width,
             new Position( 0, 0 ),
             top_torn, sprites
             );
       // Bottom
        row( new Position( 0, world.size.height ),
             new Position( 1, 0 ), world.size.width,
             new Position( 0, 0 ),
             bottom_torn, sprites
             );
        // Left
        row( new Position( -2, 0 ), new Position( 0, 1 ), world.size.height,
             new Position( 0, 0 ),
             left_torn, sprites
            );
        // Right
        row( new Position( world.size.width, 0 ),
             new Position( 0, 1 ), world.size.height,
             new Position( 0, 0 ),
             right_torn, sprites
            );
        corners( world.size, new Position( 64, 64 ), corner_torn, sprites );
    }

    private static void row( Position start, Position step, int n,
                             Position offset,
                             String[] bitmaps, List<Sprite> sprites )
    {
        int bmpIndex = 0;
        Position p = start;
        for ( int i = 0 ; i < n ; i++ )
        {
            sprites.add( new Sprite( bitmaps[bmpIndex++], null, p, offset ) );
            p = p.plus( step );
            bmpIndex = bmpIndex >= bitmaps.length ? 0 : bmpIndex;
        }
    }

    private static void corners( Dimension worldSize, Position offset,
                                 String[] bitmaps, List<Sprite> sprites )
    {
        int bmpIndex = 0;
        final Position[] positions = new Position[] {
            new Position( 0,               0 ), // Top left
            new Position( worldSize.width, 0 ), // Top right
            new Position( worldSize.width, worldSize.height ), // Bottom right
            new Position( 0,               worldSize.height ) // Bottom Left
        };
        final Position[] offsets = new Position[] {
            new Position( -offset.x, -offset.y ),
            new Position( 0,         -offset.y ),
            new Position( 0,         0         ),
            new Position( -offset.x, 0         )
        };
        for ( int i = 0 ; i < 4 ; i++ )
        {
            sprites.add( new Sprite( bitmaps[bmpIndex++], null,
                                     positions[i],
                                     offsets[i] ) );
            bmpIndex = bmpIndex >= bitmaps.length ? 0 : bmpIndex;

        }
    }
}
