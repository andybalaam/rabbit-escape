package rabbitescape.engine.util;

import rabbitescape.engine.err.RabbitEscapeException;

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

    public boolean matches( String arg )
    {
        return arg.equals( longForm ) || arg.equals( shortForm );
    }

    public OptionRequiresParameter getOptionRequiresParameter( String arg )
    {
        return new OptionRequiresParameter( arg );
    }

    public UnkownOption getUnkownOption( String arg )
    {
        return new UnkownOption( arg );
    }
    
    

}
