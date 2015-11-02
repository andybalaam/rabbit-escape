package rabbitescape.engine.solution;

import rabbitescape.engine.err.RabbitEscapeException;

public class InvalidSolution extends RabbitEscapeException
{
    private static final long serialVersionUID = 1L;

    public String message;

    public InvalidSolution( String message )
    {
        this.message = message;
    }

    public InvalidSolution( String message, RabbitEscapeException cause )
    {
        super( cause );
        this.message = message;
    }
}
