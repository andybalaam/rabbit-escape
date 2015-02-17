package rabbitescape.ui.android;

import android.content.SharedPreferences;

import rabbitescape.engine.menu.LevelsCompleted;

public class AndroidPreferencesBasedLevelsCompleted implements LevelsCompleted
{
    private final SharedPreferences m_prefs;

    public AndroidPreferencesBasedLevelsCompleted( SharedPreferences _prefs )
    {
        m_prefs = _prefs;
    }

    @Override
    public int highestLevelCompleted( String levelsDir )
    {
        return m_prefs.getInt( "levels.completed." + levelsDir, 0 );
    }

    @Override
    public void setCompletedLevel( String levelsDir, int i )
    {
        m_prefs.edit().putInt( "levels.completed." + levelsDir, i ).commit();
    }
}
