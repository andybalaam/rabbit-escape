package rabbitescape.ui.android;

import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.HashMap;
import java.util.Map;

import rabbitescape.engine.World;
import rabbitescape.engine.util.Util;

import static rabbitescape.engine.i18n.Translation.t;

public class Dialogs
{
    static class IntroDialogs
    {
        private final AndroidGameActivity activity;
        private final World world;
        private int hintNum;

        DialogInterface.OnClickListener onOk = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick( DialogInterface dialogInterface, int i )
            {
                activity.currentDialog = null;
                activity.setPaused( world, false );
            }
        };

        public IntroDialogs( AndroidGameActivity activity, World world )
        {
            this.activity = activity;
            this.world = world;
            this.hintNum = 0;

            activity.setPaused( world, true );
        }

        DialogInterface.OnClickListener onHint = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick( DialogInterface dialogInterface, int i )
            {
                ++hintNum;
                if ( hintNum > 3 || Util.isEmpty( hint( hintNum ) ) ) hintNum = 0;

                show();
            }
        };

        public void show()
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder( activity )
                .setTitle( t( world.name ) )
                .setMessage( hint( hintNum ) )
                .setPositiveButton( t( "Start" ), onOk );

            if ( Util.isEmpty( hint( hintNum + 1 ) ) )
            {
                if ( hintNum != 0 ) // No more hints, but there were some
                {
                    dialogBuilder.setNeutralButton( t( "Info" ), onHint );
                }
            }
            else
            {
                // More hints
                dialogBuilder.setNeutralButton( t( hintName() ), onHint );
            }

            activity.currentDialog = dialogBuilder.create();

            activity.currentDialog.show();
        }

        private String hint( int i )
        {
            if ( i > 3 ) i = 0;

            switch ( i )
            {
                case 0:  return introMessage();
                case 1:  return readyForDialog( world.hint1 );
                case 2:  return readyForDialog( world.hint2 );
                default: return readyForDialog( world.hint3 );
            }
        }

        private String hintName()
        {
            switch ( hintNum )
            {
                case 0: return "Hint";
                case 1: return "Hint 2";
                case 2: return "Hint 3";
                default: return "Info";
            }
        }

        private String introMessage()
        {
            return (
                readyForDialog( world.description )
                +  "\n"
                + t( "Rabbits: ${num_rabbits}  Must save: ${num_to_save}", statsValues( world ) )
            );
        }

        private String readyForDialog( String message )
        {
            return t( message ).replaceAll( "\\\\n", " " );
        }

    }

    public static void intro( final AndroidGameActivity activity, final World world )
    {
        new IntroDialogs( activity, world ).show();
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
