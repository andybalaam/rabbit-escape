package rabbitescape.render;

import java.util.Iterator;
import java.util.List;

public class Animation implements Iterable<FrameNameAndOffset>
{
    private final List<FrameNameAndOffset> frames;

    public Animation( List<FrameNameAndOffset> frames )
    {
        this.frames = frames;
    }

    @Override
    public Iterator<FrameNameAndOffset> iterator()
    {
        return frames.iterator();
    }

    public int size()
    {
        return frames.size();
    }

    public FrameNameAndOffset get( int i )
    {
        return frames.get( i );
    }
}
