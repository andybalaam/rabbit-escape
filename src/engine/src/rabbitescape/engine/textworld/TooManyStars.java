package rabbitescape.engine.textworld;

import rabbitescape.engine.err.RabbitEscapeException;

public class TooManyStars extends RabbitEscapeException
{
    private static final long serialVersionUID = 1L;

    public String[] lines;

    public TooManyStars( String[] lines )
    {
        this.lines = lines;
    }
}
