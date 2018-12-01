package rabbitescape.ui.swing;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import static rabbitescape.engine.i18n.Translation.t;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.config.Config;
import rabbitescape.engine.solution.PlaceTokenAction;
import rabbitescape.engine.solution.SolutionDemo;
import rabbitescape.engine.solution.SolutionInterpreter;
import rabbitescape.engine.solution.SolutionRecorder;
import rabbitescape.engine.solution.SolutionRecorderTemplate;

import rabbitescape.engine.util.Util;
import rabbitescape.render.GameLaunch;
import rabbitescape.render.androidlike.Sound;
import rabbitescape.render.gameloop.GameLoop;
import rabbitescape.render.gameloop.GeneralPhysics;
import rabbitescape.render.gameloop.Physics;
import rabbitescape.render.gameloop.WaterAnimation;
import rabbitescape.ui.swing.SwingGameInit.WhenUiReady;

public class SwingGameLaunch implements GameLaunch
{
    /**
     * A loop that just draws the game window when it's behind the
     * intro dialog.
     */
    class MiniGameLoop implements Runnable
    {
        public boolean running = true;

        @Override
        public void run()
        {
            while ( running )
            {
                graphics.draw( physics.frameNumber() );
                try
                {
                    Thread.sleep( 500 );
                }
                catch ( InterruptedException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static final String NOT_DEMO_MODE = "NOT_DEMO_MODE";
    public final World world;
    private final GeneralPhysics physics;
    public final SwingGraphics graphics;
    private final GameUi jframe;

    private final GameLoop loop;
    private final MainJFrame frame;
    public final SolutionRecorderTemplate solutionRecorder;
    private final SwingPlayback swingPlayback;
    private final FrameDumper frameDumper;

    /**
     * @param solutionIndex natural number values indicate demo mode. It is
     *                      the index of the solution from the rel file to play.
     */
    public SwingGameLaunch(
        SwingGameInit init,
        World world,
        LevelWinListener winListener,
        Sound sound,
        Config config,
        PrintStream debugout,
        String solution,
        boolean frameDumping
    )
    {
        this.world = world;

        SolutionInterpreter solutionInterpreter =
            createSolutionInterpreter( solution, world );

        this.frame = init.frame;
        this.solutionRecorder = new SolutionRecorder();
        if ( frameDumping )
        {
            this.frameDumper = new FrameDumper();
        }
        else
        {
            this.frameDumper = FrameDumper.createInactiveDumper();
        }
        this.swingPlayback = new SwingPlayback( this );
        WaterAnimation waterAnimation = new WaterAnimation( world, config ) ;
        this.physics = new GeneralPhysics(
            world,
            waterAnimation,
            winListener,
            false,
            solutionRecorder,
            solutionInterpreter,
            swingPlayback,
            frameDumping
        );
        // This blocks until the UI is ready:
        WhenUiReady uiPieces = init.waitForUi.waitForUi();

        this.jframe = uiPieces.jframe;
        this.graphics = new SwingGraphics(
            world,
            uiPieces.jframe,
            uiPieces.bitmapCache,
            sound,
            frameDumper,
            waterAnimation
        );

        // Used for redraw after window is resized.
        frame.setGraphics( graphics );

        jframe.setGameLaunch( this );

        sound.setMusic( world.music );

        loop = new GameLoop( 
            new SwingInput(), 
            physics, 
            waterAnimation,
            graphics, 
            config, 
            debugout 
        );
    }

    public GameUi getUi()
    {
        return jframe;
    }

    public void toggleSpeed()
    {
        physics.fast = !physics.fast;
    }

    private static SolutionInterpreter createSolutionInterpreter(
        String solution, 
        World world 
    )
    {
        if ( solution.equals( NOT_DEMO_MODE ) )
        {
            return SolutionInterpreter.getNothingPlaying();
        }
        SolutionDemo demo = new SolutionDemo( solution, world );
        return new SolutionInterpreter( demo.solution );
    }

    public void stop()
    {
        loop.pleaseStop();
    }

    @Override
    public void run( String[] args )
    {
        showIntroDialog();
        // After the dialog has gone, return keyboard
        // focus so keystrokes are not lost.
        frame.getRootPane().grabFocus();
        loop.run();
        System.out.println( solutionRecorder.getRecord() );
    }

    private static class AnswerHolder
    {
        public int answer;
    }

    /**
     * Must not be called from within event loop.
     */
    private int showDialog(
        final String title, 
        final Object message, 
        final Object[] options 
    )
    {
        final AnswerHolder holder = new AnswerHolder();

        runSwingCodeWithGameLoopBehind(
            new Runnable()
            {
                @Override
                public void run()
                {
                    holder.answer = JOptionPane.showOptionDialog(
                        frame,
                        message,
                        title,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[ options.length - 1 ]
                    );
                }
            }
        );

        return holder.answer;
    }

    /**
     * Must be called from within the event loop.
     */
    private boolean askExplodeAll()
    {
        MiniGameLoop bgDraw = new MiniGameLoop();

        new Thread( bgDraw ).start();

        try
        {
            String[] buttons = new String[] { t( "Cancel" ), t( "Explode!" ) };

            int ret = JOptionPane.showOptionDialog(
                frame,
                t( "Do you want to explode your rabbits?" ),
                t( "Explode all rabbits?" ),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                buttons,
                buttons[1]
            );

            return ( ret == 1 );
        }
        finally
        {
            bgDraw.running = false;
        }
    }

    private void runSwingCodeWithGameLoopBehind( Runnable doRun )
    {
        MiniGameLoop bgDraw = new MiniGameLoop();

        new Thread( bgDraw ).start();

        try
        {
            SwingUtilities.invokeAndWait( doRun );
        }
        catch ( InvocationTargetException e )
        {
            throw new RuntimeException( e );
        }
        catch ( InterruptedException e )
        {
            // Continue if interrupted
        }
        finally
        {
            bgDraw.running = false;
        }
    }

    /**
     * Not called from within event loop.
     */
    private void showIntroDialog()
    {
        if (inDemoMode())
        {
            return;
        }

        Util.Function<String, String> insertNewlines =
            new Util.Function<String, String>()
        {
            @Override
            public String apply( String inp )
            {
                return Util.wrapToNewline( inp, DialogText.lineLength );
            }
        };

        showDialogs(
            world.name,
            Util.concat(
                new Object[] { DialogText.introText( this.frame, world ) },
                Util.map(
                    insertNewlines,
                    world.hints,
                    new String[3]
                )
            )
        );
    }

    private void showDialogs( String title, Object[] messages )
    {
        // Keep showing dialogs until we click start
        int retVal = 0;
        while ( retVal == 0 )
        {
            for ( int i = 0; i < messages.length; ++i )
            {
                if ( "".equals( messages[i] ) )
                {
                    // No more messages
                    break;
                }

                Object nextMessage = nextMessage( messages, i );
                Object[] options = nextOptions( nextMessage, i );
                retVal = showDialog(
                    title,
                    messages[i],
                    options
                );
                if ( options.length == 1 )
                {
                    // There was only 1 option - we clicked Start
                    retVal = 1;
                }

                if ( retVal != 0 )
                {
                    // We clicked Start or closed dialog
                    break;
                }
            }
        }
    }

    private Object[] nextOptions( Object nextMessage, int i )
    {
        if ( "".equals( nextMessage ) )
        {
            if ( i == 0 )
            {
                // There were no hints at all
                return new Object[] { t( "Start" ) };
            }
            else
            {
                // Go back to level description
                return new Object[] { t( "Info" ), t( "Start" ) };
            }
        }
        else
        {
            // Another hint to come
            return new Object[] { t( hintName( i ) ), t( "Start" ) };
        }
    }

    private String hintName( int i )
    {
        if ( i == 0 )
        {
            return "Hint";
        }
        else
        {
            return "Hint " + ( i + 1 );
        }
    }

    private Object nextMessage( Object[] messages, int i )
    {
        if ( i >= messages.length - 1 )
        {
            return "";
        }
        else
        {
            return messages[ i + 1 ];
        }
    }

    /**
     * Not called from within event loop.
     */
    private void showWonDialog()
    {
        if (inDemoMode())
        {
            try
            {
                Thread.sleep( 2000 );
            }
            catch ( InterruptedException e )
            {
                // Ignore
            }
            return;
        }

        showDialog(
            t( "You won!" ),
            t(
                "Saved: ${num_saved}  Needed: ${num_to_save}",
                DialogText.statsValues( world )
            ),
            new Object[] { t( "Ok" ) }
        );
    }

    /**
     * Not called from within event loop.
     */
    private void showLostDialog()
    {
        if (inDemoMode())
        {
            try
            {
                Thread.sleep( 2000 );
            }
            catch ( InterruptedException e )
            {
                // Ignore
            }
            return;
        }

        showDialog(
            t( "You lost!" ),
            t(
                "Saved: ${num_saved}  Needed: ${num_to_save}",
                DialogText.statsValues( world )
            ),
            new Object[] { t( "Ok" ) }
        );
    }

    @Override
    public void showResult()
    {
        switch ( world.completionState() )
        {
            case WON:
            {
                showWonDialog();
                break;
            }
            case LOST:
            {
                showLostDialog();
                break;
            }
            default:
            {
                // Maybe the user clicked back - do nothing here
            }
        }

        jframe.exit();
    }

    public void checkExplodeAll()
    {
        world.setPaused( true );

        boolean explode = askExplodeAll();

        world.setPaused( false );

        if ( explode )
        {
            world.changes.explodeAllRabbits();
        }
    }

    public int addToken( int tileX, int tileY, Token.Type ability )
    {
        int prev = world.abilities.get( ability );
        int now = physics.addToken( tileX, tileY, ability );
        if ( now != prev )
        {
            graphics.playSound( "place_token" );
        }
        if ( prev != now )
        {
            solutionRecorder.append( new PlaceTokenAction( tileX, tileY ) );
        }
        return now;
    }

    public Map<Token.Type, Integer> getAbilities()
    {
        return world.abilities;
    }

    public void addStatsChangedListener( Physics.StatsChangedListener listener )
    {
        physics.addStatsChangedListener( listener );
    }

    private boolean inDemoMode()
    {
        return ( !physics.solutionInterpreter.emptySteps );
    }
}
