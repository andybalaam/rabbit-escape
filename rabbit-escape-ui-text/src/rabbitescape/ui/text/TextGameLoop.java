package rabbitescape.ui.text;

import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.render.GameLoop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class TextGameLoop implements GameLoop
{
    private final World world;
    private final InputStream in;
    private final PrintStream out;

    public TextGameLoop( World world, InputStream in, PrintStream out )
    {
        this.world = world;
        this.in = in;
        this.out = out;
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
                    out.println(
                        join( "\n", TextWorldManip.renderWorld( world, true ) ) );
                    Thread.sleep( 200 );
                }

                out.println(
                    join( "\n", TextWorldManip.renderWorld( world, false ) ) );

                if ( useInput )
                {
                    boolean done = false;
                    while ( !done )
                    {
                        String inp = input();
                        done = processInput( world, inp );
                        if ( !done )
                        {
                            out.println( "Didn't understand '" + inp + "'." );
                        }
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

    private boolean processInput( World world, String input )
    {
        if ( input.equals( "" ) )
        {
            return true;
        }
        String[] vals = input.split( " +" );
        if ( vals.length != 3 )
        {
            out.println( "num_vals = " + vals.length );
            return false;
        }
        String item = vals[0];
        int x;
        int y;
        try
        {
            x = Integer.valueOf( vals[1] );
            y = Integer.valueOf( vals[2] );
        }
        catch( java.lang.NumberFormatException e )
        {
            out.println( e.getMessage() );
            return false;
        }

        if (
               x < 0 || x >= world.size.getWidth()
            || y < 0 || y >= world.size.getHeight()
        )
        {
            out.println( "x=" + x+ " y= " + y );
            return false;
        }

        if ( item.equals( "bash" ) )
        {
            world.addThing( new Token( x, y, Token.Type.bash ) );
            return true;
        }
        else
        {
            out.println( "Unknown item " + item );
            return false;
        }
    }

    private String input()
    {
        out.println( "Press return or type 'ITEM x y' to place an item." );
        out.println( "ITEM = bash" );

        try
        {
            return new BufferedReader( new InputStreamReader( in ) ).readLine();
        }
        catch (IOException e)
        {
            return "";
        }
    }

    @Override
    public void showResult()
    {
        if ( world.success() )
        {
            out.println( "You won!" );
        }
        else
        {
            out.println( "You lost." );
        }
    }
}
