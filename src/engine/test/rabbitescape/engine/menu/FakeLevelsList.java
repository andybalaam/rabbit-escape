package rabbitescape.engine.menu;

import java.util.ArrayList;
import java.util.List;

public class FakeLevelsList
{
    public static LevelsList.LevelSetInfo levelSet( String dirName, int num )
    {
        return new LevelsList.LevelSetInfo(
            null, dirName, levelNames( dirName, num ), false );
    }

    private static List<LevelsList.LevelInfo> levelNames( String name, int num )
    {
        List<LevelsList.LevelInfo> ret = new ArrayList<LevelsList.LevelInfo>();

        for ( int i = 1; i <= num; ++i )
        {
            ret.add( new LevelsList.LevelInfo(
                "lev" + i, "LeVeL " + name + " " + i ) );
        }

        return ret;
    }
}
