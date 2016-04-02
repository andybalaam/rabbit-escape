package rabbitescape.engine.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class FakeLevelsList implements LevelsList
{
    public static class LevelSet
    {
        public final String name;
        public final List<LevelsList.LevelInfo> levels;

        public LevelSet( String name, List<LevelsList.LevelInfo> levels )
        {
            this.name = name;
            this.levels = levels;
        }
    }

    public static LevelSet levelSet( String name, int num )
    {
        return new LevelSet( name, levelNames( name, num ) );
    }

    private static List<LevelsList.LevelInfo> levelNames( String name, int num )
    {
        List<LevelInfo> ret = new ArrayList<LevelInfo>();

        for ( int i = 1; i <= num; ++i )
        {
            ret.add( new LevelInfo( "lev" + i, "LeVeL " + name + " " + i ) );
        }

        return ret;
    }
    private final HashMap<String, List<LevelInfo>> levelSets;

    public FakeLevelsList( LevelSet... provided )
    {
        levelSets = new HashMap<String, List<LevelInfo>>();

        for ( LevelSet s : provided )
        {
            levelSets.put( s.name, s.levels );
        }
    }

    @Override
    public List<LevelInfo> inDir( String levelsDir )
    {
        return levelSets.get( levelsDir );
    }

    @Override
    public Iterator<Entry<String, List<LevelInfo>>> iterator()
    {
        throw new UnsupportedOperationException();
    }
}
