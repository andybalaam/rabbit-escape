package rabbitescape.ui.swing;

import rabbitescape.render.androidlike.Sound;

public class SwingSound implements Sound
{
    private final SwingSoundCache cache;
    private boolean muted;

    public SwingSound( boolean muted )
    {
        this.cache = new SwingSoundCache();
        this.muted = muted;
    }

    @Override
    public void play( String name )
    {
        if ( muted || name == null )
        {
            return;
        }

        try
        {
            cache.get( name ).start();
        }
        catch ( Throwable t )
        {
            // All problems with sound, we just ignore
            t.printStackTrace();
        }
    }

    @Override
    public void mute( boolean muted )
    {
        this.muted = muted;
        if ( muted )
        {
            cache.dispose();
        }
    }

    @Override
    public void dispose()
    {
        cache.dispose();
    }
}
