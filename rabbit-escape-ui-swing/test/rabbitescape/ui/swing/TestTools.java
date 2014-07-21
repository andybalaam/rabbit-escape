package rabbitescape.ui.swing;

import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.ui.swing.Tools.*;

import org.junit.Test;

public class TestTools
{
    @Test
    public void Identical_bitmaps_compare_equal()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();

        SwingBitmap x1 = bitmapLoader.load( "/rabbitescape/ui/swing/x.png" );
        SwingBitmap x2 = bitmapLoader.load( "/rabbitescape/ui/swing/x.png" );

        assertThat( x1, equalTo( x2 ) );
    }

    @Test
    public void Different_bitmaps_compare_different()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();

        SwingBitmap x1 = bitmapLoader.load( "/rabbitescape/ui/swing/x.png" );
        SwingBitmap x2 = bitmapLoader.load( "/rabbitescape/ui/swing/xx.png" );

        assertThat( x1, not( equalTo( x2 ) ) );
    }
}
