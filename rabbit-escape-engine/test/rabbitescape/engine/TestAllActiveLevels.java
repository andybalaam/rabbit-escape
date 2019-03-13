package rabbitescape.engine;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import rabbitescape.engine.menu.*;
import rabbitescape.engine.solution.Solution;
import rabbitescape.engine.solution.SolutionExceptions;
import rabbitescape.engine.solution.SolutionExceptions.UnknownProblem;
import rabbitescape.engine.solution.SolutionParser;
import rabbitescape.engine.solution.SolutionRunner;
import rabbitescape.engine.util.FileSystem;

public class TestAllActiveLevels
{
    @Test
    public void All_official_levels_load_and_round_trip()
    {
        forEachOfficialLevel( assertItRoundTrips() );
    }

    @Test
    public void All_official_levels_work_and_have_winning_solutions()
    {
        forEachOfficialLevel( assertItsSolutionsWorkAndYouCanWin() );
    }

    @Test
    public void All_staging_levels_work_and_have_winning_solutions()
    {
        forEachStagingLevel( assertItsSolutionsWorkAndYouCanWin() );
    }

    @Test
    public void All_development_levels_work_and_any_solutions_are_correct()
    {
        forEachDevelopmentLevel( assertItsSolutionsWork() );
    }

    // @Test dejavu is now official - left as an example
    // public void All_dejavu_levels_work_and_any_solutions_are_correct()
    // {
    //     forEachUnofficialLevel( assertItsSolutionsWork(), "07_dejavu" );
    // }

    @Test
    public void All_official_levels_have_unique_names()
    {
        final Set<String> names = new HashSet<String>();

        forEachOfficialLevel( new T() {
            @Override public void run( World world, String fileName )
            {
                String name = ByNameConfigBasedLevelsCompleted.canonicalName(
                    world.name );

                assertThat(
                    "Level name is not unique",
                    names,
                    not( hasItem( name ) )
                );

                names.add( name );
            }
        } );
    }

    @Test
    public void All_official_levels_have_hints_and_descriptions()
    {
        forEachOfficialLevel( new T() {
            @Override public void run( World world, String fileName )
            {
                assertThat(
                    "Description is empty",
                    world.description,
                    not( equalTo( "" ) )
                );
                assertThat(
                    "Author name is empty",
                    world.author_name,
                    not( equalTo( "" ) )
                );
                assertThat(
                    "Music is empty",
                    world.music,
                    not( equalTo( "" ) )
                );
                assertThat(
                    "There are not 3 hints",
                    world.hints.length,
                    equalTo( 3 )
                );
                assertThat(
                    "A hint is empty",
                    world.hints[ 0 ],
                    not( equalTo( "" ) )
                );
                assertThat(
                    "A hint is empty",
                    world.hints[ 1 ],
                    not( equalTo( "" ) )
                );
                assertThat(
                    "A hint is empty",
                    world.hints[ 2 ],
                    not( equalTo( "" ) )
                );
            }
        } );
    }

    // --

    private T assertItRoundTrips()
    {
        return new T() {
            @Override public void run( World world, String filename )
        {

            String[] lines = renderCompleteWorld( world, true );

            assertThat(
                renderCompleteWorld( createWorld( lines ), true ),
                equalTo( lines )
            );

        } };
    }

    private T assertItsSolutionsWork()
    {
        return assertTheSolutionsWorkAndYouCanWin( false );
    }

    private T assertItsSolutionsWorkAndYouCanWin()
    {
        return assertTheSolutionsWorkAndYouCanWin( true );
    }

    private T assertTheSolutionsWorkAndYouCanWin( final boolean mustWin )
    {
        return new T() {
            @Override public void run( World world, String fileName )
        {

            boolean solved = false;
            int i = 1;
            for ( String s : world.solutions )
            {
                boolean thisS = runSolutionString( world, fileName, i, s );
                if ( thisS )
                {
                    solved = true;
                }
                ++i;
            }

            if ( mustWin && !solved )
            {
                throw new AssertionError(
                    "Level " + fileName + " has no solution!" );
            }

        } };
    }

    private void forEachOfficialLevel( T test )
    {
        for ( LevelsList.LevelSetInfo set :
            LoadLevelsList.load( MenuDefinition.allLevels ) )
        {
            if ( set.hidden )
            {
                continue;
            }
            for ( LevelsList.LevelInfo level : set.levels )
            {
                try
                {
                    World world = new LoadWorldFile( new NothingExistsFileSystem() )
                    .load(
                        new IgnoreWorldStatsListener(),
                        set.dirName + "/" + level.fileName + ".rel"
                    );

                    test.run( world, level.fileName );
                }
                catch (AssertionError e) {
                    throw new AssertionError(
                        "Level " + level.fileName + " failed.",
                        e
                    );
                }
            }
        }
    }

    private void forEachStagingLevel( T test )
    {
        forEachUnofficialLevel( test, "staging" );
    }

    private void forEachDevelopmentLevel( T test )
    {
        forEachUnofficialLevel( test, "development" );
    }

    private void forEachUnofficialLevel( T test, String levelsDir )
    {
        LevelsList levelsList = LoadLevelsList.load(
            new LevelsList(
                new LevelsList.LevelSetInfo( null, levelsDir, null, false )
            )
        );

        for ( LevelsList.LevelSetInfo set : levelsList )
        {
            for ( LevelsList.LevelInfo level : set.levels )
            {
                World world = new LoadWorldFile( new NothingExistsFileSystem() )
                    .load(
                        new IgnoreWorldStatsListener(),
                        levelsDir + "/" + level.fileName + ".rel"
                    );

                test.run( world, level.fileName );
            }
        }
    }

    private boolean runSolutionString(
        World world,
        String worldFileName,
        int solutionId,
        String solutionString
    )
    {
        try
        {
            Solution solution = SolutionParser.parse( solutionString );
            return SolutionRunner.runSolution( solution, world );
        }
        catch ( SolutionExceptions.ProblemRunningSolution e )
        {
            e.solutionId = solutionId;
            e.level = worldFileName;
            throw e;
        }
        catch ( Throwable e )
        {
            UnknownProblem u = new SolutionExceptions.UnknownProblem( e );
            u.solutionId = solutionId;
            u.level = worldFileName;
            throw u;
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

    private static interface T
    {
        void run( World world, String filename );
    }
}
