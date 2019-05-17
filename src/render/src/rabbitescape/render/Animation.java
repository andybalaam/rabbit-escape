package rabbitescape.render;

import java.util.Iterator;
import java.util.List;

public class Animation implements Iterable<Frame>
{
    private final List<Frame> frames;

    public Animation( List<Frame> frames )
    {
        this.frames = frames;
    }

    @Override
    public Iterator<Frame> iterator()
    {
        return frames.iterator();
    }

    public int size()
    {
        return frames.size();
    }

    public Frame get( int i )
    {
        return frames.get( i );
    }
}
