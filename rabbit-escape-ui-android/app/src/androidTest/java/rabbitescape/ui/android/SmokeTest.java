package rabbitescape.ui.android;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import net.artificialworlds.rabbitescape.R;

import java.util.HashMap;
import java.util.Map;

import rabbitescape.engine.Token;
import rabbitescape.engine.menu.MenuItem;

public class SmokeTest extends ActivityInstrumentationTestCase2<AndroidMenuActivity>
{
    public SmokeTest()
    {
        super( AndroidMenuActivity.class );
    }

    /**
     * Appears to work on old Android versions, and has trouble on newer ones.
     * I suspect the hack I used to do "back" is to blame.
     *
     * To run, ensure the device is unlocked, and run as an Android Test.
     */
    public void test_Smoke()
    {
        AndroidUiDriver driver = new AndroidUiDriver( this );

        // Click some menu items
        driver.clickMenuItem( 0, "Start Game" );
        driver.clickMenuItem( 1, "Medium" );

        // Navigate around menus, including using back
        driver.back( AndroidMenuActivity.class );
        driver.clickMenuItem( 1, "Medium" );

        // Open a level
        MenuItem levelItem = driver.clickMenuItem( 0, "Level ${number}" );
        assertEquals( "1", levelItem.nameParams.get( "number" ) );
        driver.clickPositive( "Start" );

        // Lose a level by exploding all
        driver.explodeAllRabbits();
        driver.clickPositive( "OK", true );

        // Back to beginning
        driver.back( AndroidMenuActivity.class );
        driver.back( AndroidMenuActivity.class );

        // Click About
        driver.clickMenuItem( 1, "About" );

        driver.back( AndroidAboutActivity.class );

        // Open an easy level
        driver.clickMenuItem( 0, "Start Game" );
        driver.clickMenuItem( 0, "Easy" );
        driver.clickMenuItem( 0, "Level ${number}" );
        driver.clickPositive( "Start" );

        // Solve it
        driver.chooseAbility( Token.Type.dig );
        driver.placeToken( 5, 2 );

        // Wait for congratulations
        driver.longWaitForDialog();
        driver.clickPositive( "Great!", true );

        // Open next
        MenuItem level1Item = driver.clickMenuItem( 1, "Level ${number}" );
        assertEquals( "2", level1Item.nameParams.get( "number" ) );
    }


    // ---


    private static class Holder<T>
    {
        T item;
    }

    private static interface RunAndReturn<T>
    {
        public T run();
    }

    static class AndroidUiDriver
    {
        private final long timeout = 10000L;
        private final long delay   =   100L;

        private final Map<Class<? extends Activity>, Instrumentation.ActivityMonitor> monitors =
            new HashMap<Class<? extends Activity>, Instrumentation.ActivityMonitor>();

        private Activity currentActivity;

        public AndroidUiDriver( ActivityInstrumentationTestCase2<AndroidMenuActivity> testCase )
        {
            monitor( testCase, AndroidMenuActivity.class );
            monitor( testCase, AndroidGameActivity.class );
            monitor( testCase, AndroidAboutActivity.class );

            testCase.launchActivity(
                "net.artificialworlds.rabbitescape", AndroidMenuActivity.class, null
            );
        }

        public void monitor(
            ActivityInstrumentationTestCase2<AndroidMenuActivity> testCase,
            Class<? extends Activity> activityClass
        )
        {
            monitors.put(
                activityClass,
                testCase.getInstrumentation().addMonitor( activityClass.getName(), null, false )
            );
        }

        public MenuItem clickMenuItem( final int itemIndex, String expectedName )
        {
            AndroidMenuActivity activity = waitActivity( AndroidMenuActivity.class );

            assertNotNull( "Could not find activity", activity );

            final ListView listView = (ListView)activity.findViewById( R.id.listView );
            assertNotNull( "Expected to find listView inside the menu activity", listView );

            changeActivity();

            MenuItem ret = runOnUiThreadAndWait(
                activity,
                new RunAndReturn<MenuItem>()
                {
                    @Override
                    public MenuItem run()
                    {
                        MenuItem item = (MenuItem)listView.getItemAtPosition( itemIndex );

                        listView.performItemClick(
                            listView, itemIndex, listView.getItemIdAtPosition( itemIndex )
                        );

                        return item;
                    }
                }
            );

            assertEquals( expectedName, ret.name );

            return ret;
        }

        public <T extends Activity> void back( Class<T> currentActivityClass )
        {
            final T activity = waitActivity( currentActivityClass );

            changeActivity();

            runOnUiThreadAndWait(
                activity,
                new RunAndReturn<String>()
                {
                    @Override
                    public String run()
                    {
                        activity.onBackPressed();
                        return "backPressed";
                    }
                }
            );
        }

        public void explodeAllRabbits()
        {
            final AndroidGameActivity activity = waitActivity( AndroidGameActivity.class );

            // Wait until we can click explode all
            for (
                int wait = 0;
                wait < timeout &&
                (
                    activity.gameSurface                               == null ||
                    activity.gameSurface.game                          == null ||
                    activity.gameSurface.game.gameLaunch.physics.world == null
                );
                wait += delay
            )
            {
                sleepDelay();
            }

            assertNotNull(
                "Game world not found", activity.gameSurface.game.gameLaunch.physics.world );

            final View button = activity.findViewById( R.id.explodeAllButton );

            assertNull( activity.currentDialog );

            runOnUiThreadAndWait(
                activity,
                new RunAndReturn<String>()
                {
                    @Override
                    public String run()
                    {
                        button.performClick();
                        return "buttonPressed";
                    }
                }
            );

            clickPositive( "Explode!" );
        }

        public void chooseAbility( Token.Type type )
        {
            final AndroidGameActivity activity = waitActivity( AndroidGameActivity.class );

            int numAbilities = activity.abilitiesGroup.getChildCount();
            for( int i = 0; i < numAbilities; ++i )
            {
                View child = activity.abilitiesGroup.getChildAt( i );
                assertTrue( child instanceof AbilityButton );
                final AbilityButton abilityButton = (AbilityButton)child;
                if ( abilityButton.ability().equals( type.name() ) )
                {
                    runOnUiThreadAndWait(
                        activity,
                        new RunAndReturn<Button>()
                        {
                            @Override
                            public Button run()
                            {
                                abilityButton.performClick();
                                return abilityButton;
                            }
                        }
                    );
                    return;
                }
            }
            fail( "Ability button not found (type: " + type.name() + ")" );
        }

        public void placeToken( final int x, final int y )
        {
            final AndroidGameActivity activity = waitActivity( AndroidGameActivity.class );

            runOnUiThreadAndWait(
                activity,
                new RunAndReturn<String>()
                {
                    @Override
                    public String run()
                    {
                        float ts = activity.gameSurface.game.gameLaunch.graphics.renderingTileSize;
                        int sx = activity.gameSurface.game.gameLaunch.graphics.scrollX;
                        int sy = activity.gameSurface.game.gameLaunch.graphics.scrollY;

                        MotionEvent motionEvent = MotionEvent.obtain(
                            50,
                            50,
                            MotionEvent.ACTION_DOWN,
                            (int)( ts * ( x + 0.5 ) ) - sx,
                            (int)( ts * ( y + 0.5 ) ) - sy,
                            0
                        );

                        activity.gameSurface.dispatchTouchEvent( motionEvent );
                        activity.gameSurface.performClick();

                        return "clicked";
                    }
                }
            );
        }

        public void clickPositive( String expectedButtonText )
        {
            clickPositive( expectedButtonText, false );
        }

        public void clickPositive( String expectedButtonText, boolean doChangeActivity )
        {
            assertEquals(
                expectedButtonText,
                clickDialogButton( DialogInterface.BUTTON_POSITIVE, doChangeActivity )
            );
        }

        public CharSequence clickDialogButton( final int buttonType, boolean doChangeActivity )
        {
            final AndroidGameActivity activity = waitActivity( AndroidGameActivity.class );

            assertNotNull( "Could not find activity", activity );

            // Wait until we can click the button
            for (
                int wait = 0;
                wait < timeout &&
                (
                    activity.currentDialog == null ||
                    !activity.currentDialog.isShowing() ||
                    activity.currentDialog.getButton( buttonType ) == null
                );
                wait += delay
                )
            {
                sleepDelay();
            }

            assertNotNull( activity.currentDialog );

            if ( doChangeActivity )
            {
                changeActivity();
            }

            // Click it
            Button button = runOnUiThreadAndWait(
                activity,
                new RunAndReturn<Button>()
                {
                    @Override
                    public Button run()
                    {
                        Button ret = activity.currentDialog.getButton( buttonType );
                        ret.performClick();
                        return ret;
                    }
                }
            );

            // Wait until the dialog has gone
            for ( int wait = 0; wait < timeout && activity.currentDialog != null; wait += delay )
            {
                sleepDelay();
            }

            return button.getText();
        }

        public void longWaitForDialog()
        {
            final AndroidGameActivity activity = waitActivity( AndroidGameActivity.class );

            for ( int wait = 0; wait < timeout * 10 && activity.currentDialog == null; wait += delay )
            {
                sleepDelay();
            }
        }

        public void changeActivity()
        {
            currentActivity = null;
        }

        // ---

        @SuppressWarnings( "unchecked" )
        private <T> T waitActivity( Class<? extends Activity> activityClass )
        {
            if ( currentActivity != null )
            {
                assertTrue( activityClass.isInstance( currentActivity ) );
                return (T)currentActivity;
            }

            Instrumentation.ActivityMonitor monitor = monitors.get( activityClass );
            currentActivity = monitor.waitForActivityWithTimeout( timeout );

            return (T)currentActivity;
        }

        private <T> T runOnUiThreadAndWait( final Activity activity, final RunAndReturn<T> code )
        {
            final Holder<T> holder = new Holder<T>();

            activity.runOnUiThread(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        holder.item = code.run();
                    }
                }
            );

            for ( int wait = 0; holder.item == null && wait < timeout; wait += delay )
            {
                sleepDelay();

            }

            assertNotNull( "runOnUiThreadAndWait never completed", holder.item );

            return holder.item;
        }

        public void sleepDelay()
        {
            try
            {
                Thread.sleep( delay );
            }
            catch ( InterruptedException e )
            {
                // Ignore
            }
        }
    }
}
