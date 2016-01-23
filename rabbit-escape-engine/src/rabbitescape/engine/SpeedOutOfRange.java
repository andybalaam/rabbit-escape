package rabbitescape.engine;

import rabbitescape.engine.err.RabbitEscapeException;

/**
 * A game speedup has been chosen that results in negative animation frame increments, zero animation
 * frame increments or is so high that game steps will be missed.
 */
public final class SpeedOutOfRange extends RabbitEscapeException
{
    private static final long serialVersionUID = 1L;
    public int speedRequested;

    public SpeedOutOfRange( int speedRequested)
    {
        this.speedRequested = speedRequested;
    }
}
