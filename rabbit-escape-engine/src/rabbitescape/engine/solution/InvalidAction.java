package rabbitescape.engine.solution;

import rabbitescape.engine.err.RabbitEscapeException;

public class InvalidAction extends RabbitEscapeException
{
    private static final long serialVersionUID = 1L;
    public String action;

    public InvalidAction( Throwable cause, String action )
    {
        super( cause );
        this.action = action;
    }

    public InvalidAction( String action )
    {
        this.action = action;
    }
}
