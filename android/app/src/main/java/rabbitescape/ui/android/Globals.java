package rabbitescape.ui.android;

import android.app.Activity;

import rabbitescape.render.androidutil.Lifecycle2SoundEvents;
import rabbitescape.ui.android.sound.AndroidSound;

public class Globals
{
    public static final AndroidSound sound = new AndroidSound();

    public static final Lifecycle2SoundEvents<Activity> soundEvents =
        new Lifecycle2SoundEvents<Activity>( sound );
}
