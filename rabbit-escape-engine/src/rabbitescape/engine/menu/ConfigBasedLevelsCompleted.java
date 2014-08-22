package rabbitescape.engine.menu;

import rabbitescape.engine.config.Config;


public class ConfigBasedLevelsCompleted implements LevelsCompleted
{
    public ConfigBasedLevelsCompleted( Config config )
    {
    }

    @Override
    public int highestLevelCompleted( String levelsDir )
    {
        return 0;
    }

    @Override
    public void setCompletedLevel( String levelsDir, int levelNum )
    {
    }
}
