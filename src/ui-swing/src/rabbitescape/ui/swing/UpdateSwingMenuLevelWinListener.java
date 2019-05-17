package rabbitescape.ui.swing;

import rabbitescape.engine.LevelWinListener;

public class UpdateSwingMenuLevelWinListener implements LevelWinListener
{
    private final MenuUi menuUi;

    public UpdateSwingMenuLevelWinListener( MenuUi menuUi )
    {
        this.menuUi = menuUi;
    }

    @Override
    public void won()
    {
        menuUi.refreshEnabledItems();
    }

    @Override
    public void lost()
    {
    }
}
