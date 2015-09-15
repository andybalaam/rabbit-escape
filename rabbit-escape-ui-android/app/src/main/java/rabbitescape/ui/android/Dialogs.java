package rabbitescape.ui.android;

import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.HashMap;
import java.util.Map;

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
                activity.currentDialog = null;
                activity.setPaused( world, false );
            }
        };

        activity.setPaused( world, true );

        activity.currentDialog = new AlertDialog.Builder( activity )
            .setTitle( t( world.name ) )
            .setMessage( introMessage( world ) )
            .setPositiveButton( t( "Start" ), onOk )
            .create();

        activity.currentDialog.show();
    }

    public static void explode( final AndroidGameActivity activity, final World world )
    {
        DialogInterface.OnClickListener onCancelButton = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick( DialogInterface dialogInterface, int i )
            {
                activity.currentDialog = null;
                activity.setPaused( world, false );
            }
        };

        DialogInterface.OnClickListener onExplode = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick( DialogInterface dialogInterface, int i )
            {
                activity.currentDialog = null;
                activity.setPaused( world, false );
                world.changes.explodeAllRabbits();
            }
        };

        activity.setPaused( world, true );
        activity.currentDialog = new AlertDialog.Builder( activity )
            .setMessage( t( "Explode all rabbits?" ) )
            .setNegativeButton( t( "Cancel" ), onCancelButton )
            .setPositiveButton( t( "Explode!" ), onExplode )
            .create();

        activity.currentDialog.show();
    }

    public static void finished( final AndroidGameActivity activity, String message, String ok )
    {
        DialogInterface.OnClickListener onOk = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick( DialogInterface dialogInterface, int i )
            {
                activity.currentDialog = null;
                activity.finish();
            }
        };

        activity.currentDialog = new AlertDialog.Builder( activity )
            .setMessage( finishedMessage( activity.gameSurface.world, message ) )
            .setPositiveButton( ok, onOk )
            .create();

        activity.currentDialog.show();
    }

    private static String introMessage( World world )
    {
        return (
            t( world.description ).replaceAll( "\\\\n", " " )
            +  "\n"
            + t( "Rabbits: ${num_rabbits}  Must save: ${num_to_save}", statsValues( world ) )
        );
    }

    private static String finishedMessage( World world, String message )
    {
        return (
               message
            +  "\n"
            + t( "Saved: ${num_saved}  Needed: ${num_to_save}", statsValues( world ) )
        );
    }

    private static Map<String, Object> statsValues( World world )
    {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put( "num_rabbits", world.num_rabbits );
        values.put( "num_to_save", world.num_to_save );
        values.put( "num_saved",   world.num_saved );
        return values;
    }
}
