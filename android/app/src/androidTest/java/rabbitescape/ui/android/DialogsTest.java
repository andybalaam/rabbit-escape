package rabbitescape.ui.android;

import android.test.ActivityInstrumentationTestCase2;

import rabbitescape.engine.Token;
import rabbitescape.engine.menu.MenuItem;

public class DialogsTest extends ActivityInstrumentationTestCase2<AndroidMenuActivity>
{
    public DialogsTest()
    {
        super( AndroidMenuActivity.class );
    }

    /**
     * To run, ensure the device is unlocked, and run as an Android Test.
     */
    public void test_Intro_and_hint_dialogs()
    {
        AndroidUiDriver driver = new AndroidUiDriver( this );

        // Click some menu items
        driver.clickMenuItem( 0, "Start Game" );
        driver.clickMenuItem( 0, "Easy" );
        driver.clickMenuItem( 0, "${number} Digging practice" );

        // I can cycle through the hints
        driver.clickNeutral( "Hint" );
        driver.clickNeutral( "Hint 2" );
        driver.clickNeutral( "Hint 3" );
        driver.clickNeutral( "Info" );
        driver.clickNeutral( "Hint" );
        assertNotNull( driver.currentDialog() );

        // I can click start, and the dialog goes away
        driver.clickPositive( "Start" );
        assertNull( driver.currentDialog() );
    }
}
