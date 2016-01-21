package rabbitescape.render;

import java.util.List;

import rabbitescape.engine.World;
import rabbitescape.engine.util.Dimension;
import rabbitescape.engine.util.Position;

/**
 * Draws things to mark the void beyond the world's edge.
 */
public class VoidBringer
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
    };

    private final static String[] left_torn = {
        "void_torn_left_1",
        "void_torn_left_2",
    };

    private final static String[] right_torn = {
        "void_torn_right_1",
        "void_torn_right_2",
    };

    private final static String[] corner_torn = {
        "void_torn_top_left",
        "void_torn_top_right",
        "void_torn_bottom_right",
        "void_torn_bottom_left"
    };

    private final static String[] bottom_torn = {
        "void_torn_bottom_1",
    };

    private final static String[] horz_lead_diag_hatching = {
        "void_hatching_horz_lead_diag_1",
        "void_hatching_horz_lead_diag_2",
    };

    private final static String[] horz_trail_diag_hatching = {
        "void_hatching_horz_trail_diag_1",
        "void_hatching_horz_trail_diag_2",
    };

    private final static String[] vert_lead_diag_hatching = {
        "void_hatching_vert_lead_diag_1",
        "void_hatching_vert_lead_diag_2",
    };

    private final static String[] vert_trail_diag_hatching = {
        "void_hatching_vert_trail_diag_1",
        "void_hatching_vert_trail_diag_2",
    };

    public enum Style
    {
        HIGHLIGHTER,
        TORN_PAPER,
        HATCHING
    }

    /**
     * Adds sprites to mark the void.
     * @param sprites  The list of sprites to add to.
     */
    public static void mark( World world, List<Sprite> sprites, Style style )
    {
        torn( world, sprites );
        return;
//        switch( style )
//        {
//        case HIGHLIGHTER:
//            highlighter( world, sprites );
//            return;
//        case HATCHING:
//            hatching( world, sprites );
//        default:
//            mark( world, sprites, Style.HATCHING );
//            return;
//        }
    }

    public static void mark( World world, List<Sprite> sprites )
    {
        int n = Style.values().length;
        int i = (int)Math.floor( ( Math.random() * n ) );
        mark( world, sprites, Style.values()[i] );
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
        row( new Position( 0, world.size.height ), new Position( 1, 0 ), world.size.width,
            new Position( 0, 0 ),
            horz_highlighter, sprites
            );
        // Right
        row( new Position( world.size.width, 0 ), new Position( 0, 1 ), world.size.height,
            new Position( 0, 0 ),
            vert_highlighter, sprites
           );
        corners( world.size, new Position( 16, 16 ), corner_highlighter, sprites );
    }

    private static void torn( World world, List<Sprite> sprites )
    {
        // Top
        row( new Position( 0, -2 ), new Position( 1, 0 ), world.size.width,
             new Position( 0, 0 ),
             top_torn, sprites
             );
       // Bottom
        row( new Position( 0, world.size.height ), new Position( 1, 0 ), world.size.width,
             new Position( 0, 0 ),
             bottom_torn, sprites
             );
        // Left
        row( new Position( -2, 0 ), new Position( 0, 1 ), world.size.height,
            new Position( 0, 0 ),
            left_torn, sprites
            );
        // Right
        row( new Position( world.size.width, 0 ), new Position( 0, 1 ), world.size.height,
            new Position( 0, 0 ),
            right_torn, sprites
            );
        corners( world.size, new Position( 64, 64 ), corner_torn, sprites );

    }

    private static void hatching( World world, List<Sprite> sprites )
    {
        // Top leading
        row( new Position( -2, -2 ), new Position( 1, 0 ), world.size.width,
             new Position( 0, 0 ),
             horz_lead_diag_hatching, sprites
             );
        // Top trailing
        row( new Position( 0, -2 ), new Position( 1, 0 ), world.size.width,
             new Position( 0, 0 ),
             horz_trail_diag_hatching, sprites
             );
        // Left leading
        row( new Position( -2, -2 ), new Position( 0, 1 ), world.size.height,
             new Position( 0, 16 ),
             vert_lead_diag_hatching, sprites
             );
        // Left trailing
        row( new Position( -2, 0 ), new Position( 0, 1 ), world.size.height,
             new Position( 0, 0 ),
             vert_trail_diag_hatching, sprites
             );
        // Right leading
        row( new Position( world.size.width, 0 ), new Position( 0, 1 ), world.size.height,
             new Position( 0, 0 ),
             vert_lead_diag_hatching, sprites
             );
        // Right trailing
        row( new Position( world.size.width, -2 ), new Position( 0, 1 ), world.size.height,
             new Position( 0, 0 ),
             vert_trail_diag_hatching, sprites
             );
        // Bottom leading
        row( new Position( 0, world.size.height ), new Position( 1, 0 ), world.size.width,
             new Position( 0, 0 ),
             horz_lead_diag_hatching, sprites
             );
        // Bottom trailing
        row( new Position( -2, world.size.height ), new Position( 1, 0 ), world.size.width,
             new Position( 0, 0 ),
             horz_trail_diag_hatching, sprites
             );
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
