package rabbitescape.engine.menu;

public class MenuTargets
{
    public static Menu levels(
        String levelsDir, LevelsCompleted levelsCompleted )
    {
        return new LevelsMenu( levelsDir, levelsCompleted );
    }
}
