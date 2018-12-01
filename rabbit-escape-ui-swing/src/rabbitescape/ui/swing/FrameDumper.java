package rabbitescape.ui.swing;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import rabbitescape.engine.util.RealFileSystem;

public class FrameDumper
{
    private static final String sep = File.separator;

    public final String recordingDir;
    public final boolean active;

    private int frameI;

    public FrameDumper()
    {
        this.active = true;
        RealFileSystem fs = new RealFileSystem();

        frameI = 0;

        int dirCount = 0;
        String recordingDirTmp;
        do
        {
            recordingDirTmp = String.format( ".%srecordings%s%04d%s",
                     sep, sep, dirCount++, sep );
        }
        while ( fs.exists( recordingDirTmp ) );

        recordingDir = recordingDirTmp;

        fs.mkdirs( recordingDir );
        System.out.printf( 
            "Dumping %sanim_test_frame_<set>_<frame>.png:",
            recordingDir 
        );
    }

    private FrameDumper( boolean active )
    {
        this.active = active;
        this.recordingDir = null;
    }

    public static FrameDumper createInactiveDumper()
    {
        return new FrameDumper( false );
    }

    public void dump( java.awt.Canvas canvas, BufferedDraw drawFrame )
    {
        if ( !active )
        {
            return;
        }
        dump( canvas, drawFrame, String.format( "%07d", frameI++ ) );
    }

    public void dump( 
        java.awt.Canvas canvas, 
        BufferedDraw drawFrame,
        String frameID 
    )
    {
        if ( !active )
        {
            return;
        }
        BufferedImage im = new BufferedImage( 
                canvas.getWidth(), 
                canvas.getHeight(),
                BufferedImage.TYPE_INT_ARGB 
        );
        drawFrame.draw( (Graphics2D)im.getGraphics() );
        String fileName = String.format( 
            "%sframe_%s.png", 
            recordingDir,
            frameID 
        );
        System.out.printf( " " + frameID );
        try
        {
            ImageIO.write( im, "PNG", new File( fileName ) );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            System.exit( 1 );
        }
    }

    public void framesToGif()
    {
        if ( !active )
        {
            return;
        }
        String cmd = String.format(
            "convert -delay 10 -loop 0 %s*.png %sanimation.gif",
            recordingDir, 
            recordingDir 
        );
        try
        {
            Runtime.getRuntime().exec( cmd );
            System.out.printf( "Wrote:  %sanimation.gif\n", recordingDir );
        }
        catch ( IOException e )
        {
            System.err.println(
                "convert from ImageMagick is required to make animated gifs" );
            e.printStackTrace();
        }
    }
}
