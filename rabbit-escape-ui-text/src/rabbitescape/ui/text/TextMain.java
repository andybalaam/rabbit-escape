package rabbitescape.ui.text;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.i18n.Translation;
import rabbitescape.engine.util.FileSystem;
import rabbitescape.engine.util.RealFileSystem;

import java.util.Locale;

public class TextMain
{
    private final TextMenu textMenu;

    public TextMain( FileSystem fs, Terminal terminal, Config config )
    {
        this.textMenu = new TextMenu( fs, terminal, config );
    }

    public static void main( String[] args )
    {
        if ( args.length > 0 )
        {
            TextSingleGameEntryPoint.entryPoint( args );
            return;
        }
        
        Locale locale = Locale.getDefault();
        Translation.init( locale );

        TextMain m = new TextMain(
            new RealFileSystem(),
            new Terminal( System.in, System.out, locale ),
            TextConfigSetup.createConfig()
        );

        m.run( args );
    }

    private void run( String[] args )
    {
        textMenu.run();
    }
}
