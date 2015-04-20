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
        throws FileNotFoundException, IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String read( String filePath )
        throws FileNotFoundException, IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write( String fileName, String contents )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String parent( String filePath )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void mkdirs( String parent )
    {
        throw new UnsupportedOperationException();
    }
}
