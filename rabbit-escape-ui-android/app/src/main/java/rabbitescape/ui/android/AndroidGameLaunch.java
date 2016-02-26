package rabbitescape.ui.android;

import android.content.res.Resources;
import android.os.Bundle;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.config.IConfigStorage;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.SoundPlayer;
import rabbitescape.render.gameloop.GameLoop;
import rabbitescape.render.gameloop.GeneralPhysics;

import static android.text.TextUtils.join;

public class AndroidGameLaunch implements Runnable
{
    // Constants
    public  static final String STATE_WORLD    = "rabbitescape.world";
    private static final String STATE_SCROLL_X = "rabbitescape.scrollx";
    private static final String STATE_SCROLL_Y = "rabbitescape.scrolly";
    public static final String STATE_FAST_PRESSED = "rabbitescape.fast_pressed";

    // Transient state
    public final GeneralPhysics physics;
    public final AndroidGraphics graphics;
    public final SoundPlayer soundPlayer;
    public final AndroidInput input;

    public final WorldSaver worldSaver;
    private GameLoop loop;
    public Token.Type chosenAbility;

    public AndroidGameLaunch(
        BitmapCache<AndroidBitmap> bitmapCache,
        Resources resources,
        World world,
        LevelWinListener winListener,
        Bundle savedInstanceState
    )
    {
        this.soundPlayer = new SoundPlayer( Globals.sound );

        int scrollX;
        int scrollY;
        boolean fast;
        if ( savedInstanceState != null )
        {
            scrollX = savedInstanceState.getInt( STATE_SCROLL_X, 0 );
            scrollY = savedInstanceState.getInt( STATE_SCROLL_Y, 0 );
            fast = savedInstanceState.getBoolean( STATE_FAST_PRESSED, false );
        }
        else
        {
            scrollX = 0;
            scrollY = 0;
            fast = false;
        }

        this.physics = new GeneralPhysics( world, winListener, fast );

        this.graphics = new AndroidGraphics(
            bitmapCache, soundPlayer, world, scrollX, scrollY );

        this.worldSaver = new WorldSaver( world, this );

        this.input = new AndroidInput( worldSaver );

        this.loop = null;

        this.chosenAbility = null;
    }

    @Override
    public void run()
    {
        loop.run();
    }

    public boolean toggleSpeed()
    {
        physics.fast = !physics.fast;
        return physics.fast;
    }

    public void stopAndDispose()
    {
        loop.pleaseStop();
        input.dispose();
        graphics.dispose();
        physics.dispose();
    }

    public int addToken( Token.Type ability, float pixelX, float pixelY )
    {
        int x = (int)( ( pixelX + graphics.scrollX ) / graphics.renderingTileSize );
        int y = (int)( ( pixelY + graphics.scrollY ) / graphics.renderingTileSize );

        return physics.addToken( x, y, ability );
    }

    public void setPaused( boolean paused )
    {
        physics.world.paused = paused;
    }

    public boolean paused()
    {
        return physics.world.paused;
    }

    public void onSaveInstanceState( Bundle outState )
    {
        outState.putString( STATE_WORLD, join( "\n", worldSaver.waitUntilSaved() ) );
        outState.putInt( STATE_SCROLL_X, graphics.scrollX );
        outState.putInt( STATE_SCROLL_Y, graphics.scrollY );
        outState.putBoolean( STATE_FAST_PRESSED, physics.fast );
    }

    public boolean isRunning()
    {
        return loop.isRunning();
    }

    public void readyToRun()
    {
        this.loop = new GameLoop(
            input, physics, graphics, new NoKeysConfig(), null );
    }

    public void scrollBy( float x, float y )
    {
        graphics.scrollBy( x, y );
    }

    /**
     * A Config with no keys set - this will disable debug output
     * from the GameLoop.
     */
    private static class NoKeysConfig implements IConfigStorage
    {
        @Override
        public void set( String s, String s2 )
        {
        }

        @Override
        public String get( String s )
        {
            return null;
        }

        @Override
        public void save()
        {
        }
    }
}
