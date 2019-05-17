package rabbitescape.ui.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import rabbitescape.engine.World;
import rabbitescape.engine.util.Util;

import static rabbitescape.engine.i18n.Translation.t;
import static rabbitescape.engine.util.NamedFieldFormatter.format;

public class Dialogs
{
    static class IntroDialogs
    {
        private final AndroidGameActivity activity;
        private final World world;
        private int hintNum;
        private final String[] hints;

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

            hints = new String[4];
            hints[0] = introMessage();
            for ( int i = 1, j = 0 ; i < 4 ; i++, j++ )
            {
                while ( j < world.hints.length
                        && Util.isEmpty( world.hints[j] ) )
                {
                    j++;
                }
                if ( j >= world.hints.length )
                {
                    hints[i] = "Hint " + i + " needed.";
                }
                else
                {
                    hints[i] = world.hints[j];
                }
            }
        }

        DialogInterface.OnClickListener onHint = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick( DialogInterface dialogInterface, int i )
            {
                hintNum = ( hintNum + 1 ) % 4 ;
                show();
            }
        };

        public void show()
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder( activity )
                .setTitle( t( world.name ) )
                .setPositiveButton( t( "Start" ), onOk );

            dialogBuilder.setNeutralButton( t( hintName() ), onHint );

            activity.currentDialog = dialogBuilder.create();

            TextView view = new TextView( activity.currentDialog.getContext() );
            view.setText( Html.fromHtml( hints[hintNum] ) );
            view.setMovementMethod( LinkMovementMethod.getInstance() );
            view.setPadding( 10, 10, 10, 10 );

            activity.currentDialog.setView( view );

            activity.currentDialog.show();
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
            return format(
                "<p><b>${description}</b></p>"
                + "<p>Rabbits: ${num_rabbits}  Must save: ${num_to_save}</p>"
                + "${author}",
                Util.newMap(
                    "description", readyForDialog( world.description ),
                    "num_rabbits", Integer.toString( world.num_rabbits ),
                    "num_to_save", Integer.toString( world.num_to_save ),
                    "author", authorHtml( world )
                )
            );
        }

        static String authorHtml( World world )
        {
            if ( Util.isEmpty( world.author_name ) )
            {
                return "";
            }

            String ret = "<p>";

            if ( Util.isEmpty( world.author_url ) )
            {
                ret += t(
                    "by ${author_name}",
                    Util.newMap( "author_name", world.author_name )
                );
            }
            else
            {
                ret += t(
                    "by <a href='${author_url}'>${author_name}</a>",
                    Util.newMap(
                        "author_name", world.author_name,
                        "author_url", world.author_url
                    )
                );
            }

            ret += "</p>";

            return ret;
        }

        private String readyForDialog( String message )
        {
            return t( message );
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
            .setMessage( finishedMessage( activity.world, message ) )
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
