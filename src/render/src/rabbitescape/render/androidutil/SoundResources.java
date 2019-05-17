package rabbitescape.render.androidutil;

public interface SoundResources<ActivityT>
{
    /**
     * If not yet already playing, load (if necessary) and
     * play the current music, and ensure all sound effects are loaded.
     */
    void start( ActivityT activity );

    /**
     * Pause the music and sounds so no sound is playing.
     */
    void pause();

    /**
     * Free up the memory used by the music and sound effects.
     */
    void dispose();
}
