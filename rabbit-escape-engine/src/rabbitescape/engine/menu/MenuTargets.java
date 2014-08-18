package rabbitescape.engine.menu;

public class MenuTargets
{
    public static Menu levels( String levelsDir )
    {
        return new LevelsMenu( levelsDir );
    }
}
