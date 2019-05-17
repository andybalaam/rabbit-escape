package rabbitescape.ui.swing;

import java.awt.Color;

import rabbitescape.render.androidlike.Paint;

public class SwingPaint implements Paint
{
    public final Color color;
    private Style style;

    public enum Style
    {
        FILL,
        STROKE
    }

    public SwingPaint( Color color )
    {
        this.color = color;
        style = Style.STROKE;
    }

    public Style getStyle()
    {
        return style;
    }

    public void setStyle( Style style )
    {
        this.style = style;
    }
}
