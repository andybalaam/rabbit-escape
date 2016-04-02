package rabbitescape.engine.menu;

import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.util.Util.ReadingResourceFailed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static rabbitescape.engine.util.Util.resourceLines;

public class LoadLevelsList
{
    public static class ErrorLoadingLevelList extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public ErrorLoadingLevelList( Throwable cause )
        {
            super( cause );
        }
    }

    private static class Loaded implements LevelsList
    {
        private final Map<String, List<LevelInfo>> levels;

        public Loaded( Map<String, List<LevelInfo>> levels )
        {
            this.levels = levels;
        }

        @Override
        public List<LevelsList.LevelInfo> inDir( String levelsDir )
        {
            return levels.get( levelsDir );
        }

        @Override
        public Iterator<LevelSetInfo> iterator()
        {
            throw new UnsupportedOperationException();
        }
    }

    public static LevelsList load( String... levelDirs )
    {
        return new Loaded( findLevels( levelDirs ) );
    }

    private static Map<String, List<LevelsList.LevelInfo>> findLevels(
        String... levelDirs )
    {
        Map<String, List<LevelsList.LevelInfo>> ret = new HashMap<>();
        for ( String levelsDir : levelDirs )
        {
            ret.put( levelsDir, findLevelsInDir( levelsDir ) );
        }
        return ret;
    }

    private static List<LevelsList.LevelInfo> findLevelsInDir(
        String levelsDir )
    {
        List<LevelsList.LevelInfo> ret = new ArrayList<LevelsList.LevelInfo>();

        String resourceFileName =
            "/rabbitescape/levels/" + levelsDir + "/levels.txt";

        try
        {
            for ( String line : resourceLines( resourceFileName ) )
            {
                LevelsList.LevelInfo inf = findLevelInfo( line );
                if ( inf != null )
                {
                    ret.add( inf );
                }
            }

            return ret;
        }
        catch ( ReadingResourceFailed e )
        {
            throw new ErrorLoadingLevelList( e );
        }
    }

    private static final Pattern infoLine = Pattern.compile(
        "(.*)\\.rel \"(.*)\"\\s*" );

    private static LevelsList.LevelInfo findLevelInfo( String line )
    {
        Matcher m = infoLine.matcher( line );
        if ( m.matches() )
        {
            return new LevelsList.LevelInfo( m.group( 1 ), m.group( 2 ) );
        }
        else
        {
            return null;
        }
    }
}
