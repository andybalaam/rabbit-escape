package rabbitescape.ui.swing;

import static rabbitescape.engine.util.Util.*;
import static rabbitescape.ui.swing.SwingConfigSetup.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import rabbitescape.engine.Token;
import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.render.BitmapCache;

class GameMenu
{
    public static interface AbilityChangedListener
    {
        void abilityChosen( Token.Type ability );
    }

    public JToggleButton mute;
    public JToggleButton pause;
    public final JButton back;


    private final BitmapCache<SwingBitmap> bitmapCache;
    private final Color backgroundColor;
    private final Dimension buttonSizeInPixels;
    private final Dimension worldSizeInPixels;

    private final JPanel panel;
    public final Map<Token.Type, JToggleButton> abilities;

    public GameMenu(
        Container contentPane,
        BitmapCache<SwingBitmap> bitmapCache,
        Dimension buttonSizeInPixels,
        Dimension worldSizeInPixels,
        Config uiConfig,
        Color backgroundColor,
        Map<Token.Type, Integer> abilityTypes
    )
    {
        this.bitmapCache = bitmapCache;
        this.backgroundColor = backgroundColor;
        this.buttonSizeInPixels = buttonSizeInPixels;
        this.worldSizeInPixels = worldSizeInPixels;
        this.panel = createPanel( contentPane );

        this.mute = addToggleButton(
            "menu_unmuted",
            "menu_muted",
            ConfigTools.getBool( uiConfig, CFG_MUTED )
        );

        this.pause = addToggleButton( "menu_pause", "menu_unpause", false );

        addSpacer();

        this.abilities = addAbilitiesButtons( abilityTypes );

        addSpacer();

        this.back = addButton( "menu_back" );

        addPanelInScrollPane( contentPane );
    }

    private void addPanelInScrollPane( Container contentPane )
    {
        JScrollPane scrollPane = new JScrollPane( panel );
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
                worldSizeInPixels.height
            )
        );

        return ret;
    }

    private Map<Token.Type, JToggleButton> addAbilitiesButtons(
        Map<Token.Type, Integer> abilityTypes )
    {
        Map<Token.Type, JToggleButton> ret = new HashMap<>();

        ButtonGroup abilitiesGroup = new ButtonGroup();

        for ( Token.Type ability : sorted( abilityTypes.keySet() ) )
        {
            String iconName = "ability_" + ability.toString();

            JToggleButton button = addToggleButton( iconName, null, false );

            ret.put( ability, button );

            abilitiesGroup.add( button );
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
        boolean selected
    )
    {
        JToggleButton button = new JToggleButton( getIcon( unSelectedImage ) );

        button.setBackground( backgroundColor );
        button.setBorderPainted( false );
        button.setSelected( selected );

        if ( selectedImage != null )
        {
            button.setSelectedIcon( getIcon( selectedImage ) );
        }

        panel.add( button );

        return button;
    }

    private JButton addButton( String image )
    {
        JButton button = new JButton( getIcon( image ) );

        button.setBackground( backgroundColor );
        button.setBorderPainted( false );

        panel.add( button );

        return button;
    }

    private ImageIcon getIcon( String name )
    {
        return new ImageIcon(
            bitmapCache.get(
                "/rabbitescape/ui/swing/images32/" + name + ".png" ).image
        );
    }

    public void addAbilitiesListener( final AbilityChangedListener listener )
    {
        for (
            final Map.Entry<Token.Type, JToggleButton> abilityEntry
                : abilities.entrySet()
        )
        {
            abilityEntry.getValue().addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent evt )
                {
                    listener.abilityChosen( abilityEntry.getKey() );
                }
            } );
        }
    }
}
