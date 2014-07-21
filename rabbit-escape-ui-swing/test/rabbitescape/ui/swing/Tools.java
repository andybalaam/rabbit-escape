package rabbitescape.ui.swing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

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

            return Arrays.equals( bytes( expected ), bytes( this.actual ) );
        }

        @Override
        public void describeTo( Description _desc )
        {
            _desc.appendText( expected.name() );

            writeToFile( "./exp.bmp", expected );
            writeToFile( "./act.bmp", expected );

            _desc.appendText(
                "\n[Comparison written to ./exp.bmp and ./act.bmp]" );
        }

        private byte[] bytes( SwingBitmap bitmap )
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            try
            {
                ImageIO.write( bitmap.image, "bmp", os );
                return os.toByteArray();
            }
            catch ( IOException e )
            {
                throw new AssertionError( e );
            }
        }

        private void writeToFile( String fileName, SwingBitmap bitmap )
        {
            try
            {
                ImageIO.write( bitmap.image, "bmp", new File( fileName ) );
            }
            catch ( IOException e )
            {
                throw new AssertionError( e );
            }
        }
    }

    public static Matcher<SwingBitmap> equalTo( final SwingBitmap expected )
    {
        return new SwingBitmapMatcher( expected );
    }
}