package rabbitescape.engine.err;

import java.util.Locale;

/**
 * Separate turning errors into string from reporting errors.
 */
public class RabbitEscapeException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage()
    {
        return translate(  Locale.getDefault() );
    }

    public String translate( Locale locale )
    {
        return ExceptionTranslation.translate( this, locale );
    }
}
