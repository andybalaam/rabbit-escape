package rabbitescape.engine.menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LevelsList implements Iterable<LevelsList.LevelSetInfo>
{
    public static class LevelSetInfo
    {
        public final String name;
        public final String dirName;
        public final List<LevelInfo> levels;

        public LevelSetInfo(
            String name, String dirName, List<LevelInfo> levels )
        {
            this.name = name;
            this.dirName = dirName;
            this.levels = levels;
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

    private final Map<String, LevelSetInfo> levelSets;

    public LevelsList( LevelSetInfo... levelSets )
    {
        this( Arrays.asList( levelSets ) );
    }

    public LevelsList( List<LevelSetInfo> levelSets )
    {
        this.levelSets = new HashMap<String, LevelSetInfo>();
        for ( LevelSetInfo set : levelSets )
        {
            this.levelSets.put( set.dirName, set );
        }
    }

    public List<LevelInfo> inDir( String levelsDir )
    {
        return levelSets.get( levelsDir ).levels;
    }

    @Override
    public Iterator<LevelSetInfo> iterator()
    {
        return levelSets.values().iterator();
    }
}
