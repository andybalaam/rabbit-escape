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
        activity.runOnUiThread(
            new Runnable()
            {
                public void run()
                {
                    showAlert();
                }
            }
        );
    }

    private void showAlert()
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
            .setMessage( t( "Well done, you won!" ) )
            .setPositiveButton( t( "I am great" ), onOk )
            .create()
            .show();
    }
}
