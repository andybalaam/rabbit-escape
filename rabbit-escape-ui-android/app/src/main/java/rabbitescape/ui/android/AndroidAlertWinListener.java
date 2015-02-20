package rabbitescape.ui.android;

import android.app.AlertDialog;
import android.content.DialogInterface;

import rabbitescape.engine.LevelWinListener;

import static rabbitescape.engine.i18n.Translation.t;

public class AndroidAlertWinListener implements LevelWinListener
{
    private final AndroidGameActivity activity;

    public AndroidAlertWinListener( AndroidGameActivity activity )
    {
        this.activity = activity;
    }

    @Override
    public void won()
    {
        showAlert( t( "Well done, you won!" ), t( "Great!" ) );
    }

    @Override
    public void lost()
    {
        showAlert( t( "Bad luck, you didn't save enough." ), t( "OK" ) );
    }

    private void showAlert( final String message, final String ok )
    {
        activity.runOnUiThread(
            new Runnable()
            {
                public void run()
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
        );
    }
}
