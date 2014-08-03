package rabbitescape.ui.swing;

import java.util.ArrayList;
import java.util.List;

import rabbitescape.render.Animation;
import rabbitescape.render.FrameNameAndOffset;

public class SwingAnimation
{
    private final List<SwingBitmapAndOffset> bitmaps;

    public SwingAnimation( List<SwingBitmapAndOffset> bitmaps )
    {
        this.bitmaps = bitmaps;
    }

    public SwingAnimation( SwingBitmapLoader bitmapLoader, Animation animation )
    {
        this.bitmaps = new ArrayList<SwingBitmapAndOffset>();

        for ( FrameNameAndOffset frame : animation )
        {
            SwingBitmap bmp = bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/" + frame.name + ".png" );

            this.bitmaps.add(
                new SwingBitmapAndOffset( bmp, frame.offsetX, frame.offsetY ) );
        }
    }

    public SwingBitmapAndOffset get( int frameNum )
    {
        return bitmaps.get( frameNum );
    }
}
