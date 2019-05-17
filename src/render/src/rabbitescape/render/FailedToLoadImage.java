package rabbitescape.render;

import rabbitescape.engine.err.RabbitEscapeException;

public class FailedToLoadImage extends RabbitEscapeException
{
    private static final long serialVersionUID = 1L;
    public final String imageName;
    public final Throwable cause;

    public FailedToLoadImage( String imageName, Throwable cause )
    {
        super( cause );
        this.imageName = imageName;
        this.cause = cause;
    }

    public FailedToLoadImage( String imageName )
    {
        this.imageName = imageName;
        this.cause = null;
    }
}
