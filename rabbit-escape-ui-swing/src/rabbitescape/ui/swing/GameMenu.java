package rabbitescape.ui.swing;

import static rabbitescape.engine.i18n.Translation.*;
import static rabbitescape.engine.util.Util.*;
import static rabbitescape.ui.swing.SwingConfigSetup.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import rabbitescape.engine.Token;
import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.render.BitmapCache;

class GameMenu
{
    public interface AbilityChangedListener
    {
        void abilityChosen( Token.Type ability );
    }

    private static final int ICON_SIZE = 32;

    public JToggleButton mute;
    public JToggleButton pause;
    public JToggleButton speed;
    public final JButton explodeAll;
    public final JButton zoomIn;
    public final JButton zoomOut;
    public final JButton back;


    private final BitmapCache<SwingBitmap> bitmapCache;
    private final Color backgroundColor;

    private final JPanel panel;
    public final Map<Token.Type, AbilityDisplay> abilities;

    public GameMenu(
        Container contentPane,
        BitmapCache<SwingBitmap> bitmapCache,
        Dimension buttonSizeInPixels,
        Config uiConfig,
        Color backgroundColor,
        Map<Token.Type, Integer> abilityTypes
    )
    {
        this.bitmapCache = bitmapCache;
        this.backgroundColor = backgroundColor;
        this.panel = createPanel();

        this.mute = addToggleButton(
            "menu_unmuted",
            "menu_muted",
            ConfigTools.getBool( uiConfig, CFG_MUTED ),
            t( "Mute" )
        );

        this.pause = addToggleButton(
            "menu_pause", "menu_unpause", false, t( "Pause" ) );

        addSpacer();

        this.abilities = addAbilitiesButtons( abilityTypes );

        addSpacer();

        this.explodeAll = addButton( "menu_explode_all", t( "Explode all" ) );

        this.speed =
            addToggleButton( "menu_speedup_inactive", "menu_speedup_active",
                             false, t( "speed up" ) );

        this.zoomIn     = addButton( "menu_zoom_in",     t( "Zoom in" ) );
        this.zoomOut    = addButton( "menu_zoom_out",    t( "Zoom out" ) );
        this.back       = addButton( "menu_back",        t( "Back" ) );

        panel.setPreferredSize(
            new Dimension(
                buttonSizeInPixels.width + 8,
                  ( 2 * 16 )                                // Spacers
                + ( 6 + abilityTypes.size() )               // Buttons
                    * ( 16 + buttonSizeInPixels.height )
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

    private JPanel createPanel()
    {
        LayoutManager layout = new FlowLayout( FlowLayout.CENTER, 4, 4 );
        JPanel ret = new JPanel( layout );

        ret.setBackground( backgroundColor );

        return ret;
    }

    private Map<Token.Type, AbilityDisplay> addAbilitiesButtons(
        Map<Token.Type, Integer> abilityTypes )
    {
        Map<Token.Type, AbilityDisplay> ret = new HashMap<>();

        ButtonGroup abilitiesGroup = new ButtonGroup();

        int key = KeyEvent.VK_1;
        for ( Token.Type ability : sorted( abilityTypes.keySet() ) )
        {
            String iconName = "ability_" + ability.toString();

            JToggleButton button = addToggleButton(
                iconName, null, false, t( Token.name( ability ) ) );

            JLabel abilityNumber =  new JLabel();
            panel.add( abilityNumber );

            MenuTools.clickOnKey( button, iconName, key );

            AbilityDisplay abilityDisplay = new AbilityDisplay(
                button,
                abilityNumber
            );
            abilityDisplay.setNumLeft( abilityTypes.get( abilityt statu ) );
            ret.put( ability, abilityDisplay );

            abilitiesGroup.add( button );

            ++key;
        }

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

    public void addAbilitiesListener( final AbilityChangedListener listener )
    {
        for (
            final Map.Entry<Token.Type, AbilityDisplay> abilityEntry
                : abilities.entrySet()
        )
        {
            abilityEntry.getValue().button.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed( ActionEvent evt )
                    {
                        listener.abilityChosen( abilityEntry.getKey() );
                    }
                }
            );
        }
    }

    static class AbilityDisplay
    {
        final JToggleButton button;
        final JLabel label;

        AbilityDisplay(JToggleButton button, JLabel label) {
            this.button = button;
            this.label = label;
        }

        public void setNumLeft( int numLeft )
        {
            if ( numLeft == 0 ) {
                button.setEnabled( false );
            }
            label.setText( " " + numLeft );
        }
    }
}
