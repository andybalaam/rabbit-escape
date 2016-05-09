package rabbitescape.engine.solution;

import rabbitescape.engine.World;
import rabbitescape.engine.solution.Solution;
import rabbitescape.engine.solution.SolutionParser;
import rabbitescape.engine.util.MegaCoder;


public class SolutionDemo
{
    public final String solnChars =
        "[0-9a-z\\(\\);,&]*";

    public final Solution solution;

    /**
     * @param path           The path of the .rel file.
     * @param solutionString A string which may contain the solution
     *                       commamnds or the number of the solution
     *                       in the .rel file.
     * @param genTest        If true the output is in a format useful
     *                       for copying into Java unit test source.
     */
    public SolutionDemo( String solutionCommandLine, World world )
    {

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

        this.solution = SolutionParser.parse( solutionString );
    }

}
