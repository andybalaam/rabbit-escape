package rabbitescape.render;

import rabbitescape.render.androidlike.Bitmap;

public interface BitmapLoader<T extends Bitmap>
{
    /**
     * @param fileName the thing to load
     * @param tileSize the tileSize you will use: MUST be a value got from a
     *        previous call to sizeFor().
     * @return a loaded bitmap
     */
    T load( String fileName, int tileSize );

    /**
     * @return the recommended tile size to ask for if you're going to
     *         scale your image to the supplied tileSize.
     */
    int sizeFor( int tileSize );
}
