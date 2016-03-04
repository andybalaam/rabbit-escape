package rabbitescape.ui.android;

import android.content.SharedPreferences;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.menu.ByNumberConfigBasedLevelsCompleted;

public class TestAndroidConfigUpgradeTo1 extends TestCase
{
    public void test_Empty_config_results_in_empty_config()
    {
        // Make a config based on preferences, with no version
        FakeSharedPreferences prefs = new FakeSharedPreferences();
        AndroidConfigUpgradeTo1 to1 = new AndroidConfigUpgradeTo1();
        FakeAndroidConfigStorage storage = new FakeAndroidConfigStorage( prefs );

        // Sanity: no prefs at start
        assertEquals( 0, prefs.getAll().size() );

        // This is what we are testing: run the upgrade
        to1.run( storage );

        // The content version was changed, and nothing was done
        assertEquals( 1, prefs.getAll().size() );
        assertEquals( "1", prefs.getString( "config.version", null ) );
    }

    public void test_Unnumbered_dirs_are_upgraded()
    {
        // Make a config based on preferences, with no version
        FakeSharedPreferences prefs = new FakeSharedPreferences();
        AndroidConfigUpgradeTo1 to1 = new AndroidConfigUpgradeTo1();
        FakeAndroidConfigStorage storage = new FakeAndroidConfigStorage( prefs );

        // Make some old-style unnumbered-directory prefs
        prefs.edit()
            .putInt( "levels.completed.medium", 7 )
            .putInt( "levels.completed.hard", 12 )
            .commit();

        // This is what we are testing: run the upgrade
        to1.run( storage );

        // The content and version were upgraded
        assertEquals( 2, prefs.getAll().size() );
        assertEquals( "1", prefs.getString( "config.version", null ) );
        assertEquals(
            "{\"hard\":12,\"medium\":7}",
            prefs.getString( "levels.completed", null )
        );
    }

    public void test_Numbered_dirs_are_upgraded()
    {
        // Make a config based on preferences, with no version
        FakeSharedPreferences prefs = new FakeSharedPreferences();
        AndroidConfigUpgradeTo1 to1 = new AndroidConfigUpgradeTo1();
        FakeAndroidConfigStorage storage = new FakeAndroidConfigStorage( prefs );

        // Make some old-style unnumbered-directory prefs
        prefs.edit()
            .putInt( "levels.completed.03_hard", 13 )
            .putInt( "levels.completed.01_easy", 8 )
            .putInt( "levels.completed.02_medium", 8 )
            .commit();

        // This is what we are testing: run the upgrade
        to1.run( storage );

        // The content and version were upgraded
        assertEquals( 2, prefs.getAll().size() );
        assertEquals( "1", prefs.getString( "config.version", null ) );
        assertEquals(
            "{\"easy\":8,\"hard\":13,\"medium\":8}",
            prefs.getString( "levels.completed", null )
        );
    }

    public void test_Levels_completed_upgrades_from_0_to_1()
    {
        SharedPreferences prefs = new FakeSharedPreferences();

        AndroidPreferencesBasedLevelsCompleted oldCompleted =
            new AndroidPreferencesBasedLevelsCompleted( prefs );

        // Set some levels completed in different dirs
        oldCompleted.setCompletedLevel( "abc", 12 );
        oldCompleted.setCompletedLevel( "02_def", 14 );
        oldCompleted.setCompletedLevel( "ghi", 2 );

        // Sanity: we get what we put in
        assertEquals( 12, oldCompleted.highestLevelCompleted( "abc" ) );
        assertEquals( 14, oldCompleted.highestLevelCompleted( "02_def" ) );
        assertEquals( 2, oldCompleted.highestLevelCompleted( "ghi" ) );

        // Sanity: unmentioned dir
        assertEquals( 0, oldCompleted.highestLevelCompleted( "jkl" ) );

        // This is what we are testing - run the upgrade
        Config upgraded = AndroidConfigSetup.createConfig( prefs, new AndroidConfigUpgradeTo1() );

        // The new levelscompleted should agree with the old
        ByNumberConfigBasedLevelsCompleted newCompleted =
            new ByNumberConfigBasedLevelsCompleted( upgraded );

        assertEquals( 12, newCompleted.highestLevelCompleted( "abc" ) );
        assertEquals( 14, newCompleted.highestLevelCompleted( "def" ) );
        assertEquals( 2, newCompleted.highestLevelCompleted( "ghi" ) );
        assertEquals( 0, newCompleted.highestLevelCompleted( "jkl" ) );
    }

    // ---

    private static class FakeAndroidConfigStorage implements IAndroidConfigStorage
    {
        private final FakeSharedPreferences prefs;

        public FakeAndroidConfigStorage( FakeSharedPreferences prefs )
        {
            this.prefs = prefs;
        }

        @Override
        public void set( String key, String value )
        {
            prefs.edit().putString( key, value ).commit();
        }

        @Override
        public String get( String key )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void save( Config config )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public SharedPreferences getPrefs()
        {
            return prefs;
        }
    }

    private static class FakeSharedPreferences implements SharedPreferences
    {
        Map<String, Object> contents = new HashMap<String, Object>();

        @Override
        public Map<String, ?> getAll()
        {
            return contents;
        }

        @Override
        public String getString( String key, String def )
        {
            String ret = (String)contents.get( key );
            if ( ret == null )
            {
                return def;
            }
            else
            {
                return ret;
            }
        }

        @Override
        public Set<String> getStringSet( String s, Set<String> strings )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getInt( String key, int def )
        {
            Integer ret = (Integer)contents.get( key );
            if ( ret == null )
            {
                return def;
            }
            else
            {
                return ret;
            }
        }

        @Override
        public long getLong( String key, long def )
        {
            Long ret = (Long)contents.get( key );
            if ( ret == null )
            {
                return def;
            }
            else
            {
                return ret;
            }
        }

        @Override
        public float getFloat( String s, float v )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean getBoolean( String s, boolean b )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains( String key )
        {
            return contents.containsKey( key );
        }

        @Override
        public Editor edit()
        {
            return new FakeEditor( contents );
        }

        @Override
        public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener onSharedPreferenceChangeListener
        )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener onSharedPreferenceChangeListener
        )
        {
            throw new UnsupportedOperationException();
        }
    }

    private static class FakeEditor implements SharedPreferences.Editor
    {
        private final Map<String, Object> contents;
        private final HashMap<String, Object> newContents;
        private List<String> toDelete;

        public FakeEditor( Map<String, Object> contents )
        {
            this.contents = contents;
            this.newContents = new HashMap<String, Object>();
            this.toDelete = new ArrayList<String>();
        }

        @Override
        public SharedPreferences.Editor putString( String key, String value )
        {
            newContents.put( key, value );
            return this;
        }

        @Override
        public SharedPreferences.Editor putStringSet(
            String s, Set<String> strings
        )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public SharedPreferences.Editor putInt( String key, int value )
        {
            newContents.put( key, value );
            return this;
        }

        @Override
        public SharedPreferences.Editor putLong( String key, long value )
        {
            newContents.put( key, value );
            return this;
        }

        @Override
        public SharedPreferences.Editor putFloat( String s, float v )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public SharedPreferences.Editor putBoolean( String s, boolean b )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public SharedPreferences.Editor remove( String key )
        {
            toDelete.add( key );
            return this;
        }

        @Override
        public SharedPreferences.Editor clear()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean commit()
        {
            for ( String del : toDelete )
            {
                contents.remove( del );
            }

            for ( Map.Entry<String, Object> entry : newContents.entrySet() )
            {
                contents.put( entry.getKey(), entry.getValue() );
            }

            boolean ret = newContents.size() > 0 || toDelete.size() > 0;

            newContents.clear();
            toDelete.clear();

            return ret;
        }

        @Override
        public void apply()
        {
            throw new UnsupportedOperationException();
        }
    }
}
