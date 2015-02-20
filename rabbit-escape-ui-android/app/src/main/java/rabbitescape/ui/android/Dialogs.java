package rabbitescape.ui.android;

import android.app.AlertDialog;
import android.content.DialogInterface;

import rabbitescape.engine.World;

import static rabbitescape.engine.i18n.Translation.t;

public class Dialogs
{
    public static void intro( final AndroidGameActivity activity, final World world )
    {
        DialogInterface.OnClickListener onOk = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick( DialogInterface dialogInterface, int i )
            {
                activity.setPaused( world, false );
            }
        };

        world.setIntro( false );  // We can't use onDismiss, so we can't guarantee to unintro
        activity.setPaused( world, true ); // ourselves.  Instead, use pause so the user can always
                                  // unpause manually if needed.

        new AlertDialog.Builder( activity )
            .setTitle( t( world.name ) )
            .setMessage( t( world.description ).replaceAll( "\\\\n", " " ) )
            .setPositiveButton( t( "Start" ), onOk )
            .create()
            .show();
    }

    public static void explode( final AndroidGameActivity activity, final World world )
    {
        DialogInterface.OnClickListener onCancel = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick( DialogInterface dialogInterface, int i )
            {
                activity.setPaused( world, false );
            }
        };

        DialogInterface.OnClickListener onExplode = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick( DialogInterface dialogInterface, int i )
            {
                activity.setPaused( world, false );
                world.changes.explodeAllRabbits();
            }
        };

        activity.setPaused( world, true );
        new AlertDialog.Builder( activity )
            .setMessage( t( "Explode all rabbits?" ) )
            .setNegativeButton( t( "Cancel" ), onCancel )
            .setPositiveButton( t( "Explode!" ), onExplode )
            .create()
            .show();
    }

    public static void finished( final AndroidGameActivity activity, String message, String ok )
    {
        DialogInterface.OnClickListener onOk = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick( DialogInterface dialogInterface, int i )
            {
                activity.finish();
            }
        };

        new AlertDialog.Builder( activity )
            .setMessage( message )
            .setPositiveButton( ok, onOk )
            .create()
            .show();
    }
}
