package rabbitescape.ui.swing;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static rabbitescape.engine.i18n.Translation.t;

/**
 * @brief GUI elements for retrieving levels from issues.
 */
public class GitHubIssueDialog extends JDialog implements ChangeListener
{

    private static final long serialVersionUID = 1L;
    private JTextField issueNameBox = new JTextField( "" );
    private JTextArea issueTextBox = new JTextArea( "" );
    private JTextArea issueWorldBox = new JTextArea( "" );
    private JTextField statusBox = new JTextField(
        t( "Attempting to contact github." ) );
    private IssueSpinnerModel issueModel;
    private WorldSpinnerModel worldModel;
    private boolean choseWorld = false;
    GitHubClient ghc = null;

    public static enum Label
    {
        ALL,
        BUG,
        LEVEL
    };

    class IssueSpinnerModel implements SpinnerModel, GitHubPageFetcher
    {
        private int issueIndex = 0;
        private ChangeListener changeListener;
        private Label filterMode = Label.ALL;

        IssueSpinnerModel()
        {
        }

        /**
         * @brief
         */
        @Override
        public void notifyAndFetch( String url,
            String requestProperty,
            String notification,
            GitHubPageFetchNotifier ghpfn )
        {
            GitHubIssue ghi = issueModel.getCurrentIssue();
            if ( null == ghi )
            {
                return;
            }
            final IssuePageFetchWorker<Void, Void> w =
                new IssuePageFetchWorker<Void, Void>( url, requestProperty,
                    ghpfn );
            w.execute();
            statusBox.setText( notification );
            // Let dt repaint statusBox
            DotTic dt = new DotTic( w );
            dt.start();
        }

        @Override
        public void addChangeListener( ChangeListener cl )
        {
            changeListener = cl;
        }

        @Override
        public Object getNextValue()
        {

            return getRelativeValue( -1 );
        }

        @Override
        public Object getPreviousValue()
        {
            return getRelativeValue( +1 );
        }

        public Object getRelativeValue( int indexStep )
        {
            if ( null == ghc )
            {
                return null;
            }
            int newIssueIndex = issueIndex + indexStep;
            GitHubIssue ghi = ghc.getIssue( newIssueIndex, this );
            if ( null == ghi )
            {
                return null;
            }
            switch ( filterMode )
            {
            case BUG:
                if ( !ghi.isBug() )
                {
                    return getRelativeValue( indexStep
                        + (int)Math.signum( indexStep ) );
                }
                break;
            case LEVEL:
                if ( !ghi.isLevel() )
                {
                    return getRelativeValue( indexStep
                        + (int)Math.signum( indexStep ) );
                }
                break;
            case ALL:
            }
            return Integer.valueOf( ghi.getNumber() );
        }

        public GitHubIssue getCurrentIssue()
        {
            if ( null == ghc )
            {
                return null;
            }
            return ghc.getIssue( issueIndex, this );
        }

        @Override
        public Object getValue()
        {
            GitHubIssue ghi = getCurrentIssue();
            if ( null == ghi )
            {
                return null;
            }
            return Integer.valueOf( ghi.getNumber() );
        }

        @Override
        public void removeChangeListener( ChangeListener arg0 )
        {
            throw new RuntimeException(); // not used
        }

        public void setFilter( Label filter )
        {
            filterMode = filter;
        }

        @Override
        public void setValue( Object issueNumberIntegerObject )
        {
            Integer issueNumber = (Integer)issueNumberIntegerObject;
            if ( null == issueNumber )
            {
                return;
            }
            issueIndex = ghc.getIndexOfNumber( issueNumber.intValue() );
            // new issue may have different world index current
            worldModel.setValue( worldModel.getValue() );
            changeListener.stateChanged( new ChangeEvent( this ) );
        }

    }

    class WorldSpinnerModel implements SpinnerModel
    {
        private ChangeListener changeListener;

        @Override
        public void addChangeListener( ChangeListener l )
        {
            changeListener = l;
        }

        @Override
        public Object getNextValue()
        {
            GitHubIssue ghi = issueModel.getCurrentIssue();
            if ( null == ghi )
            {
                return null;
            }
            int value = ghi.getCurrentWorldIndex() - 1;
            return Integer.valueOf( value < 0 ? 0 : value );
        }

        @Override
        public Object getPreviousValue()
        {
            GitHubIssue ghi = issueModel.getCurrentIssue();
            if ( null == ghi )
            {
                return null;
            }
            int value = ghi.getCurrentWorldIndex() + 1;
            // No bounds checking needed here, setValue's is OK.
            return Integer.valueOf( value );
        }

        @Override
        public Object getValue()
        {
            GitHubIssue ghi = issueModel.getCurrentIssue();
            if ( null == ghi )
            {
                return null;
            }
            return Integer.valueOf( ghi.getCurrentWorldIndex() );
        }

        @Override
        public void removeChangeListener( ChangeListener l )
        {
            throw new RuntimeException(); // Not used
        }

        @Override
        public void setValue( Object WorldIndexIntegerObject )
        {
            Integer worldIndexInteger = (Integer)WorldIndexIntegerObject;
            if ( null == worldIndexInteger )
            {
                return;
            }
            GitHubIssue ghi = issueModel.getCurrentIssue();
            if ( null == ghi )
            {
                return;
            }
            if ( ghi.setCurrentWorldIndex( (int)worldIndexInteger ) )
            {
                changeListener.stateChanged( new ChangeEvent( this ) );
            }
        }

    }

    protected GitHubIssueDialog( Frame frame )
    {
        super( frame, true ); // arg2 sets modal
        setTitle( t(
            "Retrieve level from the GitHub Rabbit Escape issue pages." ) );

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel pane = new JPanel( new GridBagLayout() );

        issueModel = new IssueSpinnerModel();
        JSpinner issueSpinner = new JSpinner( issueModel );
        issueSpinner.setPreferredSize( new Dimension( 60, 30 ) );
        issueSpinner.addChangeListener( this );

        worldModel = new WorldSpinnerModel();
        JSpinner worldSpinner = new JSpinner( worldModel );
        worldSpinner.setPreferredSize( new Dimension( 60, 30 ) );
        worldSpinner.addChangeListener( this );

        JRadioButton levelFilterButton = new JRadioButton( t( "Level" ) );
        JRadioButton bugFilterButton = new JRadioButton( t( "Bug" ) );
        JRadioButton allFilterButton = new JRadioButton( t( "All" ) );
        ButtonGroup filterButtons = new ButtonGroup();
        filterButtons.add( levelFilterButton );
        filterButtons.add( bugFilterButton );
        filterButtons.add( allFilterButton );
        allFilterButton.doClick(); // set preselected filter
        JButton fetchFollowButton =
            new JButton( t( "Fetch followup comments" ) );
        JPanel spacerPanel = new JPanel();
        // Ater packing, this gives the dialog it's width
        spacerPanel.setPreferredSize( new Dimension( 500, 2 ) );
        JButton okButton = new JButton( t( "OK" ) );
        JButton cancelButton = new JButton( t( "Cancel" ) );

        statusBox.setEditable( false );
        issueNameBox.setEditable( false );
        issueTextBox.setEditable( false );
        JScrollPane issueTextScrollPane = new JScrollPane( issueTextBox );
        issueTextScrollPane.setPreferredSize( new Dimension( 300, 300 ) );
        issueTextBox.setBorder( BorderFactory.createLineBorder( Color.GRAY ) );
        issueWorldBox.setEditable( false );
        issueWorldBox.setBorder( BorderFactory.createLineBorder( Color.GRAY ) );
        issueWorldBox.setFont( new Font( "monospaced", Font.PLAIN, 12 ) );
        // After packing, this gives the dialog it's height
        issueWorldBox.setPreferredSize( new Dimension( 2, 500 ) );

        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        pane.add( issueSpinner, gbc );
        gbc.gridheight = 1;

        gbc.gridx = 0;
        gbc.gridy = 2;
        pane.add( worldSpinner, gbc );

        gbc.gridx = 1;
        gbc.gridy = 0;
        pane.add( levelFilterButton, gbc );

        gbc.gridx = 1;
        gbc.gridy = 1;
        pane.add( bugFilterButton, gbc );

        gbc.gridx = 1;
        gbc.gridy = 2;
        pane.add( allFilterButton, gbc );

        gbc.gridx = 2;
        gbc.gridy = 0;
        pane.add( fetchFollowButton, gbc );

        gbc.gridwidth = 3;
        gbc.gridx = 3;
        gbc.gridy = 0;
        pane.add( statusBox, gbc );
        gbc.gridwidth = 1;

        gbc.gridwidth = 3;
        gbc.gridx = 3;
        gbc.gridy = 1;
        pane.add( issueNameBox, gbc );
        gbc.gridwidth = 1;

        gbc.gridx = 3;
        gbc.gridy = 2;
        pane.add( spacerPanel, gbc );

        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 4;
        gbc.gridy = 2;
        pane.add( okButton, gbc );

        gbc.gridx = 5;
        gbc.gridy = 2;
        pane.add( cancelButton, gbc );

        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy = 3;
        pane.add( issueTextScrollPane, gbc );
        gbc.gridwidth = 1;

        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.gridx = 3;
        gbc.gridy = 3;
        pane.add( issueWorldBox, gbc );
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        setLayout( new GridBagLayout() );
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        this.add( pane, gbc );

        levelFilterButton.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                issueModel.setFilter( Label.LEVEL );
            }
        } );
        bugFilterButton.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                issueModel.setFilter( Label.BUG );
            }
        } );
        allFilterButton.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                issueModel.setFilter( Label.ALL );
            }
        } );
        fetchFollowButton.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                fetchFollowupComments();
            }
        } );
        okButton.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                choseWorld = true;
                setVisible( false );
            }
        } );
        cancelButton.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                setVisible( false );
            }
        } );

        setResizable( false ); // / @TODO maybe make it resize so the
                               // worldTextBox expands
        pack();
        setLocationRelativeTo( frame );
        stateChanged( null ); // show initial values for GUI items
        ghc = new GitHubClient();
        final InitFetchWorker<Void, Void> ifw =
            new InitFetchWorker<Void, Void>();
        ifw.execute();
        DotTic dt = new DotTic( (SwingWorker<Void, Void>)ifw );
        dt.start();
        setVisible( true );

    }

    public String getWorld()
    {
        GitHubIssue ghi = issueModel.getCurrentIssue();
        if ( !choseWorld || ( null == ghi ) ) // user cancelled or maybe no inet
        {
            return null;
        }
        String wrappedWorld = ghi.getCurrentWorld();
        if ( null == wrappedWorld )
        {
            return null;
        }
        return wrappedWorld;
    }

    public String generateFilename()
    {
        String title = issueModel.getCurrentIssue().getTitle();
        if ( null == title )
        {
            title = t( "Issue has no title" );
        }
        title = title.replaceAll( "\\W", "" );
        return title;
    }

    /**
     * @brief github only sends the opening comment in te first instance.
     */
    public void fetchFollowupComments()
    {
        GitHubIssue ghi = issueModel.getCurrentIssue();
        if ( null == ghi )
        {
            return;
        }
        final FetchCommentsWorker<Void, Void> fcw =
            new FetchCommentsWorker<Void, Void>( ghi );
        fcw.execute();
        statusBox.setText( t( "Fetching followup comments for" ) + " #"
            + ghi.getNumber()
            + "." );
        // Let dt repaint statusBox
        DotTic dt = new DotTic( fcw );
        dt.start();
    }

    /**
     * @brief Listens to the issue choosing spinner and world choosing spinner
     */
    @Override
    public void stateChanged( ChangeEvent e )
    {
        GitHubIssue ghi = issueModel.getCurrentIssue();
        if ( null == ghi )
        {
            return;
        }

        issueNameBox.setText( ghi.getTitle() );
        issueNameBox.repaint();

        String worldText = ghi.getCurrentWorld(); // /
        if ( null == worldText ) // some issues have no worlds
        {
            issueWorldBox.setText( "" );
        }
        {
            issueWorldBox.setText( worldText );
        }
        issueWorldBox.repaint();

        issueTextBox.setText( ghi.getBody() );
        issueTextBox.repaint();
    }

    private final class DotTic implements ActionListener
    {
        private Timer timer;
        private SwingWorker<Void, Void> worker;

        public DotTic( SwingWorker<Void, Void> ifw )
        {
            timer = new Timer( 800, this );
            worker = ifw;
        }

        public void start()
        {
            timer.start();
        }

        @Override
        public void actionPerformed( ActionEvent event )
        {
            statusBox.setText( statusBox.getText() + "." );
            statusBox.repaint();
            if ( worker.isDone() )
            {
                timer.stop();
                if ( !"".equals( ghc.getError() ) )
                {
                    statusBox.setText( ghc.getError() );
                }
                else
                {
                    statusBox.setText( statusBox.getText() + t( "Done." ) );
                }
                statusBox.repaint();
                stateChanged( null ); // update info in dialog with new info
                                      // from github
            }
        }
    }

    private final class InitFetchWorker<T, U> extends SwingWorker<Void, Void>
    {
        @Override
        protected Void doInBackground() throws Exception
        {
            ghc.initialise();
            return null;
        }
    }

    private final class IssuePageFetchWorker<T, U>
        extends
        SwingWorker<Void, Void>
    {
        String u;
        String rp;
        GitHubPageFetchNotifier ghpfn;

        public IssuePageFetchWorker( String url, String requestProperty,
            GitHubPageFetchNotifier notifier )
        {
            u = url;
            rp = requestProperty;
            ghpfn = notifier;
        }

        @Override
        protected Void doInBackground() throws Exception
        {
            String answer = HttpTools.get( u, rp );
            ghpfn.setPage( answer );
            return null;
        }
    }

    private final class FetchCommentsWorker<T, U>
        extends
        SwingWorker<Void, Void>
    {
        private GitHubIssue ghi;

        public FetchCommentsWorker( GitHubIssue i )
        {
            ghi = i;
        }

        @Override
        protected Void doInBackground() throws Exception
        {
            ghi.fetchComments( ghc );
            return null;
        }
    }
}
