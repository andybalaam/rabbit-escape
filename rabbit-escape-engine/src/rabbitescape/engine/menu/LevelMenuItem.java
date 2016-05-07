package rabbitescape.engine.menu;

import static rabbitescape.engine.util.Util.*;

public class LevelMenuItem extends MenuItem
{
    public final String fileName;
    public final String levelsDir;
    public final int levelNumber;

    public LevelMenuItem(
        String fileName, String levelsDir, int number, boolean enabled, boolean hidden )
    {
        super(
            "Level ${number}",
            Type.LEVEL,
            newMap( "number", String.valueOf( number ) ),
            enabled,
            hidden
        );

        this.fileName = fileName;
        this.levelsDir = levelsDir;
        this.levelNumber = number;
    }
    
    public LevelMenuItem(
        String fileName, String levelsDir, int number, boolean enabled )
    {
        this( fileName, levelsDir, number, enabled, false );
    }

}
