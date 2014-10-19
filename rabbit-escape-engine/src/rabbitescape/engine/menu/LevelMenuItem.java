package rabbitescape.engine.menu;

import static rabbitescape.engine.util.Util.*;

public class LevelMenuItem extends MenuItem
{
    public final String fileName;
    public final int levelNumber;

    public LevelMenuItem( String fileName, int number, boolean enabled )
    {
        super(
            "Level ${number}",
            Type.LEVEL,
            newMap( "number", String.valueOf( number ) ),
            enabled
        );

        this.fileName = fileName;
        this.levelNumber = number;
    }

}
