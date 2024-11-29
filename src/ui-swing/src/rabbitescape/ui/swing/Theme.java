package rabbitescape.ui.swing;

public abstract class Theme {

    public void change(SwingGraphics swingGraphics) {
        setSwingGraphicsColors(swingGraphics);
    }

    public abstract void setSwingGraphicsColors(SwingGraphics swingGraphics);
    public abstract Theme getOppositeTheme();
}
