package rabbitescape.ui.swing;

import javax.swing.*;
import java.awt.*;

public abstract class Theme
{
    protected Color backgroundColor;

    public void change( SideMenu sideMenu, MenuUi menuUi, Container contentPane , JScrollPane scrollPane, JPanel menuPanel)
    {
        setSideMenuColor(sideMenu);
        setColorMenuUi(contentPane, scrollPane, menuPanel);
    }

    public abstract void setSideMenuColor( SideMenu sideMenu );
    public abstract void setColorMenuUi( Container contentPane , JScrollPane scrollPane, JPanel menuPanel);

    public abstract Theme getOppositeTheme();

    public Color getBackgroundColor()
    {
        return backgroundColor;
    }
}

