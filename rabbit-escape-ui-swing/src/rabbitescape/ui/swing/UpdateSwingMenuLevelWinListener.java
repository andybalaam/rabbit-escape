package rabbitescape.ui.swing;

import rabbitescape.engine.LevelWinListener;

public class UpdateSwingMenuLevelWinListener implements LevelWinListener
{
    private final MenuJFrame menuJFrame;

    public UpdateSwingMenuLevelWinListener( MenuJFrame menuJFrame )
    {
        this.menuJFrame = menuJFrame;
    }

    @Override
    public void won()
    {
        menuJFrame.refreshEnabledItems();
    }

    @Override
    public void lost()
    {
    }
}
