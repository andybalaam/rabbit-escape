package rabbitescape.engine.menu;

public interface LevelsCompleted
{
    public int highestLevelCompleted( String levelsDir );
    public void setCompletedLevel( String levelsDir, int levelNum );
}
