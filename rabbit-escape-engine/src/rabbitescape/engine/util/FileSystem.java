package rabbitescape.engine.util;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface FileSystem
{
    public boolean exists( String fileName );

    @SuppressWarnings( "DuplicateThrows" )
    public String[] readLines( String fileName )
        throws FileNotFoundException, IOException;

    @SuppressWarnings( "DuplicateThrows" )
    public String read( String fileName )
        throws FileNotFoundException, IOException;

    public void write( String fileName, String contents )
        throws IOException;

    public String parent( String filePath );

    public void mkdirs( String dirPath );
}
