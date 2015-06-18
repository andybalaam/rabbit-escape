package rabbitescape.ui.android;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.SurfaceHolder;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
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

    // Transient state
    public final GeneralPhysics physics;
    public final AndroidGraphics graphics;
    public final SoundPlayer<AndroidBitmap> soundPlayer;
    public final AndroidInput input;

    public final WorldSaver worldSaver;
    private final GameLoop loop;

    public AndroidGameLaunch(
        SurfaceHolder surfaceHolder,
        BitmapCache<AndroidBitmap> bitmapCache,
        Resources resources,
        World world,
        LevelWinListener winListener,
        float displayDensity,
        Bundle savedInstanceState
    )
    {
        this.soundPlayer = new SoundPlayer<AndroidBitmap>( Globals.sound );
        this.physics = new GeneralPhysics( world, winListener );

        int scrollX;
        int scrollY;
        if ( savedInstanceState != null )
        {
            scrollX = savedInstanceState.getInt( STATE_SCROLL_X, 0 );
            scrollY = savedInstanceState.getInt( STATE_SCROLL_Y, 0);

        }
        else
        {
            scrollX = 0;
            scrollY = 0;
        }

        this.graphics = new AndroidGraphics(
            bitmapCache, soundPlayer, world, surfaceHolder, displayDensity, scrollX, scrollY );

        this.worldSaver = new WorldSaver( world, this );

        this.input = new AndroidInput( worldSaver );

        loop = new GameLoop( input, physics, graphics );
    }

    @Override
    public void run()
    {
        loop.run();
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
    }

    public boolean isRunning()
    {
        return loop.isRunning();
    }

    public void scrollBy( float x, float y )
    {
        graphics.scrollBy( x, y );
    }
}
