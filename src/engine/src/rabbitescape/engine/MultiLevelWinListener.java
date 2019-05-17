package rabbitescape.engine;

public class MultiLevelWinListener implements LevelWinListener
{
    private final LevelWinListener[] subListeners;

    public MultiLevelWinListener( LevelWinListener... subListeners )
    {
        this.subListeners = subListeners;
    }

    @Override
    public void won()
    {
        for ( LevelWinListener listener : subListeners )
        {
            listener.won();
        }
    }

    @Override
    public void lost()
    {
        for ( LevelWinListener listener : subListeners )
        {
            listener.lost();
        }
    }
}
