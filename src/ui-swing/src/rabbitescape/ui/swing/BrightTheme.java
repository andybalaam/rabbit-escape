package rabbitescape.ui.swing;

import java.awt.*;

public class BrightTheme extends Theme
{
    static Theme uniqueInstance = new BrightTheme();

    @Override
    public void setSideMenuColor( SideMenu sideMenu )
    {
        sideMenu.setPanelBackground( Color.WHITE );
    }

    @Override
    public void setMenuUiColor( MenuUi menuUi )
    {
        menuUi.setBackgroundColor( Color.WHITE );
    }

    @Override
    public Theme getOppositeTheme()
    {
        return DarkTheme.getInstance();
    }

    public static Theme getInstance() {
        return uniqueInstance;
    }
}
