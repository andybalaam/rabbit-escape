package rabbitescape.engine;

import java.util.Map;

import rabbitescape.engine.err.RabbitEscapeException;

public class BadSavedState extends RabbitEscapeException
{
    public final Map<String, String> saveState;

    public BadSavedState( Throwable cause, Map<String, String> saveState )
    {
        super( cause );
        this.saveState = saveState;
    }

    private static final long serialVersionUID = 1L;
}
