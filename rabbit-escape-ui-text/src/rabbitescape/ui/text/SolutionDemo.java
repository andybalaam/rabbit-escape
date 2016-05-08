package rabbitescape.ui.text;

import rabbitescape.engine.IgnoreWorldStatsListener;
import rabbitescape.engine.LoadWorldFile;
import rabbitescape.engine.World;
import rabbitescape.engine.solution.Solution;
import rabbitescape.engine.solution.SolutionParser;
import rabbitescape.engine.solution.SolutionRunner;
import rabbitescape.engine.util.MegaCoder;
import rabbitescape.engine.util.RealFileSystem;


public class SolutionDemo
{
    public final String solnChars =
        "[0-9a-z\\(\\);,&]*";

    /**
     * @param path           The path of the .rel file.
     * @param solutionString A string which may contain the solution
     *                       commamnds or the number of the solution
     *                       in the .rel file.
     * @param genTest        If true the output is in a format useful
     *                       for copying into Java unit test source.
     */
    public SolutionDemo(
        String path, String solutionCommandLine, boolean genTest )
    {
        World world = new LoadWorldFile(
            new RealFileSystem() ).load(
                new IgnoreWorldStatsListener(), path );

        String solutionString;
        try
        {
            int solutionNumber = Integer.parseInt( solutionCommandLine ) - 1;
            solutionString = world.solutions[solutionNumber];
        }
        catch ( NumberFormatException e )
        {
            solutionString = solutionCommandLine;
            if ( !solutionString.matches( solnChars ) )
            {
                solutionString = MegaCoder.decode( solutionString );
            }
        }

        Solution solution = SolutionParser.parse( solutionString );

        SolutionRunner.runSolution( solution, world, System.out, genTest );
    }

}
