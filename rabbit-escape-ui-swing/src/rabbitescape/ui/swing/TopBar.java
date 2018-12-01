package rabbitescape.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.util.Map;

import javax.swing.*;

import rabbitescape.engine.Token;
import rabbitescape.render.gameloop.Physics;

import static rabbitescape.engine.i18n.Translation.t;
import static rabbitescape.engine.util.Util.*;

public class TopBar implements Physics.StatsChangedListener
{
    private static final String outText     = "Out: ${num1} / ${num2}";
    private static final String savedText   = "Saved: ${num1} / ${num2}";

    private static final String abilityText =
        "${ability} (${numLeft} left)";

    private final Color backgroundColor;
    private final JPanel panel;
    private final JLabel out;
    private final JLabel saved;
    private final JLabel ability;
    private final int numToSave;

    public TopBar( 
        Color backgroundColor, 
        int numToSave,
        Container contentPane, 
        String worldName 
    )
    {
        this.backgroundColor = backgroundColor;
        this.panel = createPanel( contentPane );

        addLabel( worldName, 300 );
        this.ability = addLabel( "" );
        this.out     = addLabel( outText );
        this.saved   = addLabel( savedText );
        setCountText( this.saved, savedText, 0, numToSave );
        setCountText( this.out, outText, 0, 0 );
        this.numToSave = numToSave;
    }

    private JPanel createPanel( Container contentPane )
    {
        LayoutManager layout = new FlowLayout( FlowLayout.CENTER, 4, 4 );
        JPanel ret = new JPanel( layout );

        ret.setBackground( backgroundColor );

        contentPane.add( ret, BorderLayout.NORTH );

        return ret;
    }

    private JLabel addLabel( String text, int width )
    {
        JLabel ret = new JLabel( t( text, newMap( "num", "" ) ) );
        ret.setPreferredSize( new Dimension( width, 20 ) );
        panel.add( ret );
        return ret;
    }

    private JLabel addLabel( String text )
    {
        return addLabel( text, 200);
    }

    @Override
    public void changed( int waiting, int out, int saved )
    {
        setCountText( this.out,     outText,     out, waiting+out );
        setCountText( this.saved,   savedText,   saved, numToSave );
    }

    public void abilityChanged( Token.Type ability, int numLeft )
    {
        setAbilityText( this.ability, abilityText, ability.name(), numLeft );
    }

    private void setCountText( JLabel label, String text, int num1, int num2 )
    {
        setText( label, text,
                 newMap( "num1", String.valueOf( num1 ),
                         "num2", String.valueOf( num2 ) ) );
    }

    private void setAbilityText(
        JLabel label, String text, String ability, int numLeft )
    {
        setText(
            label,
            text,
            newMap( "ability", ability, "numLeft", String.valueOf( numLeft ) )
        );
    }

    private void setText(
        final JLabel label,
        final String text,
        final Map<String, Object> params
    )
    {
        SwingUtilities.invokeLater(
            new Runnable()
            {
                @Override
                public void run()
                {
                    label.setText( t( text, params ) );
                }
            }
        );
    }

}
