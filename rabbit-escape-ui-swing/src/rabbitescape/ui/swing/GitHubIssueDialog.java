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

/**
 * @brief GUI elements for retrieving levels from issues.
 */
public class GitHubIssueDialog extends JDialog implements ChangeListener
{

    private static final long serialVersionUID = 1L;
    private JTextField issueNameBox = new JTextField( "Attempting to contact github." );
    private JTextArea issueTextBox = new JTextArea( "" );
    private JTextArea issueWorldBox = new JTextArea( "" );
    private IssueSpinnerModel issueModel;
    private boolean choseWorld = false;
    GitHubClient ghc = null;
    
    class IssueSpinnerModel implements SpinnerModel
    {
        private int issueIndex = 0;
        private ChangeListener changeListener;
        
        IssueSpinnerModel()
        {
        }
        
        @Override
        public void addChangeListener( ChangeListener cl )
        {
            changeListener = cl;
        }

        @Override
        public Object getNextValue()
        {
            return getValue( issueIndex - 1);
        }

        @Override
        public Object getPreviousValue()
        {
            return getValue( issueIndex + 1);
        }
        
        public Object getValue ( int index )
        {
            if( null == ghc )
            {
                return null;
            }
            GitHubIssue ghi = ghc.getIssue( index );
            if( null == ghi )
            {
                return null;
            }
            return Integer.valueOf( ghi.getNumber() );
        }
        
        public GitHubIssue getCurrentIssue()
        {
            if( null == ghc )
            {
                return null;
            }
            return ghc.getIssue(issueIndex);
        }
        

        @Override
        public Object getValue()
        {
            GitHubIssue ghi =  getCurrentIssue();
            if( null == ghi )
            {
                return null;
            }
            return Integer.valueOf(ghi.getNumber() );
        }
        
        

        @Override
        public void removeChangeListener( ChangeListener arg0 )
        {
            throw new RuntimeException(); // not used
        }

        @Override
        public void setValue( Object issueNumberIntegerObject )
        {
            Integer issueNumber = (Integer)issueNumberIntegerObject;
            if( null == issueNumber )
            {
                return;
            }
            issueIndex = ghc.getIndexOfNumber( issueNumber.intValue() );
            changeListener.stateChanged( new ChangeEvent( this ) );
        }
        
    }
    
    protected GitHubIssueDialog( Frame frame )
    {
        super( frame, true ); // arg2 sets modal
        setTitle( "Retrieve level from the GitHub Rabbit Escape issue pages." );
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        JPanel pane = new JPanel( new GridBagLayout() );
        
        issueModel = new IssueSpinnerModel();
        JSpinner issueSpinner = new JSpinner(issueModel);
        issueSpinner.setPreferredSize( new Dimension( 60, 30 ) );
        issueSpinner.addChangeListener( this );
        JRadioButton levelFilterButton = new JRadioButton( "Level" );
        JRadioButton bugFilterButton = new JRadioButton( "Bug" );
        JRadioButton allFilterButton = new JRadioButton( "All" );
        ButtonGroup filterButtons = new ButtonGroup();
        filterButtons.add( levelFilterButton );
        filterButtons.add( bugFilterButton );
        filterButtons.add( allFilterButton );
        allFilterButton.doClick(); // set preselected filter
        JPanel spacerPanel = new JPanel();
        // after packing, this gives the dialog it's width
        spacerPanel.setPreferredSize( new Dimension( 500, 2 ) ); 
        JButton okButton = new JButton( "OK" );
        JButton cancelButton = new JButton( "Cancel" );
        
        issueNameBox.setEditable( false );
        issueTextBox.setEditable( false );
        JScrollPane issueTextScrollPane = new JScrollPane(issueTextBox);
        issueTextScrollPane.setPreferredSize( new Dimension(300,300) );
        issueTextBox.setBorder( BorderFactory.createLineBorder( Color.GRAY ) );
        issueWorldBox.setEditable( false );
        issueWorldBox.setBorder( BorderFactory.createLineBorder( Color.GRAY ) );
        issueWorldBox.setFont( new Font("monospaced", Font.PLAIN, 12) );
        // after packing, this gives the dialog it's height
        issueWorldBox.setPreferredSize( new Dimension( 2, 500 ) );
        
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        pane.add( issueSpinner, gbc );
        gbc.gridheight = 1;
        
        gbc.gridx = 1;
        gbc.gridy =0;
        pane.add( levelFilterButton, gbc );
        
        gbc.gridx= 1 ;
        gbc.gridy= 1 ;
        pane.add( bugFilterButton, gbc );
        
        gbc.gridx= 1 ;
        gbc.gridy= 2 ;
        pane.add( allFilterButton, gbc );
    
        gbc.gridwidth = 3;
        gbc.gridx = 2;
        gbc.gridy = 0;
        pane.add(  issueNameBox, gbc );
        gbc.gridwidth = 1;
            
        gbc.gridx= 2 ;
        gbc.gridy= 1 ;
        pane.add( spacerPanel, gbc );
        
        gbc.fill = GridBagConstraints.NONE;
        
        gbc.gridx= 3 ;
        gbc.gridy= 1 ;
        pane.add( okButton, gbc );
        
        gbc.gridx=4;
        gbc.gridy=1;
        pane.add( cancelButton, gbc );
        
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 3;
        pane.add( issueTextScrollPane, gbc );
        gbc.gridwidth = 1;
        
        gbc.gridwidth = 3;
        gbc.gridheight = 2;
        gbc.gridx = 2;
        gbc.gridy = 2;
        pane.add( issueWorldBox, gbc );
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        
        setLayout( new GridBagLayout() );
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(pane, gbc);
        
        okButton.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                choseWorld = true;
                setVisible(false);
            }
         } );
        cancelButton.addActionListener( new ActionListener() 
        {
            public void actionPerformed( ActionEvent e )
            {
                setVisible(false);
            }
         } );

        
        setResizable( false ); /// @TODO maybe make it resize so the worldTextBox expands
        pack();
        setLocationRelativeTo( frame );
        stateChanged(null); // set initial values
        ghc = new GitHubClient();
        final NetWorker nw = new NetWorker();
        nw.execute();DotTic dt = new DotTic (nw);
        dt.start();
        setVisible( true );
        
    }
    
    public String getWorld()
    {
        GitHubIssue ghi = issueModel.getCurrentIssue();
        if( !choseWorld || (null == ghi) ) // user cancelled or maybe no inet
        {
            return null;
        }
        return ghi.getWorld( 0 ) ; /** @TODO choose which world */
    }

    public String generateFilename()
    {
        String title = issueModel.getCurrentIssue().getTitle();
        title = title.replaceAll( "\\W", "" );
        return title;
    }

    /**
     * @brief Listens to the issue choosing spinner
     * @TODO choose which world
     */
    @Override
    public void stateChanged( ChangeEvent e )
    {
        GitHubIssue ghi = issueModel.getCurrentIssue();
        if( null == ghi )
        {
            return;
        }
        
        
        issueNameBox.setText( ghi.getTitle() );
        issueNameBox.repaint();
        
        String worldText = ghi.getWorld(0); /// 
        if ( null == worldText ) // some issues have no worlds
        {
            issueWorldBox.setText( "" );
        }
        {
            issueWorldBox.setText(worldText);
        }
        issueWorldBox.repaint();
        
        issueTextBox.setText( ghi.getBody() );
        issueTextBox.repaint();
    }

    private final class DotTic implements ActionListener 
    {
        private Timer timer;
        private NetWorker netWorker;
        
        public DotTic (NetWorker netWorkerIn)
        {
            timer = new Timer(800,this);
            netWorker = netWorkerIn;
        }
        
        public void start() 
        {
            timer.start();
        }
        
        public void actionPerformed(ActionEvent event){
            issueNameBox.setText( issueNameBox.getText() + "." );
            issueNameBox.repaint();
            if (netWorker.isDone())
            {
                timer.stop();
                if(!"".equals( ghc.getError() ))
                {
                    issueNameBox.setText( ghc.getError() );
                }
                else
                {
                    issueNameBox.setText( "Done" );
                }
                issueNameBox.repaint();
                stateChanged(null); //update info in dialog with new info from github
            }
          }
    }
    
    private final class NetWorker extends SwingWorker<Void,Void> 
    {

        @Override
        protected Void doInBackground() throws Exception
        {
            ghc.initialise();
            return null;
        }
        
    }
}
