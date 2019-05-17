package rabbitescape.engine.menu;

import static rabbitescape.engine.config.ConfigKeys.*;

import java.util.Locale;
import java.util.Set;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.util.Util;

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
        for ( LevelsList.LevelInfo level : levelsList.inDir( levelsDir ) )
        {
            if ( !completed.contains( canonicalName( level.name ) ) )
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

        LevelsList.LevelInfo newlyCompleted =
            levelsList.inDir( levelsDir ).get( levelNum - 1 );

        String completedName = canonicalName( newlyCompleted.name );

        if ( !completed.contains( completedName ) )
        {
            completed.add( completedName );
            ConfigTools.setSet( config, CFG_LEVELS_COMPLETED, completed );
            config.save();
        }
    }

    // ---

    public static String canonicalName( String name )
    {
        if ( Util.isEmpty( name ) )
        {
            throw new EmptyLevelName();
        }
        else
        {
            return name.toLowerCase( en_UK ).replaceAll( "[^a-z0-9]", "_" );
        }
    }
}
