package rabbitescape.ui.android;

import android.content.SharedPreferences;

import rabbitescape.engine.config.IConfigStorage;

public interface IAndroidConfigStorage extends IConfigStorage
{
    SharedPreferences getPrefs();
}
