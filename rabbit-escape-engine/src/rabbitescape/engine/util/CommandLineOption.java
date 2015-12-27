package rabbitescape.engine.util;

import rabbitescape.engine.err.RabbitEscapeException;

/**
 * Supply a long form option (eg "--start"). 
 * The short form is generated automatically (eg "-s").
 * Parameters may be supplied as the next arg, or
 * concatenated ( "-s 0", "-s0");
 * Limitations;
 * * Options must begin "--".
 * * Different options cannot begin with the same letter
 * * Options may not be repeated on the command line.
 */
public class CommandLineOption
{
    class UnkownOption extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;
        public final String unknownArg;
        
        public UnkownOption( String arg )
        {
            this.unknownArg = arg;
        }
    }
    
    class OptionRequiresParameter extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;
        public final String arg;
        
        public OptionRequiresParameter( String arg )
        {
            this.arg = arg;
        }
    }
    
    class OptionDoesNotTakeParameter extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;
        public final String arg;
        
        public OptionDoesNotTakeParameter( String arg )
        {
            this.arg = arg;
        }
    }

    public final String longForm;
    public final String shortForm;
    public final boolean takesParam;
    private String value = null;
    private boolean present = false;

    public CommandLineOption( String longForm, boolean takesParam )
    {
        this.takesParam = takesParam;
        this.longForm = longForm;
        assert( longForm.startsWith( "--" ) );
        this.shortForm = longForm.substring( 1, 3 );
    }
    
    public void setValue( String value )
    {
        if ( value.length() > 0 && value.startsWith( "-" ) )
        {
            throw new OptionRequiresParameter( longForm );
        }
        this.value = value;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setPresent()
    {
        present = true;
    }
    
    public boolean isPresent()
    {
        return present;
    }

    /**
     * Checks if the command line argument is a match for this one.
     * Also tests for concatenated param.
     */
    public boolean matches( String arg )
    {
        String maybeParam = null;
        if ( arg.startsWith( longForm ) )
        {
            maybeParam = arg.substring( longForm.length() );
        }
        if ( arg.startsWith( shortForm ) )
        {
            maybeParam = arg.substring( shortForm.length() );
        }
        if ( maybeParam == null )
        {
            return false;
        }
        if ( maybeParam.length() > 0 )
        {
            if ( !takesParam )
            {
                throw new OptionDoesNotTakeParameter(shortForm);
            }
            this.setValue( maybeParam );
        }
        return true;
    }

    public OptionRequiresParameter getOptionRequiresParameter( String arg )
    {
        return new OptionRequiresParameter( arg );
    }

    public UnkownOption getUnkownOption( String arg )
    {
        return new UnkownOption( arg );
    }

    public int getInt()
    {
        return Integer.parseInt( value );
    }
    
    

}
