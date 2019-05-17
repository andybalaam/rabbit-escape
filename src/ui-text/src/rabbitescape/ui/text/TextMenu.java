package rabbitescape.ui.text;

import java.io.IOException;
import java.util.Stack;

import static rabbitescape.engine.util.Util.*;
import static rabbitescape.engine.i18n.Translation.*;

import rabbitescape.engine.CompletedLevelWinListener;
import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.MultiLevelWinListener;
import rabbitescape.engine.config.Config;
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
import rabbitescape.engine.menu.MenuItem.Type;
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

        public final String name;
        public final Type type;

        public UnknownMenuItemType( MenuItem item )
        {
            this.name = item.name;
            this.type = item.type;
        }
    }

    private final FileSystem fs;
    private final Terminal terminal;
    private final LevelsList levelsList;
    private final LevelsCompleted levelsCompleted;
    private final Stack<Menu> stack = new Stack<Menu>();

    public TextMenu( FileSystem fs, Terminal terminal, Config config )
    {
        this.fs = fs;
        this.terminal = terminal;
        this.levelsList = LoadLevelsList.load( MenuDefinition.allLevels );
        this.levelsCompleted = new ByNameConfigBasedLevelsCompleted(
            config, levelsList );
    }

    public void run()
    {
        Menu menu = MenuDefinition.mainMenu(
            levelsCompleted, levelsList, true );

        stack.clear();
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

        new TextSingleGameEntryPoint( fs, terminal.out, terminal.locale )
            .launchGame(
                new String[] { levelItem.fileName },
                winListeners( levelItem )
            );
    }

    protected LevelWinListener winListeners( LevelMenuItem item )
    {
        return new MultiLevelWinListener(
            new CompletedLevelWinListener(
                item.levelsDir, item.levelNumber, levelsCompleted ),
            new UpdateTextMenuLevelWinListener( this )
        );
    }

    private void about()
    {
        terminal.out.println(
            "\n"
            + t( AboutText.text ) + "\n"
            + "\n"
            + t( "Press Return to continue." ) + "\n"
        );
        readLine();
    }

    public MenuItem showMenu( Menu menu )
    {
        terminal.out.println( menu.intro + "\n" );
        terminal.out.println( renderItems( menu ) );

        while( true )
        {
            int chosenNum = readItemNum( menu.items.length );
            if ( chosenNum == -1 )
            {
                return null;
            }
            else
            {
                MenuItem ret = menu.items[chosenNum];
                if ( ret.enabled || TapTimer.matched )
                {
                    return ret;
                }
                else
                {
                    terminal.out.println( t( "That menu item is disabled." ) );
                }
            }
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

    public static String renderItems( Menu menu )
    {
        StringBuilder ret = new StringBuilder();

        for ( IdxObj<MenuItem> item : enumerate1( menu.items ) )
        {
            if ( item.object.hidden && !TapTimer.matched )
            {
                continue;
            }
            ret.append( renderItem( item.index, item.object ) );
        }

        return ret.toString();
    }

    private static String renderItem( int i, MenuItem item )
    {
        if ( item.enabled || TapTimer.matched )
        {
            return t(
                "${num}. ${item}\n",
                newMap(
                    "num", String.valueOf( i ),
                    "item", t( item.name, item.nameParams )
                )
            );
        }
        else
        {
            return t(
                "   ${item} (disabled)\n",
                newMap(
                    "num", String.valueOf( i ),
                    "item", t( item.name, item.nameParams )
                )
            );
        }
    }

    public void refreshEnabledItems()
    {
        Menu menu = stack.lastElement();
        menu.refresh();
    }
}
