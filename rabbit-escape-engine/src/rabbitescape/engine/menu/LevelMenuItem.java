package rabbitescape.engine.menu;

import static rabbitescape.engine.util.Util.*;

public class LevelMenuItem extends MenuItem
{
    public final String fileName;

    public LevelMenuItem( String fileName, int number )
    {
        super(
            "Level ${number}",
            Type.LEVEL,
            newMap( "number", String.valueOf( number ) )
        );

        this.fileName = fileName;
    }

}
