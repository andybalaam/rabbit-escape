package rabbitescape.ui.android;

import rabbitescape.engine.LevelWinListener;

import static rabbitescape.engine.i18n.Translation.t;
import static rabbitescape.engine.util.Util.newMap;

public class AndroidAlertWinListener implements LevelWinListener
{
    private final AndroidGameActivity activity;
    private final String levelSet;
    private final boolean lastInset;

    public AndroidAlertWinListener(
        AndroidGameActivity activity, String levelSet, boolean lastInSet )
    {
        this.activity = activity;
        this.levelSet = levelSet;
        this.lastInset = lastInSet;
    }

    @Override
    public void won()
    {
        String message;
        String buttonText;
        if ( lastInset )
        {
            message = t(
                "Fantastic!  You completed the ${levelset} levels!",
                newMap( "levelset", levelSet )
            );
            buttonText = t( "Yippee!" );
        }
        else
        {
            message = t("Well done, you won!");
            buttonText = t( "Great!" );
        }

        dialogInUiThread( message, buttonText );
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
