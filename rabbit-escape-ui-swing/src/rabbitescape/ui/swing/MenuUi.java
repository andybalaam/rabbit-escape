package rabbitescape.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import static rabbitescape.engine.util.Util.*;
import static rabbitescape.engine.i18n.Translation.*;
import static rabbitescape.ui.swing.SwingConfigSetup.*;

import rabbitescape.engine.CompletedLevelWinListener;
import rabbitescape.engine.IgnoreLevelWinListener;
import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.MultiLevelWinListener;
import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigKeys;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.config.TapTimer;
import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.menu.AboutText;
import rabbitescape.engine.menu.ByNameConfigBasedLevelsCompleted;
import rabbitescape.engine.menu.LevelMenuItem;
import rabbitescape.engine.menu.LevelsCompleted;
import rabbitescape.engine.menu.LevelsList;
import rabbitescape.engine.menu.LoadLevelsList;
import rabbitescape.engine.menu.Menu;
import rabbitescape.engine.menu.MenuDefinition;
import rabbitescape.engine.menu.MenuItem;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.engine.util.Util.IdxObj;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.androidlike.Sound;

public class MenuUi
{
    public static class UnknownMenuItemType extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String name;
        public final MenuItem.Type type;

        public UnknownMenuItemType( MenuItem item )
        {
            this.name = item.name;
            this.type = item.type;
        }
    }

    private class ButtonListener implements ActionListener
    {
        private final MenuItem item;

        public ButtonListener( MenuItem item )
        {
            this.item = item;
        }

        @Override
        public void actionPerformed( ActionEvent event )
        {
            switch ( item.type )
            {
                case MENU:
                {
                    SwingUtilities.invokeLater( new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            stack.add( item.menu );
                            placeMenu();
                        }
                    } );
                    return;
                }
                case ABOUT:
                {
                    about();
                    return;
                }
                case LEVEL:
                {
                    level( (LevelMenuItem)item );
                    return;
                }
                case LOAD:
                {
                    chooseLevel();
                    return;
                }
                case GITHUB_ISSUE:
                {
                    chooseIssue();
                    return;
                }
                case QUIT:
                {
                    frame.exit();
                    return;
                }
                case DEMO:
                {
                    return;
                }
                default:
                {
                    throw new UnknownMenuItemType( item );
                }
            }
        }
    }

    private static final Color backgroundColor = Color.WHITE;
    private static final Color buttonColor = Color.LIGHT_GRAY;

    private final RealFileSystem fs;
    private final PrintStream out;
    private final Locale locale;
    private final BitmapCache<SwingBitmap> bitmapCache;

    private final Stack<Menu> stack;
    private final Config uiConfig;
    private final MainJFrame frame;
    private final Sound sound;

    private final JPanel menuPanel;
    private final LevelsCompleted levelsCompleted;
    private SideMenu sidemenu;

    public MenuUi(
        RealFileSystem fs,
        PrintStream out,
        Locale locale,
        BitmapCache<SwingBitmap> bitmapCache,
        Config uiConfig,
        MainJFrame frame,
        Sound sound
    )
    {
        this.fs = fs;
        this.out = out;
        this.locale = locale;
        this.bitmapCache = bitmapCache;
        this.stack = new Stack<>();
        this.uiConfig = uiConfig;
        this.frame = frame;
        this.sound = sound;
        this.menuPanel = new JPanel( new GridBagLayout() );

        LevelsList levelsList = LoadLevelsList.load(
            MenuDefinition.allLevels );

        this.levelsCompleted = new ByNameConfigBasedLevelsCompleted(
            uiConfig, levelsList );

        stack.push(
            MenuDefinition.mainMenu( this.levelsCompleted, levelsList, true )
        );

        init();
    }

    public void init()
    {
        Container contentPane = frame.getContentPane();

        contentPane.setLayout( new BorderLayout( 4, 4 ) );

        sidemenu = new SideMenu(
            contentPane,
            bitmapCache,
            new Dimension( 32, 32 ),
            uiConfig,
            backgroundColor
        );

        JScrollPane scrollPane = new JScrollPane( menuPanel  );
        contentPane.add( scrollPane, BorderLayout.CENTER );
        contentPane.setBackground( backgroundColor );
        scrollPane.setBackground( backgroundColor );
        scrollPane.getVerticalScrollBar().setUnitIncrement( 16 );
        menuPanel.setBackground( backgroundColor );

        placeMenu();

        frame.setBoundsFromConfig();

        frame.setTitle( t( "Rabbit Escape" ) );

        frame.pack();
        frame.setVisible( true );

        initListeners();
    }

    private void uninit()
    {
        frame.getContentPane().removeAll();
    }

    public void placeMenu()
    {
        Menu menu = stack.lastElement();

        menuPanel.removeAll();

        Dimension buttonSize = new Dimension( 300, 40 );

        JLabel label = new JLabel( t( menu.intro ) );
        label.setHorizontalAlignment( SwingConstants.CENTER );
        label.setForeground( Color.RED );
        label.setPreferredSize( buttonSize );
        menuPanel.add( label, constraints( 0 ) );

        for ( IdxObj<MenuItem> item : enumerate1( menu.items ) )
        {
            if ( item.object.hidden && !TapTimer.matched )
            {
                continue;
            }
            JButton button = new JButton(
                t( item.object.name, item.object.nameParams ) );

            button.setBackground( buttonColor );
            button.addActionListener( new ButtonListener( item.object ) );
            button.setVisible( true );
            button.setEnabled( item.object.enabled || TapTimer.matched );
            button.setPreferredSize( buttonSize );
            menuPanel.add( button, constraints( item.index ) );
        }

        sound.setMusic( "tryad-let_them_run" );

        frame.repaint();
        frame.revalidate();
    }

    public void refreshEnabledItems()
    {
        Menu menu = stack.lastElement();
        menu.refresh();
    }

    private GridBagConstraints constraints( int i )
    {
        return new GridBagConstraints(
            0,
            i,
            1,
            1,
            1.0,
            1.0,
            GridBagConstraints.CENTER,
            GridBagConstraints.NONE,
            new Insets( 10, 10, 10, 10 ),
            0,
            0
        );
    }

    private void chooseIssue()
    {
        GitHubIssueDialog id = new GitHubIssueDialog( frame );
        String world = id.getWorld();
        if( null == world )
        {
            return; // User clicked cancel, or selected issue with no world
        }
        String path = ConfigTools.getString(
            uiConfig, ConfigKeys.CFG_LOAD_LEVEL_PATH );

        File nameCandidate = new File ( path + File.separator +
                                        id.generateFilename() + ".rel" );
        int version = 0;
        String filename = id.generateFilename();
        while( nameCandidate.exists() )
        {
            nameCandidate = new File (
                path + File.separator + filename + "." + ( version++ ) + ".rel"
            );
        }
        PrintWriter out;
        try
        {
            out = new PrintWriter( nameCandidate );
            out.print( world );
            out.close();
            playLevel( 
                nameCandidate.getAbsolutePath(),
                new IgnoreLevelWinListener() 
            );
        }
        catch ( FileNotFoundException e ) /// @TODO fix exception handling
        {
            e.printStackTrace();
        }
    }

    private void chooseLevel()
    {
        String filename = chooseLevelFilename();
        if ( filename == null )
        {
            return;  // The user cancelled or closed the dialog
        }

        playLevel( filename, new IgnoreLevelWinListener() );
    }

    private String chooseLevelFilename()
    {
        String path = ConfigTools.getString(
            uiConfig, ConfigKeys.CFG_LOAD_LEVEL_PATH );

        final JFileChooser fc = new JFileChooser();
        final FileNameExtensionFilter relFilter = new FileNameExtensionFilter(
            t( "Rabbit Escape Level (*.rel)" ),
            "rel"
        );
        fc.setDialogTitle( t( "Open a level file" ) );

        fc.setCurrentDirectory( new File( path ) );
        fc.addChoosableFileFilter( relFilter );
        fc.setFileFilter( relFilter );
        int chooserVal = fc.showOpenDialog( frame );

        if ( JFileChooser.APPROVE_OPTION != chooserVal )
        {
            return null; // The user cancelled or closed the dialog
        }

        String filename = fc.getSelectedFile().getAbsolutePath();

        ConfigTools.setString(
            uiConfig,
            ConfigKeys.CFG_LOAD_LEVEL_PATH,
            fc.getCurrentDirectory().getAbsolutePath()
        );
        uiConfig.save();

        return filename;
    }

    private void level( final LevelMenuItem item )
    {
        playLevel( item.fileName, winListeners( item ) );
    }

    private void playLevel(
        final String filename, final LevelWinListener levelWinListener )
    {
        new SwingWorker<Void, Void>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                uninit();

                new SwingSingleGameEntryPoint(
                    fs,
                    out,
                    locale,
                    bitmapCache,
                    uiConfig,
                    frame,
                    sound,
                    MenuUi.this,
                    SwingGameLaunch.NOT_DEMO_MODE,
                    false
                ).launchGame(
                    new String[] { filename },
                    levelWinListener
                );

                return null;
            }
        }.execute();
    }

    protected LevelWinListener winListeners( LevelMenuItem item )
    {
        return new MultiLevelWinListener(
            new CompletedLevelWinListener(
                item.levelsDir, item.levelNumber, levelsCompleted ),
            new UpdateSwingMenuLevelWinListener( this )
        );
    }

    private void about()
    {
        JTextPane text = new JTextPane();
        text.setText( t( AboutText.text ) );
        text.setBackground( null );

        JOptionPane.showMessageDialog(
            frame,
            text,
            t( "About Rabbit Escape" ),
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void back()
    {
        stack.pop();

        if ( stack.empty() )
        {
            frame.exit();
        }
        else
        {
            SwingUtilities.invokeLater(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        placeMenu();
                    }
                }
            );
        }
    }

    private void setMuted( boolean muted )
    {
        ConfigTools.setBool( uiConfig, CFG_MUTED, muted );
        uiConfig.save();
        sound.mute( muted );
    }

    private void initListeners()
    {
        sidemenu.mute.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                setMuted( sidemenu.mute.isSelected() );
            }
        } );

        MenuTools.clickOnKey( sidemenu.mute, "mute", KeyEvent.VK_M );

        sidemenu.back.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                back();
            }
        } );

        MenuTools.clickOnKey( sidemenu.back, "back", KeyEvent.VK_ESCAPE );

        sidemenu.exit.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                frame.exit();
            }
        } );

        MenuTools.clickOnKey( sidemenu.exit, "quit", KeyEvent.VK_Q );
    }
}
