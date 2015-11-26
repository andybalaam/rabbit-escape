package rabbitescape.engine;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import rabbitescape.engine.menu.LevelMenuItem;
import rabbitescape.engine.menu.LevelsCompleted;
import rabbitescape.engine.menu.Menu;
import rabbitescape.engine.menu.MenuDefinition;
import rabbitescape.engine.menu.MenuItem;
import rabbitescape.engine.solution.Solution;
import rabbitescape.engine.solution.SolutionExceptions;
import rabbitescape.engine.solution.SolutionParser;
import rabbitescape.engine.solution.SolutionRunner;
import rabbitescape.engine.util.FileSystem;

public class TestAllActiveLevels
{
    @Test
    public void All_levels_load_and_round_trip()
    {
        Menu menu = MenuDefinition.mainMenu( new IgnoreLevelsCompleted() );
        Menu levelSets = menu.items[0].menu;
        for ( MenuItem levelSet : levelSets.items )
        {
            for ( MenuItem levelItem : levelSet.menu.items )
            {
                LevelMenuItem lev = (LevelMenuItem)levelItem;

                World world = new LoadWorldFile(
                    new NothingExistsFileSystem() ).load(
                        new IgnoreWorldStatsListener(), lev.fileName );

                String[] lines = renderCompleteWorld( world, true );

                assertThat(
                    renderCompleteWorld( createWorld( lines ), true ),
                    equalTo( lines )
                );
            }
        }
    }

    @Test
    public void All_solutions_are_correct()
    {
        Menu menu = MenuDefinition.mainMenu( new IgnoreLevelsCompleted() );
        Menu levelSets = menu.items[0].menu;
        for ( MenuItem levelSet : levelSets.items )
        {
            for ( MenuItem levelItem : levelSet.menu.items )
            {
                LevelMenuItem lev = (LevelMenuItem)levelItem;

                World world = new LoadWorldFile(
                    new NothingExistsFileSystem() ).load(
                        new IgnoreWorldStatsListener(), lev.fileName );

                int i = 1;
                for ( String s : world.solutions )
                {
                    runSolutionString( world, lev.fileName, i, s );
                    ++i;
                }
            }
        }
    }

    // --

    private void runSolutionString(
        World world,
        String worldFileName,
        int solutionId,
        String solutionString
    )
    {
        Solution solution = SolutionParser.parse( solutionString );
        try
        {
            SolutionRunner.runSolution( solution, world );
        }
        catch ( SolutionExceptions.ProblemRunningSolution e )
        {
            e.solutionId = solutionId;
            e.level = worldFileName;
            throw e;
        }
    }

    private static class IgnoreLevelsCompleted implements LevelsCompleted
    {
        @Override
        public int highestLevelCompleted( String levelsDir )
        {
            return 0;
        }

        @Override
        public void setCompletedLevel( String levelsDir, int levelNum )
        {
        }
    }

    private static class NothingExistsFileSystem implements FileSystem
    {
        @Override
        public boolean exists( String fileName )
        {
            return false;
        }

        @Override
        public String[] readLines( String fileName )
            throws FileNotFoundException, IOException
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public String read( String fileName )
            throws FileNotFoundException, IOException
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void write( String fileName, String contents ) throws IOException
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public String parent( String filePath )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void mkdirs( String dirPath )
        {
            throw new UnsupportedOperationException();
        }
    }
}
