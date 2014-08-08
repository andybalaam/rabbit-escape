package rabbitescape.ui.text;

import java.io.PrintStream;
import java.util.Locale;

import rabbitescape.engine.World;
import rabbitescape.engine.util.FileSystem;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.engine.util.Translation;
import rabbitescape.render.GameLoop;
import rabbitescape.render.Main;

public class TextMain extends Main
{
    public TextMain( FileSystem fs, PrintStream out, Locale locale )
    {
        super( fs, out, locale );
    }

    public static void main( String[] args )
    {
        Locale locale = Locale.getDefault();
        Translation.init( locale );

        Main m = new TextMain(
            new RealFileSystem(), System.out, locale );

        m.run( args );
    }

    @Override
    public GameLoop createGameLoop( World world )
    {
        return new TextGameLoop(
            world, new Terminal( System.in, System.out, Locale.getDefault() ) );
    }
}
