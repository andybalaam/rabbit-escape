package rabbitescape.ui.swing;

import java.awt.Color;

import rabbitescape.render.androidlike.Paint;

public class SwingPaint implements Paint
{
    public final Color color;

    public SwingPaint( Color color )
    {
        this.color = color;
    }
}
