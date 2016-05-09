package rabbitescape.ui.text;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.*;

import rabbitescape.engine.menu.Menu;
import rabbitescape.engine.menu.MenuItem;

public class TestTextMenu
{
    @Test
    public void Empty_menu_has_no_items()
    {
        Menu menu = new Menu( "", new MenuItem[] {} );
        assertThat(
            TextMenu.renderItems( menu ),
            equalTo(
                ""
            )
        );
    }

    @Test
    public void Disabled_items_are_shown_as_disabled()
    {
        assertThat(
            TextMenu.renderItems( new MyMenu().m ),
            equalTo(
                  "1. a\n"
                + "   b (disabled)\n"
                + "3. c\n"
            )
        );
    }

    @Test
    public void Items_can_be_selected()
    {
        MyMenu menu = new MyMenu();

        // Ask for item number 1, then later number 3
        FakeTerminal terminal = new FakeTerminal( "1", "3" );
        TextMenu textMenu = new TextMenu( null, terminal.t, null );

        // Item number 1 was returned
        assertThat( textMenu.showMenu( menu.m ), sameInstance( menu.a ) );

        // Then item number 3 was returned
        assertThat( textMenu.showMenu( menu.m ), sameInstance( menu.c ) );
    }

    @Test
    public void Disabled_items_can_not_be_selected()
    {
        MyMenu menu = new MyMenu();

        // Ask for item number 2
        FakeTerminal terminal = new FakeTerminal( "2", "1" );
        TextMenu textMenu = new TextMenu( null, terminal.t, null );

        // The 2 was ignored, and the 1 was listened to
        assertThat( textMenu.showMenu( menu.m ), sameInstance( menu.a ) );

        assertThat(
            terminal.out.toString(),
            equalTo(
                  "\n"
                + "\n"
                + "1. a\n"
                + "   b (disabled)\n"
                + "3. c\n"
                + "\n"
                + "Enter a number (1-3), or 0 to go back: "
                + "That menu item is disabled.\n"
                + "Enter a number (1-3), or 0 to go back: "
            )
        );
    }

    // ---

    /**
     * Make a menu containing 3 items, one of which is disabled.
     */
    private static class MyMenu
    {
        public final MenuItem a;
        public final MenuItem b;
        public final MenuItem c;
        public final Menu m;

        public MyMenu()
        {
            this.a = new MenuItem( "a", MenuItem.Type.ABOUT, true, false );
            this.b = new MenuItem( "b", MenuItem.Type.ABOUT, false, false );
            this.c = new MenuItem( "c", MenuItem.Type.ABOUT, true, false );
            this.m = new Menu( "", new MenuItem[] { a, b, c } );
        }
    }
}
