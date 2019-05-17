package rabbitescape.engine;

import rabbitescape.engine.menu.LevelsCompleted;

public class CompletedLevelWinListener implements LevelWinListener
{
    private final String levelsDir;
    private final int levelNumber;
    private final LevelsCompleted levelsCompleted;

    public CompletedLevelWinListener(
        String levelsDir,
        int levelNumber,
        LevelsCompleted levelsCompleted
    )
    {
        this.levelsDir = levelsDir;
        this.levelNumber = levelNumber;
        this.levelsCompleted = levelsCompleted;
    }

    @Override
    public void won()
    {
        if ( levelsCompleted.highestLevelCompleted( levelsDir ) < levelNumber )
        {
            levelsCompleted.setCompletedLevel( levelsDir, levelNumber );
        }
    }

    @Override
    public void lost()
    {
    }
}
