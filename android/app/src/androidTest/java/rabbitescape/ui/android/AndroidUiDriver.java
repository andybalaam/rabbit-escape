package rabbitescape.ui.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ListView;

import junit.framework.Assert;

import net.artificialworlds.rabbitescape.R;

import java.util.HashMap;
import java.util.Map;

import rabbitescape.engine.Token;
import rabbitescape.engine.menu.MenuItem;

import static net.artificialworlds.rabbitescape.BuildConfig.APPLICATION_ID;

class AndroidUiDriver
{
    private static class Holder<T>
    {
        T item;
    }

    private static interface RunAndReturn<T>
    {
        public T run();
    }

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
            APPLICATION_ID, AndroidMenuActivity.class, null
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

        Assert.assertNotNull( "Could not find activity", activity );

        final ListView listView = (ListView)activity.findViewById( R.id.listView );
        Assert.assertNotNull( "Expected to find listView inside the menu activity", listView );

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

        Assert.assertEquals( expectedName, ret.name );

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

        Assert.assertNotNull(
            "Game world not found", activity.gameSurface.game.gameLaunch.physics.world
        );

        final View button = activity.findViewById( R.id.explodeAllButton );

        Assert.assertNull( activity.currentDialog );

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
            Assert.assertTrue( child instanceof AbilityButton );
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
        Assert.fail( "Ability button not found (type: " + type.name() + ")" );
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

    public AlertDialog currentDialog()
    {
        AndroidGameActivity activity = waitActivity( AndroidGameActivity.class );

        return activity.currentDialog;
    }

    public void clickNeutral( String expectedButtonText )
    {
        Assert.assertEquals(
            expectedButtonText,
            clickDialogButton( DialogInterface.BUTTON_NEUTRAL, false )
        );
    }

    public void clickPositive( String expectedButtonText )
    {
        clickPositive( expectedButtonText, false );
    }

    public void clickPositive( String expectedButtonText, boolean doChangeActivity )
    {
        Assert.assertEquals(
            expectedButtonText,
            clickDialogButton( DialogInterface.BUTTON_POSITIVE, doChangeActivity )
        );
    }

    public CharSequence clickDialogButton( final int buttonType, boolean doChangeActivity )
    {
        final AndroidGameActivity activity = waitActivity( AndroidGameActivity.class );

        Assert.assertNotNull( "Could not find activity", activity );

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

        Assert.assertNotNull( activity.currentDialog );

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
            Assert.assertTrue( activityClass.isInstance( currentActivity ) );
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

        Assert.assertNotNull( "runOnUiThreadAndWait never completed", holder.item );

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
