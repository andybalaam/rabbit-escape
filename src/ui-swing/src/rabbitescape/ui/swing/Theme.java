package rabbitescape.ui.swing;

import java.awt.*;

public abstract class Theme
{

    public void change( SideMenu sideMenu, MenuUi menuUi)
    {
        setSideMenuColor( sideMenu);
        setMenuUiColor(menuUi);
    }

    public abstract void setSideMenuColor( SideMenu sideMenu );
    public abstract void setMenuUiColor( MenuUi menuUi );

    public abstract Theme getOppositeTheme();
}

