package rabbitescape.render;

import rabbitescape.render.androidlike.Bitmap;

public interface BitmapLoader<T extends Bitmap>
{
    T load( String fileName );
}
