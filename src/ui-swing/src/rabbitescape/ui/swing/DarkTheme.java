package rabbitescape.ui.swing;

import java.awt.*;

public class DarkTheme extends Theme
{
    static Theme uniqueInstance = new DarkTheme();

    private DarkTheme() {

    }

    @Override
    public void setSideMenuColor( SideMenu sideMenu )
    {
        sideMenu.setPanelBackground( Color.LIGHT_GRAY );
    }

    @Override
    public void setMenuUiColor( MenuUi menuUi )
    {
        menuUi.setBackgroundColor( Color.LIGHT_GRAY );
    }

    @Override
    public Theme getOppositeTheme()
    {
        return BrightTheme.getInstance();
    }

    public static Theme getInstance() {
        return uniqueInstance;
    }
}
