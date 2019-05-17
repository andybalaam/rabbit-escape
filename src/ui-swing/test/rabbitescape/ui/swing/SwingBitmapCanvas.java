package rabbitescape.ui.swing;

public class SwingBitmapCanvas extends SwingCanvas
{
    public final SwingBitmap bitmap;

    public SwingBitmapCanvas( SwingBitmap bitmap )
    {
        super(
            bitmap.image.createGraphics(),
            bitmap.image.getWidth(),
            bitmap.image.getHeight()
        );

        this.bitmap = bitmap;
    }
}
