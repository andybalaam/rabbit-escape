package rabbitescape.render.androidutil;

/**
 * Receive android lifecycle events, and do the right thing to a
 * SoundResources based on them.  I.e. dispose of everything if we are leaving
 * the app, load everything if we are starting the app, pause if we are
 * being suspended, and keep playing if we're just moving between activities.
 */
public class Lifecycle2SoundEvents<ActivityT>
{
    private final SoundResources<ActivityT> sound;
    private Object currentActivity;

    public Lifecycle2SoundEvents( SoundResources<ActivityT> sound )
    {
        this.sound = sound;
        this.currentActivity = null;
    }

    public void onRestart( ActivityT activity )
    {
    }

    public void onStart( ActivityT activity )
    {
    }

    public void onCreate( ActivityT activity )
    {
    }

    public void onResume( ActivityT activity )
    {
        currentActivity = activity;
        sound.start( activity );
    }

    public void onSaveInstanceState( ActivityT activity )
    {
    }

    public void onPause( ActivityT activity )
    {
        sound.pause();
    }

    public void onStop( ActivityT activity )
    {
        if ( activity == currentActivity )
        {
            sound.dispose();
            currentActivity = null;
        }
    }

    public void onDestroy( ActivityT activity )
    {
    }
}
