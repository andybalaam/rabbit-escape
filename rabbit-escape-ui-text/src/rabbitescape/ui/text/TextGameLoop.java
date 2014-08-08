package rabbitescape.ui.text;

import static rabbitescape.engine.util.Util.*;
import static rabbitescape.engine.util.Translation.t;

import rabbitescape.engine.World;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.render.GameLoop;

public class TextGameLoop implements GameLoop
{
    private final World world;
    private final Terminal terminal;

    public TextGameLoop( World world, Terminal terminal )
    {
        this.world = world;
        this.terminal = terminal;
    }

    @Override
    public void run( String[] args )
    {
        boolean useInput = false;
        if ( args.length > 1 && args[1].equals( "--interactive" ) )
        {
            useInput = true;
        }

        while( !world.finished() )
        {
            try
            {
                if ( !useInput )
                {
                    printWorldWithState();
                    Thread.sleep( 200 );
                }

                printWorld();

                if ( useInput )
                {
                    InputHandler inputHandler =
                        new InputHandler( world, terminal );

                    while ( !inputHandler.handle() )
                    {
                    }
                }
                else
                {
                    Thread.sleep( 200 );
                }
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }

            world.step();
        }
    }

    private void printWorld()
    {
        printWorldImpl( false );
    }

    private void printWorldWithState()
    {
        printWorldImpl( true );
    }

    private void printWorldImpl( boolean showChanges )
    {
        terminal.out.println(
            join( "\n", TextWorldManip.renderWorld( world, showChanges ) ) );
    }

    @Override
    public void showResult()
    {
        if ( world.success() )
        {
            terminal.out.println( t( "You won!" ) );
        }
        else
        {
            terminal.out.println( t( "You lost." ) );
        }
    }
}
