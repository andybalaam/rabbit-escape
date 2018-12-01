package rabbitescape.engine.util;

import static rabbitescape.engine.util.Util.*;
import static rabbitescape.engine.i18n.Translation.*;

public class CommandLineOptionSet
{

    public static void parse( String[] args, CommandLineOption... options )
    {
        for ( int i = 0; i < args.length; i++ )
        {
            String arg = args[i];
            CommandLineOption o = match( arg, options );
            o.setPresent();
            try
            {
                // If param was not concatenated with option,
                // check along the array.
                if ( o.takesParam && o.getValue() == null )
                {
                    o.setValue( args[++i], arg );
                }
            }
            catch ( ArrayIndexOutOfBoundsException eAIOOB )
            {
                throw o.getOptionRequiresParameter( arg );
            }
        }
        // If we had --test and --template they would both be -t.
        // Ensure that kind of thing does not happen.
        for ( int i = 0; i < options.length ; i++ )
        {
            for ( int j = 0 ; j < i ; j++ )
            {
                reAssert( 
                    !options[i].shortForm.equals( options[j].shortForm ),
                    t( 
                        options[i].longForm + " and " + options[j].longForm +
                        " have the same short form: " + options[i].shortForm 
                    ) 
                );
            }
        }
    }

    private static CommandLineOption match( String arg,
                                            CommandLineOption[] options )
    {
        for ( CommandLineOption o: options )
        {
            if ( o.matches( arg ) )
            {
                return o;
            }
        }
        throw (
            new CommandLineOption( "--null", false ) ).getUnknownOption( arg );
    }

}
