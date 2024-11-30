package rabbitescape.ui.swing;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;

import javax.swing.*;
import java.awt.*;

import static rabbitescape.ui.swing.SwingConfigSetup.CFG_DARK_THEME;

public abstract class Theme
{
    protected Color backgroundColor;
    protected Color graphPaperMajor;
    protected Color graphPaperMinor;
    protected Color waterColor;

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

    public Color getGraphPaperMajor()
    {
        return graphPaperMajor;
    }

    public Color getGraphPaperMinor()
    {
        return graphPaperMinor;
    }

    public Color getWaterColor()
    {
        return waterColor;
    }

    public static Theme getTheme(Config uiConfig) {
        return ConfigTools.getBool( uiConfig, CFG_DARK_THEME ) ? DarkTheme.getInstance() : BrightTheme.getInstance();
    }
}

