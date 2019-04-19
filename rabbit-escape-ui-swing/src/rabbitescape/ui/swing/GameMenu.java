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

        Dimension doubleButtonSize = new Dimension(
            buttonSizeInPixels.width * 2,
            buttonSizeInPixels.height
        );
        Dimension spacerSize = new Dimension(
            buttonSizeInPixels.width * 2,
            buttonSizeInPixels.height / 2
        );

        this.mute = addToggleButton(
            "menu_unmuted",
            "menu_muted",
            ConfigTools.getBool( uiConfig, CFG_MUTED ),
            t( "Mute" ),
            doubleButtonSize
        );

        this.pause = addToggleButton(
            "menu_pause",
            "menu_unpause",
            false,
            t( "Pause" ),
            doubleButtonSize
        );

        addSpacer( spacerSize );

        this.abilities = addAbilitiesButtons(
            abilityTypes, buttonSizeInPixels );

        addSpacer( spacerSize );

        this.explodeAll = addButton(
            "menu_explode_all",
            t( "Explode all" ),
            doubleButtonSize
        );

        this.speed =
            addToggleButton(
                "menu_speedup_inactive",
                "menu_speedup_active",
                false,
                t( "speed up" ),
                doubleButtonSize
            );

        this.zoomIn = addButton(
            "menu_zoom_in",
            t( "Zoom in" ),
            doubleButtonSize
        );
        this.zoomOut = addButton(
            "menu_zoom_out",
            t( "Zoom out" ),
            doubleButtonSize
        );
        this.back = addButton(
            "menu_back",
            t( "Back" ),
            doubleButtonSize
        );

        panel.setPreferredSize(
            new Dimension(
                (int)( doubleButtonSize.width * 1.5 ),
                  ( 2 * spacerSize.height )       // Spacers
                + ( 6 + abilityTypes.size() )     // Buttons
                    * ( 8 + buttonSizeInPixels.height )
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
        Map<Token.Type, Integer> abilityTypes,
        Dimension buttonSizeInPixels
    )
    {
        Map<Token.Type, AbilityDisplay> ret = new HashMap<>();

        ButtonGroup abilitiesGroup = new ButtonGroup();

        int key = KeyEvent.VK_1;
        for ( Token.Type ability : sorted( abilityTypes.keySet() ) )
        {
            String iconName = "ability_" + ability.toString();

            JToggleButton button = addToggleButton(
                iconName,
                null,
                false,
                t( Token.name( ability ) ),
                buttonSizeInPixels
            );

            JLabel abilityNumber =  new JLabel();
            abilityNumber.setPreferredSize( buttonSizeInPixels );
            abilityNumber.setHorizontalTextPosition( SwingConstants.LEFT );
            panel.add( abilityNumber );

            MenuTools.clickOnKey( button, iconName, key );

            AbilityDisplay abilityDisplay = new AbilityDisplay(
                button,
                abilityNumber
            );
            abilityDisplay.setNumLeft( abilityTypes.get( ability ) );
            ret.put( ability, abilityDisplay );

            abilitiesGroup.add( button );

            ++key;
        }

        return ret;
    }

    private void addSpacer( Dimension spacerSize )
    {
        JPanel spacer = new JPanel();
        spacer.setBackground( backgroundColor );
        spacer.setPreferredSize( spacerSize );

        panel.add( spacer );
    }

    private JToggleButton addToggleButton(
        String unSelectedImage,
        String selectedImage,
        boolean selected,
        String description,
        Dimension buttonSizeInPixels
    )
    {
        JToggleButton button = new JToggleButton( getIcon( unSelectedImage ) );

        button.setBackground( backgroundColor );
        button.setBorderPainted( false );
        button.setSelected( selected );
        button.setToolTipText( description );
        button.setPreferredSize( buttonSizeInPixels );

        if ( selectedImage != null )
        {
            button.setSelectedIcon( getIcon( selectedImage ) );
        }

        panel.add( button );

        return button;
    }

    private JButton addButton(
        String image,
        String description,
        Dimension buttonSizeInPixels
    )
    {
        JButton button = new JButton( getIcon( image ) );

        button.setBackground( backgroundColor );
        button.setBorderPainted( false );
        button.setToolTipText( description );
        button.setPreferredSize( buttonSizeInPixels );

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
