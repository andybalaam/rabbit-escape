package rabbitescape.ui.android;

import rabbitescape.render.androidlike.Paint;

public class AndroidPaint implements Paint
{
    public final android.graphics.Paint paint;

    public AndroidPaint( android.graphics.Paint paint )
    {
        this.paint = paint;
    }
}
