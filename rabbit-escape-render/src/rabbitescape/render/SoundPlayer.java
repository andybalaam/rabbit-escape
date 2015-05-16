package rabbitescape.render;

import java.util.List;

import rabbitescape.render.androidlike.Bitmap;
import rabbitescape.render.androidlike.Sound;

public class SoundPlayer<T extends Bitmap>
{
    public final Sound sound;

    public SoundPlayer( Sound sound )
    {
        this.sound = sound;
    }

    public void play( List<Sprite<T>> sprites )
    {
        for ( Sprite<T> sprite : sprites )
        {
            sound.play( sprite.soundEffect );
        }
    }
}
