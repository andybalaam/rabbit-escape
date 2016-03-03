package rabbitescape.engine.menu;

import java.util.Locale;

import rabbitescape.engine.err.RabbitEscapeException;

public class ByNameConfigBasedLevelsCompleted
{
    public static class EmptyLevelName extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;
    }

    private static final Locale en_UK = Locale.UK;

    // ---

    public static String canonicalName( String name )
    {
        if ( name.isEmpty() )
        {
            throw new EmptyLevelName();
        }
        else
        {
            return name.toLowerCase( en_UK ).replaceAll( "[^a-z]", "_" );
        }
    }
}
