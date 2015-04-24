package rabbitescape.ui.android;

import android.view.ScaleGestureDetector;

public class OnScaleListener implements ScaleGestureDetector.OnScaleGestureListener
{
    private final AndroidGraphics graphics;

    public OnScaleListener( AndroidGraphics graphics )
    {
        this.graphics = graphics;
    }

    @Override
    public void onScaleEnd( ScaleGestureDetector detector )
    {
    }

    @Override
    public boolean onScaleBegin( ScaleGestureDetector detector )
    {
        return true;
    }

    @Override
    public boolean onScale( ScaleGestureDetector detector )
    {
        graphics.scaleRenderingTileSize(
            detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY() );

        return true;
    }
}

