package rabbitescape.ui.text;

import rabbitescape.engine.i18n.Translation;
import rabbitescape.engine.util.FileSystem;
import rabbitescape.engine.util.RealFileSystem;

import java.util.Locale;

public class TextMenuMain
{
    private final TextMenu textMenu;

    public TextMenuMain( FileSystem fs, Terminal terminal )
    {
        this.textMenu = new TextMenu( fs, terminal );
    }

    public static void main( String[] args )
    {
        Locale locale = Locale.getDefault();
        Translation.init( locale );

        TextMenuMain m = new TextMenuMain(
            new RealFileSystem(),
            new Terminal( System.in, System.out, locale )
        );

        m.run( args );
    }

    private void run( String[] args )
    {
        textMenu.run();
    }
}
