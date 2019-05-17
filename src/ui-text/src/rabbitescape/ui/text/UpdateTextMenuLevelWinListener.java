package rabbitescape.ui.text;

import rabbitescape.engine.LevelWinListener;

public class UpdateTextMenuLevelWinListener implements LevelWinListener
{
    private final TextMenu textMenu;

    public UpdateTextMenuLevelWinListener( TextMenu textMenu )
    {
        this.textMenu = textMenu;
    }

    @Override
    public void won()
    {
        textMenu.refreshEnabledItems();
    }

    @Override
    public void lost()
    {
    }
}
