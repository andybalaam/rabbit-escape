package rabbitescape.ui.swing;

import static rabbitescape.engine.i18n.Translation.*;
import static rabbitescape.ui.swing.SwingConfigSetup.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.render.BitmapCache;

class SideMenu
{
    private static final int ICON_SIZE = 32;

    public final JToggleButton mute;
    public final JButton back;
    public final JButton exit;

    private final BitmapCache<SwingBitmap> bitmapCache;
    private final Color backgroundColor;
    private final Dimension buttonSizeInPixels;

    private final JPanel panel;

    public SideMenu(
        Container contentPane,
        BitmapCache<SwingBitmap> bitmapCache,
        Dimension buttonSizeInPixels,
        Config uiConfig,
        Color backgroundColor
    )
    {
        this.bitmapCache = bitmapCache;
        this.backgroundColor = backgroundColor;
        this.buttonSizeInPixels = buttonSizeInPixels;
        this.panel = createPanel( contentPane );

        this.mute = addToggleButton(
            "menu_unmuted",
            "menu_muted",
            ConfigTools.getBool( uiConfig, CFG_MUTED ),
            t( "Mute" )
        );

        addSpacer();

        this.back = addButton( "menu_back", t( "Back" ) );

        addSpacer();

        this.exit = addButton( "menu_exit", t( "Exit" ) );

        panel.setPreferredSize(
            new Dimension(
                8 + buttonSizeInPixels.width,
                  ( 2 * 16 )                                  // Spacers
                + ( 3 * ( 16 + buttonSizeInPixels.height ) )  // Buttons
            )
        );

        addPanelInScrollPane( contentPane );
    }

    private void addPanelInScrollPane( Container contentPane )
    {
        JScrollPane scrollPane = new JScrollPane( panel );
        scrollPane.getVerticalScrollBar().setUnitIncrement( 16 );
        contentPane.add( scrollPane, BorderLayout.WEST );
    }

    private JPanel createPanel( Container contentPane )
    {
        LayoutManager layout = new FlowLayout( FlowLayout.CENTER, 4, 4 );
        JPanel ret = new JPanel( layout );

        ret.setBackground( backgroundColor );

        ret.setPreferredSize(
            new Dimension(
                buttonSizeInPixels.width + 8,
                -1
            )
        );

        return ret;
    }

    private void addSpacer()
    {
        JPanel spacer = new JPanel();
        spacer.setBackground( backgroundColor );

        panel.add( spacer );
    }

    private JToggleButton addToggleButton(
        String unSelectedImage,
        String selectedImage,
        boolean selected,
        String description
    )
    {
        JToggleButton button = new JToggleButton( getIcon( unSelectedImage ) );

        button.setBackground( backgroundColor );
        button.setBorderPainted( false );
        button.setSelected( selected );
        button.setToolTipText( description );

        if ( selectedImage != null )
        {
            button.setSelectedIcon( getIcon( selectedImage ) );
        }

        panel.add( button );

        return button;
    }

    private JButton addButton( String image, String description )
    {
        JButton button = new JButton( getIcon( image ) );

        button.setBackground( backgroundColor );
        button.setBorderPainted( false );
        button.setToolTipText( description );

        panel.add( button );

        return button;
    }

    private ImageIcon getIcon( String name )
    {
        return new ImageIcon(
            bitmapCache.get( name, ICON_SIZE ).image );
    }
}
