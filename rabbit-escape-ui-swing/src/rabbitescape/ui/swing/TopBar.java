package rabbitescape.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;

import javax.swing.*;

import rabbitescape.ui.swing.SwingGameLoop.StatsChangedListener;

import static rabbitescape.engine.i18n.Translation.t;
import static rabbitescape.engine.util.Util.*;

public class TopBar implements StatsChangedListener
{
    private static final String waitingText = "Waiting: ${num}";
    private static final String outText     = "Out: ${num}";
    private static final String savedText   = "Saved: ${num}";
    private static final String needText    = "Need to save: ${num}";

    private final Color backgroundColor;
    private final JPanel panel;
    private final JLabel waiting;
    private final JLabel out;
    private final JLabel saved;
    private final JLabel need;

    public TopBar(
        Color backgroundColor, int numToSave, Container contentPane )
    {
        this.backgroundColor = backgroundColor;
        this.panel = createPanel( contentPane );

        this.waiting = addLabel( waitingText );
        this.out     = addLabel( outText );
        this.saved   = addLabel( savedText );
        this.need    = addLabel( needText );
        setText( this.need, needText, numToSave );
    }

    private JPanel createPanel( Container contentPane )
    {
        LayoutManager layout = new FlowLayout( FlowLayout.CENTER, 4, 4 );
        JPanel ret = new JPanel( layout );

        ret.setBackground( backgroundColor );

        contentPane.add( ret, BorderLayout.NORTH );

        return ret;
    }

    private JLabel addLabel( String text )
    {
        JLabel ret = new JLabel( t( text, newMap( "num", "" ) ) );
        ret.setPreferredSize( new Dimension( 150, 20 ) );
        panel.add( ret );
        return ret;
    }

    @Override
    public void changed( int waiting, int out, int saved )
    {
        setText( this.waiting, waitingText, waiting );
        setText( this.out,     outText,     out );
        setText( this.saved,   savedText,   saved );
    }

    private void setText( final JLabel label, final String text, final int num )
    {
        SwingUtilities.invokeLater(
            new Runnable()
            {
                @Override
                public void run()
                {
                    label.setText(
                        t( text, newMap( "num", String.valueOf( num ) ) ) );
                }
            }
        );
    }
}
