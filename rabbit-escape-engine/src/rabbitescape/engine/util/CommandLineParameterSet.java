package rabbitescape.engine.util;

public class CommandLineParameterSet
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
                if ( o.takesParam )
                {
                    o.setValue( args[++i] );
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
        throw (new CommandLineOption("--null",false)).getUnkownOption( arg );
    }

}
