package rabbitescape.ui.swing;

import java.awt.*;

public class DarkTheme extends Theme
{
    private final SwingPaint baseColor = new SwingPaint( Color.BLACK );
    private final SwingPaint graphPaperMajor = new SwingPaint( new Color( 205, 212, 220 ) );
    private final SwingPaint graphPaperMinor =  new SwingPaint( new Color( 235, 243, 255 ) );

    @Override
    public void setSwingGraphicsColors( SwingGraphics swingGraphics )
    {
        swingGraphics.setBaseColor(baseColor);
        swingGraphics.setGraphPaperMinor(graphPaperMajor);
        swingGraphics.setGraphPaperMinor(graphPaperMinor);
    }

    @Override
    public Theme getOppositeTheme()
    {
        return new BrightTheme();
    }
}
