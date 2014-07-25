package rabbitescape.ui.swing;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class Tools
{
    public static class SwingBitmapMatcher extends BaseMatcher<SwingBitmap>
    {
        private final SwingBitmap expected;
        private SwingBitmap actual;

        public SwingBitmapMatcher( SwingBitmap expected )
        {
            this.expected = expected;
            this.actual = null;
        }

        @Override
        public boolean matches( Object actual )
        {
            if ( !( actual instanceof SwingBitmap ) )
            {
                throw new AssertionError(
                    actual + " is not a SwingBitmap" );
            }

            this.actual = (SwingBitmap)actual;

            return Arrays.equals( pixels( expected ), pixels( this.actual ) );
        }

        @Override
        public void describeTo( Description _desc )
        {
            _desc.appendText( expected.name() );

            writeToFile( "./exp.png", this.expected );
            writeToFile( "./act.png", this.actual );

            _desc.appendText(
                "\n[Comparison written to ./exp.png and ./act.png]" );
        }

        private int[] pixels( SwingBitmap bitmap )
        {
            BufferedImage img = bitmap.image;

            Raster r = img.getData();

            int w = r.getWidth();
            int h = r.getHeight();

            int[] ret = new int[r.getNumBands() * w * h];

            return r.getPixels( r.getMinX(), r.getMinY(), w, h, ret );
        }

        private void writeToFile( String fileName, SwingBitmap bitmap )
        {
            try
            {
                boolean written = ImageIO.write(
                    bitmap.image, "png", new File( fileName ) );

                assertThat( written, is( true ) );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
                throw new AssertionError( e );
            }
            catch ( Throwable e )
            {
                e.printStackTrace();
            }
        }
    }

    public static Matcher<SwingBitmap> equalTo( final SwingBitmap expected )
    {
        return new SwingBitmapMatcher( expected );
    }
}