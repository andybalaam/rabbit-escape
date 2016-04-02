package rabbitescape.engine.menu;

import java.util.List;
import java.util.Map;

public interface LevelsList
    extends Iterable<Map.Entry<String, List<LevelsList.LevelInfo>>>
{
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

    List<LevelInfo> inDir( String levelsDir );
}
