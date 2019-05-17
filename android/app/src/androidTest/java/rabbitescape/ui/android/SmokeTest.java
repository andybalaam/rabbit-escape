package rabbitescape.ui.android;

import android.test.ActivityInstrumentationTestCase2;

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
        MenuItem levelItem = driver.clickMenuItem( 0, "${number} Easy for some" );
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
        driver.clickMenuItem( 0, "${number} Digging practice" );
        driver.clickPositive( "Start" );

        // Solve it
        driver.chooseAbility( Token.Type.dig );
        driver.placeToken( 5, 2 );

        // Wait for congratulations
        driver.longWaitForDialog();
        driver.clickPositive( "Great!", true );

        // Open next
        MenuItem level1Item = driver.clickMenuItem( 1, "${number} Bashing practice" );
        assertEquals( "2", level1Item.nameParams.get( "number" ) );
    }
}
