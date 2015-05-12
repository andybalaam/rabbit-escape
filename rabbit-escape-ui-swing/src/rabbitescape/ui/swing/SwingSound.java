package rabbitescape.ui.swing;

import rabbitescape.render.androidlike.Sound;

public class SwingSound implements Sound
{
    private final SwingSoundCache cache;

    public SwingSound()
    {
        this.cache = new SwingSoundCache();
    }

    @Override
    public void play( String name )
    {
        if ( name == null )
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
}
