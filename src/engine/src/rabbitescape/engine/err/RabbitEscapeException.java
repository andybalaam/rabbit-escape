package rabbitescape.engine.err;

import java.util.Locale;

/**
 * Separate turning errors into string from reporting errors.
 */
public class RabbitEscapeException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public final RabbitEscapeException cause;

    public RabbitEscapeException()
    {
        this.cause = null;
    }

    public RabbitEscapeException( RabbitEscapeException cause )
    {
        super( cause );
        this.cause = cause;
    }

    public RabbitEscapeException( Throwable cause )
    {
        super( cause );
        this.cause = null;
    }

    @Override
    public String getMessage()
    {
        return translate( Locale.getDefault() );
    }

    public String translate( Locale locale )
    {
        String ret = "";
        if ( cause != null )
        {
            ret += cause.translate( locale );
            ret += "\n";
        }
        return ret + ExceptionTranslation.translate( this, locale );
    }
}
