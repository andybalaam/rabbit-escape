package rabbitescape.engine.util;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface FileSystem
{
    boolean exists( String fileName );
    String[] readLines( String fileName )
        throws FileNotFoundException, IOException;
}
