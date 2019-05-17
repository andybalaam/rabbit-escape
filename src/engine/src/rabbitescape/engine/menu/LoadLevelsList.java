package rabbitescape.engine.menu;

import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.menu.LevelsList.LevelSetInfo;
import rabbitescape.engine.util.Util.ReadingResourceFailed;

import java.util.ArrayList;
import java.util.List;
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

    public static LevelsList load( LevelsList emptyLevelSets )
    {
        return new LevelsList( findLevels( emptyLevelSets ) );
    }

    private static List<LevelsList.LevelSetInfo> findLevels(
        LevelsList emptyLevelSets )
    {
        List<LevelsList.LevelSetInfo> ret = new ArrayList<>();
        for ( LevelSetInfo set : emptyLevelSets )
        {
            ret.add(
                new LevelsList.LevelSetInfo(
                    set.name,
                    set.dirName,
                    findLevelsInDir( set.dirName ),
                    set.hidden
                )
            );
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
