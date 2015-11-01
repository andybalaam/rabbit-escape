package rabbitescape.engine.solution;

import rabbitescape.engine.err.RabbitEscapeException;

public class InvalidInstruction extends RabbitEscapeException
{
    private static final long serialVersionUID = 1L;
    public String instruction;

    public InvalidInstruction( String instruction )
    {
        this.instruction = instruction;
    }
}
