package rabbitescape.engine.menu;

public interface LevelsCompleted
{
    /**
     * @return 1-based number of the highest level completed (so a return
     *         value of 0 indicates no levels have been completed).
     */
    public int highestLevelCompleted( String levelsDir );

    /**
     * @param levelsDir
     * @param levelNum the 1-based level you just completed, so a value
     *                 of 1 means you have completed the first level.
     */
    public void setCompletedLevel( String levelsDir, int levelNum );
}
