package rabbitescape.ui.android;

import java.util.ArrayList;

import rabbitescape.render.Animation;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.FrameNameAndOffset;

public class AndroidAnimation
{
    private final ArrayList<AndroidBitmapAndOffset> bitmaps;

    public AndroidAnimation( BitmapCache<AndroidBitmap> bitmapCache, Animation animation )
    {
        this.bitmaps = new ArrayList<AndroidBitmapAndOffset>();

        for ( FrameNameAndOffset frame : animation )
        {
            AndroidBitmap bmp = bitmapCache.get( frame.name );

            this.bitmaps.add(
                new AndroidBitmapAndOffset( bmp, frame.offsetX, frame.offsetY ) );
        }
    }

    public AndroidBitmapAndOffset get( int frameNum )
    {
        return bitmaps.get( frameNum );
    }
}
