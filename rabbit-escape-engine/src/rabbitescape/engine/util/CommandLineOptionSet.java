package rabbitescape.engine.util;

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
                // If param was not concatenated with option, check along the array.
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
    }
    
    private static CommandLineOption match( String arg, CommandLineOption[] options )
    {
        for ( CommandLineOption o: options )
        {
            if ( o.matches( arg ) )
            {
                return o;
            }
        }
        throw ( new CommandLineOption( "--null", false ) ).getUnknownOption( arg );
    }

}
