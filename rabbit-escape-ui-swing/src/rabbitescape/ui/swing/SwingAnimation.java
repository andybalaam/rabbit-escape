package rabbitescape.ui.swing;

import java.util.ArrayList;
import java.util.List;

import rabbitescape.render.Animation;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.FrameNameAndOffset;
import rabbitescape.render.ScaledBitmap;

public class SwingAnimation
{
    private final List<SwingBitmapAndOffset> bitmaps;

    public SwingAnimation(
        BitmapCache<SwingBitmap> bitmapCache, Animation animation )
    {
        this.bitmaps = new ArrayList<>();

        for ( FrameNameAndOffset frame : animation )
        {
            ScaledBitmap<SwingBitmap> bmp = bitmapCache.get( frame.name );

            this.bitmaps.add(
                new SwingBitmapAndOffset( bmp, frame.offsetX, frame.offsetY ) );
        }
    }

    public SwingBitmapAndOffset get( int frameNum )
    {
        return bitmaps.get( frameNum );
    }
}
