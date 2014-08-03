package rabbitescape.ui.swing;

import java.util.List;

public class SwingAnimation
{
    private final List<SwingBitmapAndOffset> bitmaps;

    public SwingAnimation( List<SwingBitmapAndOffset> bitmaps )
    {
        this.bitmaps = bitmaps;
    }

    public SwingBitmapAndOffset get( int frameNum )
    {
        return bitmaps.get( frameNum );
    }
}
