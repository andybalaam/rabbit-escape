package rabbitescape.engine.util;

import java.io.FileNotFoundException;
import java.io.IOException;

public class NothingExistsFileSystem implements FileSystem
{
    @Override
    public boolean exists( String fileName )
    {
        return false;
    }

    @Override
    public String[] readLines( String fileName )
        throws FileNotFoundException,
        IOException
    {
        throw new UnsupportedOperationException();
    }
}
