package rabbitescape.ui.swing;

import java.io.PrintStream;
import java.util.Locale;

import javax.swing.SwingUtilities;

import rabbitescape.engine.World;
import rabbitescape.engine.util.FileSystem;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.render.GameLoop;
import rabbitescape.render.Main;

public class SwingMain extends Main
{
    private SwingMain( FileSystem fs, PrintStream out, Locale locale )
    {
        super( fs, out, locale );
    }

    public static void main( String[] args )
    {
        Main m = new SwingMain(
            new RealFileSystem(), System.out, Locale.getDefault() );

        m.run( args );
    }

    @Override
    public GameLoop createGameLoop( World world )
    {
        SwingGameInit init = new SwingGameInit();

        SwingUtilities.invokeLater( init );

        return new SwingGameLoop( init, world );
    }
}
