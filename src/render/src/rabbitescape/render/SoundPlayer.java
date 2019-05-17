package rabbitescape.render;

import java.util.List;

import rabbitescape.render.androidlike.Sound;

public class SoundPlayer
{
    public final Sound sound;

    public SoundPlayer( Sound sound )
    {
        this.sound = sound;
    }

    public void play( List<Sprite> sprites )
    {
        for ( Sprite sprite : sprites )
        {
            sound.playSound( sprite.soundEffect );
        }
    }
}
