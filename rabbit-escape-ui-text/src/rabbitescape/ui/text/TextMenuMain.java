package rabbitescape.ui.text;

import rabbitescape.engine.i18n.Translation;

import java.util.Locale;

public class TextMenuMain
{
    private final TextMenu textMenu;

    public TextMenuMain( Terminal terminal )
    {
        this.textMenu = new TextMenu( terminal );
    }

    public static void main( String[] args )
    {
        Locale locale = Locale.getDefault();
        Translation.init( locale );

        TextMenuMain m = new TextMenuMain(
            new Terminal( System.in, System.out, locale ) );

        m.run( args );
    }

    private void run( String[] args )
    {
        textMenu.run();
    }
}
