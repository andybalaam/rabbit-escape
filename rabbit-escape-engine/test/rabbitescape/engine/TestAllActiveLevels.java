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
import rabbitescape.engine.solution.SolutionFactory;
import rabbitescape.engine.util.FileSystem;

public class TestAllActiveLevels
{
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

                int i = 0;
                for ( String s : world.solutions )
                {
                    Solution solution = SolutionFactory.create( s, i );
                    solution.checkSolution( world );
                    ++i;
                }
            }
        }
    }
}
