package rabbitescape.ui.swing;

import rabbitescape.render.androidlike.Sound;

/**
 * Play absolutely no sounds.
 */
public class SwingNullSound implements Sound
{
    @Override
    public void setMusic( String music )
    {
    }

    @Override
    public void playSound( String soundEffect )
    {
    }

    @Override
    public void mute( boolean muted )
    {
    }

    @Override
    public void dispose()
    {
    }
}
