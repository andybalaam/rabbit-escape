package rabbitescape.ui.text;

import java.io.IOException;
import java.util.Stack;

import static rabbitescape.engine.util.Util.*;
import static rabbitescape.engine.i18n.Translation.*;

import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.menu.LevelMenuItem;
import rabbitescape.engine.menu.Menu;
import rabbitescape.engine.menu.MenuDefinition;
import rabbitescape.engine.menu.MenuItem;
import rabbitescape.engine.util.FileSystem;

public class TextMenu
{
    public static class InputError extends RabbitEscapeException
    {
        public static final long serialVersionUID = 1L;

        public InputError( Throwable e )
        {
            super( e );
        }
    }

    public static class UnknownMenuItemType extends RabbitEscapeException
    {
        public static final long serialVersionUID = 1L;

        public final MenuItem item;

        public UnknownMenuItemType( MenuItem item )
        {
            this.item = item;
        }
    }

    private final FileSystem fs;
    private final Terminal terminal;

    public TextMenu( FileSystem fs, Terminal terminal )
    {
        this.fs = fs;
        this.terminal = terminal;
    }

    public void run()
    {
        Menu menu = MenuDefinition.mainMenu;
        Stack<Menu> stack = new Stack<>();
        stack.push( menu );

        while ( true )
        {
            MenuItem item = showMenu( stack.peek() );
            if ( item == null || item.type == MenuItem.Type.QUIT )
            {
                stack.pop();
                if ( stack.empty() )
                {
                    break;
                }
            }
            else if ( item.type == MenuItem.Type.MENU )
            {
                stack.push( item.menu );
            }
            else
            {
                handleTarget( item );
            }
        }
    }

    private void handleTarget( MenuItem item )
    {
        switch ( item.type )
        {
            case ABOUT:
            {
                about();
                return;
            }
            case LEVEL:
            {
                level( item );
                return;
            }
            default:
            {
                throw new UnknownMenuItemType( item );
            }
        }
    }

    private void level( MenuItem item )
    {
        LevelMenuItem levelItem = (LevelMenuItem)item;

        new TextMain( fs, terminal.out, terminal.locale )
            .launchGame( new String[] { levelItem.fileName, "--interactive" } );
    }

    private void about()
    {
        terminal.out.println( t(
            "\n"
            + "Rabbit Escape\n"
            + "Copyright(c) 2014 by Andy Balaam\n"
            + "\n"
            + "All code released under GPL v2\n"
            + "All graphics and levels released under CC-BY-NC\n"
            + "\n"
            + "https://github.com/andybalaam/rabbit-escape\n"
            + "\n"
            + "Press Return to continue.\n"
        ) );
        readLine();
    }

    private MenuItem showMenu( Menu menu )
    {
        terminal.out.println( menu.intro + "\n" );
        terminal.out.println( renderMenu( menu ) );
        int chosenNum = readItemNum( menu.items.length );
        if ( chosenNum == -1 )
        {
            return null;
        }
        else
        {
            return menu.items[chosenNum];
        }
    }

    private int readItemNum( int length )
    {
        int num = -1;
        while ( num < 0 || num > length )
        {
            terminal.out.print(
                t(
                    "Enter a number (1-${max}), or 0 to go back: ",
                    newMap( "max", String.valueOf( length ) )
                )
            );
            String answer = readLine();
            try
            {
                num = Integer.parseInt( answer );
            }
            catch ( NumberFormatException e )
            {
                num = -1;
            }
        }
        return num - 1;
    }

    private String readLine()
    {
        try
        {
           return terminal.in.readLine();
        }
        catch ( IOException e )
        {
            throw new InputError( e );
        }
    }

    private String renderMenu( Menu menu )
    {
        StringBuilder ret = new StringBuilder();

        int i = 1;
        for ( MenuItem item : menu.items )
        {
            ret.append(
                t(
                    "${num}. ${item}\n",
                    newMap(
                        "num", String.valueOf( i ),
                        "item", t( item.name, item.nameParams )
                    )
                )
            );

            ++i;
        }

        return ret.toString();
    }
}
