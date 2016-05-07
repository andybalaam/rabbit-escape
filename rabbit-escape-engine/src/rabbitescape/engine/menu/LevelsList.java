package rabbitescape.engine.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * List of all level sets in the game, sorted by directory name.
 */
public class LevelsList implements Iterable<LevelsList.LevelSetInfo>
{
    public static class LevelSetInfo
    {
        public final String name;
        public final String dirName;
        public final List<LevelInfo> levels;
        public final boolean hidden;

        public LevelSetInfo(
            String name, String dirName,
            List<LevelInfo> levels, boolean hidden )
        {
            this.name = name;
            this.dirName = dirName;
            this.levels = levels;
            this.hidden = hidden;
        }
        
        public LevelSetInfo(
            String name, String dirName,
            List<LevelInfo> levels )
        {
            this( name, dirName, levels, false );
        }
    }

    public static class LevelInfo
    {
        public final String fileName;
        public final String name;

        public LevelInfo( String fileName, String name )
        {
            this.fileName = fileName;
            this.name = name;
        }

        @Override
        public String toString()
        {
            return String.format(
                "LevelInfo(\"%s\",\"%s\")",
                fileName,
                name
            );
        }

        @Override
        public boolean equals( Object objOther )
        {
            if ( objOther.getClass() != getClass() )
            {
                return false;
            }
            LevelInfo other = (LevelInfo)objOther;

            return (
                   other.name.equals( name )
                && other.fileName.equals( fileName )
            );
        }

        @Override
        public int hashCode()
        {
            return 31 * name.hashCode() + fileName.hashCode();
        }
    }

    // ---

    private final SortedMap<String, LevelSetInfo> levelSets;

    public LevelsList( LevelSetInfo... levelSets )
    {
        this( Arrays.asList( levelSets ) );
    }

    public static LevelsList excludingHidden( LevelsList levelsList )
    {
        List<LevelSetInfo> ret = new ArrayList<LevelSetInfo>();
        for ( LevelSetInfo i : levelsList )
        {
            if ( !i.hidden )
            {
                ret.add( i );
            }
        }
        return new LevelsList( ret );
    }

    public LevelsList( List<LevelSetInfo> levelSets )
    {
        this.levelSets = new TreeMap<String, LevelSetInfo>();
        for ( LevelSetInfo set : levelSets )
        {
            this.levelSets.put( set.dirName, set );
        }
    }

    public List<LevelInfo> inDir( String levelsDir )
    {
        return levelSets.get( levelsDir ).levels;
    }

    public int size()
    {
        return levelSets.size();
    }

    @Override
    public Iterator<LevelSetInfo> iterator()
    {
        return levelSets.values().iterator();
    }
}
