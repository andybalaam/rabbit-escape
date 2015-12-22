package rabbitescape.render;

import rabbitescape.render.androidlike.Bitmap;

public class ScaledBitmap<T extends Bitmap> implements SizedRecyclable
{
    private final BitmapScaler<T> scaler;
    private final BitmapLoader<T> loader;
    private final String fileName;

    private T unscaledBitmap;
    private T bitmap;

    public int unscaledTileSize;
    public int tileSize;

    public ScaledBitmap(
        BitmapScaler<T> scaler,
        BitmapLoader<T> loader,
        String fileName
    )
    {
        this.scaler = scaler;
        this.loader = loader;
        this.fileName = fileName;

        this.unscaledBitmap = null;
        this.bitmap = null;

        this.unscaledTileSize = -1;
        this.tileSize = -1;
    }

    public T bitmap( int tileSize )
    {
        scaleTo( tileSize );
        return bitmap;
    }

    @Override
    public long size()
    {
        long ret = 0;
        if ( bitmap != null )
        {
            ret += bitmap.getByteCount();
        }
        if ( unscaledBitmap != null && unscaledBitmap != bitmap )
        {
            ret += unscaledBitmap.getByteCount();
        }
        // Hack to make even not-yet-initialised images need some space
        return ret > 0 ? ret : 1;
    }

    @Override
    public void recycle()
    {
        replaceBitmap( null );
        replaceUnscaledBitmap( null );
    }

    private void scaleTo( int tileSize )
    {
        if ( this.tileSize == tileSize )
        {
            return;
        }

        int desiredUnscaledTileSize = loader.sizeFor( tileSize );
        if ( unscaledTileSize != desiredUnscaledTileSize )
        {
            unscaledTileSize = desiredUnscaledTileSize;
            replaceUnscaledBitmap(
                loader.load( fileName, desiredUnscaledTileSize ) );
        }

        this.tileSize = tileSize;
        if ( tileSize == unscaledTileSize )
        {
            replaceBitmap( unscaledBitmap );
        }
        else
        {
            double scale = tileSize / (double)unscaledTileSize;
            replaceBitmap( scaler.scale( unscaledBitmap, scale ) );
        }
    }

    private void replaceUnscaledBitmap( T newUnscaledBitmap )
    {
        if ( unscaledBitmap != null )
        {
            unscaledBitmap.recycle();
        }
        unscaledBitmap = newUnscaledBitmap;
    }

    private void replaceBitmap( T newBitmap )
    {
        if ( bitmap != null && bitmap != unscaledBitmap )
        {
            bitmap.recycle();
        }
        bitmap = newBitmap;
    }
}
