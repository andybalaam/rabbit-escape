package rabbitescape.engine.menu;

import java.util.List;

public interface LevelsList
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
    }

    List<LevelInfo> inDir( String levelsDir );
}
