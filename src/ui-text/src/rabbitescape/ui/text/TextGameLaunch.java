package rabbitescape.ui.text;

import static rabbitescape.engine.i18n.Translation.*;
import static rabbitescape.engine.util.Util.*;

import java.util.Map;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.solution.SandboxGame;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.util.MegaCoder;
import rabbitescape.render.GameLaunch;

public class TextGameLaunch implements GameLaunch
{
    private final SandboxGame sandboxGame;
    private final LevelWinListener winListener;
    private final Terminal terminal;

    public TextGameLaunch(
        World world, LevelWinListener winListener, Terminal terminal )
    {
        this.sandboxGame = new SandboxGame( world );
        this.winListener = winListener;
        this.terminal = terminal;
    }

    @Override
    public void run( String[] args )
    {
        boolean useInput = true;
        if ( args.length > 1 && args[1].equals( "noinput" ) )
        {
            useInput = false;
        }

        InputHandler inputHandler =
            new InputHandler( sandboxGame, terminal );

        int commandIndex = 1;
        while(
            sandboxGame.getWorld().completionState() == CompletionState.RUNNING
        )
        {
            try
            {
                if ( !useInput )
                {
                    printWorldWithState();
                    Thread.sleep( 200 );
                }

                printWorld(inputHandler);

                if ( useInput )
                {
                    //noinspection StatementWithEmptyBody
                    while ( !inputHandler.handle( commandIndex ) )
                    {
                    }
                    ++commandIndex;
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

            checkWon();
        }
        printSolution( inputHandler.solution(), true );
    }

    private void printSolution( String solution, boolean withObfuscation )
    {
        terminal.out.println(
            t( ":solution.1=${solution}", newMap( "solution", solution ) ) );

        if ( withObfuscation )
        {
            terminal.out.println(
                t(
                    ":solution.1.code=${solution}",
                    newMap( "solution", MegaCoder.encode( solution ) )
                )
            );
        }
    }

    private void checkWon()
    {
        if ( sandboxGame.getWorld().completionState() == CompletionState.WON )
        {
            winListener.won();
        }
    }

    private void printWorld( InputHandler inputHandler )
    {
        printSolution( inputHandler.solution(), false );
        printWorldImpl( false );
        printState();
    }

    private void printWorldWithState()
    {
        printWorldImpl( true );

    }

    private void printState()
    {
        terminal.out.println();

        for (
            Map.Entry<Token.Type, Integer> entry :
                sandboxGame.getWorld().abilities.entrySet()
        )
        {
            terminal.out.println(
                t(
                    "${token}: ${number_left}${selected}",
                    newMap(
                        "token", entry.getKey().name(),
                        "number_left", String.valueOf( entry.getValue() ),
                        "selected", isSelected( entry.getKey() ) ? "*" : ""
                    )
                )
            );
        }

        terminal.out.println();
    }

    private boolean isSelected( Token.Type ability )
    {
        return ability.equals( sandboxGame.getSelectedType() );
    }

    private void printWorldImpl( boolean showChanges )
    {
        String[] txt = TextWorldManip.renderWorld(
            sandboxGame.getWorld(), showChanges, true );

        terminal.out.println();
        terminal.out.println( join( "\n", txt ) );
    }

    @Override
    public void showResult()
    {
        if ( sandboxGame.getWorld().completionState() == CompletionState.WON )
        {
            terminal.out.println( t( "You won!" ) );
        }
        else
        {
            terminal.out.println( t( "You lost." ) );
        }
    }
}
