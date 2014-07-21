package rabbitescape.render;

import rabbitescape.engine.err.RabbitEscapeException;

public class FailedToLoadImage extends RabbitEscapeException
{
    private static final long serialVersionUID = 1L;
    public final String imageName;

    public FailedToLoadImage( String imageName, Exception cause )
    {
        super( cause );
        this.imageName = imageName;
    }
}
