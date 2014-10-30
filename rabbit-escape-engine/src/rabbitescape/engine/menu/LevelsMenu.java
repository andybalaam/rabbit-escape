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

    private final String levelsDir;
    private final LevelsCompleted levelsCompleted;

    public LevelsMenu( String levelsDir, LevelsCompleted levelsCompleted )
    {
        super( "Choose a level:", menuItems( levelsDir, levelsCompleted ) );

        this.levelsDir = levelsDir;
        this.levelsCompleted = levelsCompleted;

        refresh();
    }

    private static MenuItem[] menuItems(
        String levelsDir, LevelsCompleted levelsCompleted )
    {
        String[] levelFileNames = levelsInResource(
            "/rabbitescape/levels/" + levelsDir + "/ls.txt" );

        MenuItem[] ret = new MenuItem[ levelFileNames.length ];

        int i = 1;
        for ( String fileName : levelFileNames )
        {
            ret[i-1] = new LevelMenuItem(
                levelsDir + "/" + fileName + ".rel",
                levelsDir,
                i,
                true
            );

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

    @Override
    public void refresh()
    {
        int i = 1;
        for ( MenuItem item : items )
        {
            item.enabled = (
                i <= levelsCompleted.highestLevelCompleted( levelsDir ) + 1 );

            ++i;
        }
    }
}
