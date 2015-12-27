package rabbitescape.ui.text;

import rabbitescape.engine.IgnoreWorldStatsListener;
import rabbitescape.engine.LoadWorldFile;
import rabbitescape.engine.World;
import rabbitescape.engine.solution.Solution;
import rabbitescape.engine.solution.SolutionParser;
import rabbitescape.engine.solution.SolutionRunner;
import rabbitescape.engine.util.RealFileSystem;


public class SolutionDemo
{
    
    public SolutionDemo( String path, int solutionNumber )
    {
        World world = new LoadWorldFile(
            new RealFileSystem() ).load(
                new IgnoreWorldStatsListener(), path );
        
        String solutionString = world.solutions[ solutionNumber - 1 ];

        Solution solution = SolutionParser.parse( solutionString );
        
        SolutionRunner.runSolution( solution, world, false );
    }

}
