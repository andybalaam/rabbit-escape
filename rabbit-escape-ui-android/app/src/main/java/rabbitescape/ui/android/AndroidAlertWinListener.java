package rabbitescape.ui.android;

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
        dialogInUiThread( t( "Well done, you won!" ), t( "Great!" ) );
    }

    @Override
    public void lost()
    {
        dialogInUiThread( t( "Bad luck, you didn't save enough." ), t( "OK" ) );
    }

    private void dialogInUiThread( final String message, final String ok )
    {
        activity.runOnUiThread(
            new Runnable()
            {
                public void run()
                {
                    Dialogs.finished( activity, message, ok );
                }
            }
        );
    }
}
