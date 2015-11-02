package rabbitescape.ui.swing;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.ui.swing.Tools.*;

import org.junit.Test;

public class TestTools
{
    @Test
    public void Identical_bitmaps_compare_equal()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();

        SwingBitmap x1 = bitmapLoader.load( "x", 32 );
        SwingBitmap x2 = bitmapLoader.load( "x", 32 );

        assertThat( x1, equalTo( x2 ) );
    }

    @Test
    public void Different_bitmaps_compare_different()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();

        SwingBitmap x1 = bitmapLoader.load( "x", 32 );
        SwingBitmap x2 = bitmapLoader.load( "sixx", 32 );

        assertThat( x1, not( equalTo( x2 ) ) );
    }
}
