package rabbitescape.engine.menu;

import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.util.Util.ReadingResourceFailed;

public class LevelsMenu extends Menu
{
    public static class ErrorLoadingLevelList extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public ErrorLoadingLevelList( Throwable cause )
        {
            super( cause );
        }
    }

    public LevelsMenu( String levelsDir )
    {
        super( "Choose a level:", menuItems( levelsDir ) );
    }

    public static MenuItem[] menuItems( String levelsDir )
    {
        String[] levelFileNames = levelsInResource(
            "/rabbitescape/levels/" + levelsDir + "/ls.txt" );

        MenuItem[] ret = new MenuItem[ levelFileNames.length ];

        int i = 0;
        for ( String fileName : levelFileNames )
        {
            ret[i] = new LevelMenuItem(
                levelsDir + "/" + fileName + ".rel", i + 1 );

            ++i;
        }

        return ret;
    }

    private static String[] levelsInResource( String lsResourceName )
    {
        try
        {
            return stringArray(
                map(
                    stripLast( 4 ),
                    filter(
                        endsWith( ".rel" ),
                        resourceLines( lsResourceName )
                    )
                )
            );
        }
        catch ( ReadingResourceFailed e )
        {
            throw new ErrorLoadingLevelList( e );
        }
    }
}
