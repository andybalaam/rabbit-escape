package rabbitescape.ui.swing;

import javax.swing.*;
import java.awt.*;

public class BrightTheme extends Theme
{
    private static Theme uniqueInstance = new BrightTheme();

    private BrightTheme() {
        backgroundColor = Color.WHITE;
        graphPaperMajor =  new Color( 205, 212, 220 );
        graphPaperMinor = new Color( 235, 243, 255 );
        waterColor = new Color(130, 167, 221);
    }

    @Override
    public void setSideMenuColor( SideMenu sideMenu )
    {
        sideMenu.setPanelBackground( backgroundColor );
    }

    @Override
    public Theme getOppositeTheme()
    {
        return DarkTheme.getInstance();
    }

    @Override
    public void setColorMenuUi(
        Container contentPane,
        JScrollPane scrollPane,
        JPanel menuPanel
    )
    {
        contentPane.setBackground( backgroundColor );
        scrollPane.setBackground( backgroundColor );
        menuPanel.setBackground( backgroundColor );
    }

    public static Theme getInstance() {
        return uniqueInstance;
    }
}
