package rabbitescape.engine.menu;

import static rabbitescape.engine.util.Util.*;

public class LevelMenuItem extends MenuItem
{
    public final String fileName;
    public final String levelsDir;
    public final int levelNumber;
    public final String name;

    public LevelMenuItem(
        String fileName, String levelsDir, int number, boolean enabled,
        String levelName )
    {
        super(
            "${number} " + levelName,
            Type.LEVEL,
            newMap( "number", String.valueOf( number ) ),
            enabled
        );

        this.fileName = fileName;
        this.levelsDir = levelsDir;
        this.levelNumber = number;
        this.name = levelName;
    }

}
