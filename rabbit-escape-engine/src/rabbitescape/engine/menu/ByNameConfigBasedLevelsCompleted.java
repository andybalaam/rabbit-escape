package rabbitescape.engine.menu;

import static rabbitescape.engine.config.ConfigKeys.*;

import java.util.Locale;
import java.util.Set;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.err.RabbitEscapeException;

public class ByNameConfigBasedLevelsCompleted implements LevelsCompleted
{
    public static class EmptyLevelName extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;
    }

    private static final Locale en_UK = Locale.UK;

    private final Config config;
    private final LevelsList levelsList;

    // ---

    public ByNameConfigBasedLevelsCompleted(
        Config config, LevelsList levelsList
    )
    {
        this.config = config;
        this.levelsList = levelsList;
    }

    @Override
    public int highestLevelCompleted( String levelsDir )
    {
        Set<String> completed = ConfigTools.getSet(
            config, CFG_LEVELS_COMPLETED, String.class );

        int i = 0;
        for ( String name : levelsList.inDir( stripNumber_( levelsDir ) ) )
        {
            if ( !completed.contains( name ) )
            {
                break;
            }
            ++i;
        }

        return i;
    }

    @Override
    public void setCompletedLevel( String levelsDir, int levelNum )
    {
        Set<String> completed = ConfigTools.getSet(
            config, CFG_LEVELS_COMPLETED, String.class );

        String newlyCompleted =
            levelsList.inDir( levelsDir ).get( levelNum - 1 );

        if ( !completed.contains( newlyCompleted ) )
        {
            completed.add( newlyCompleted );
            ConfigTools.setSet( config, CFG_LEVELS_COMPLETED, completed );
            config.save();
        }
    }

    // ---

    public static String canonicalName( String name )
    {
        if ( name.isEmpty() )
        {
            throw new EmptyLevelName();
        }
        else
        {
            return name.toLowerCase( en_UK ).replaceAll( "[^a-z0-9]", "_" );
        }
    }

    /**
     * Strips digits and underscores.
     */
    public static String stripNumber_( String levelsDir )
    {
        return levelsDir.replaceAll( "[0-9_]", "" );
    }
}
