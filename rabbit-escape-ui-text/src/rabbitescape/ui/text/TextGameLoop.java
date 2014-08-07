package rabbitescape.ui.text;

import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.render.GameLoop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TextGameLoop implements GameLoop
{
    private final World world;

    public TextGameLoop( World world )
    {
        this.world = world;
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
                    System.out.println(
                        join( "\n", TextWorldManip.renderWorld( world, true ) ) );
                    Thread.sleep( 200 );
                }

                System.out.println(
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
                            System.out.println( "Didn't understand '" + inp + "'." );
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
            System.out.println( "num_vals = " + vals.length );
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
            System.out.println( e.getMessage() );
            return false;
        }

        if (
               x < 0 || x >= world.size.getWidth()
            || y < 0 || y >= world.size.getHeight()
        )
        {
            System.out.println( "x=" + x+ " y= " + y );
            return false;
        }

        if ( item.equals( "bash" ) )
        {
            world.addThing( new Token( x, y, Token.Type.bash ) );
            return true;
        }
        else
        {
            System.out.println( "Unknown item " + item );
            return false;
        }
    }

    private String input()
    {
        System.out.println( "Press return or type 'ITEM x y' to place an item." );
        System.out.println( "ITEM = bash" );

        try
        {
            return new BufferedReader( new InputStreamReader( System.in ) ).readLine();
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
            System.out.println( "You won!" );
        }
        else
        {
            System.out.println( "You lost." );
        }
    }
}
