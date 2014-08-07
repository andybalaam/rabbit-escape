package rabbitescape.ui.text;

import java.io.PrintStream;
import java.util.Locale;

import rabbitescape.engine.World;
import rabbitescape.engine.util.FileSystem;
import rabbitescape.engine.util.RealFileSystem;
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
        Main m = new TextMain(
            new RealFileSystem(), System.out, Locale.getDefault() );

        m.run( args );
    }

    @Override
    public GameLoop createGameLoop( World world )
    {
        return new TextGameLoop( world, System.in, System.out );
    }
}
