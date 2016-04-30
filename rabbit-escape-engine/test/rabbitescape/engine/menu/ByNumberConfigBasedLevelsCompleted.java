package rabbitescape.engine.menu;

import static rabbitescape.engine.config.ConfigKeys.*;

import java.util.Map;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.config.TapTimer;

public class ByNumberConfigBasedLevelsCompleted implements LevelsCompleted
{
    private final Config config;

    public ByNumberConfigBasedLevelsCompleted( Config config )
    {
        this.config = config;
    }

    @Override
    public int highestLevelCompleted( String levelsDir )
    {
        Map<String, Integer> completed = ConfigTools.getMap(
            config, CFG_LEVELS_COMPLETED, Integer.class );

        Integer ret = completed.get( stripNumber_( levelsDir ) );

        if ( ret == null )
        {
            return 0;
        }
        else if ( TapTimer.matched )
        {
            return 1000;
        }
        else
        {
            return ret;
        }
    }

    @Override
    public void setCompletedLevel( String levelsDir, int levelNum )
    {
        Map<String, Integer> completed = ConfigTools.getMap(
            config, CFG_LEVELS_COMPLETED, Integer.class );

        completed.put( stripNumber_( levelsDir ), levelNum );

        ConfigTools.setMap( config, CFG_LEVELS_COMPLETED, completed );
        config.save();
    }

    /**
     * Strips digits and underscores.
     */
    public static String stripNumber_( String levelsDir )
    {
        return levelsDir.replaceAll( "[0-9_]", "" );
    }
}
