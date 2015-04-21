package rabbitescape.ui.swing;

import rabbitescape.render.gameloop.Input;

public class SwingInput implements Input
{
    @Override
    public void waitMs( long wait_time )
    {
        try
        {
            Thread.sleep( wait_time );
        }
        catch ( InterruptedException ignored )
        {
        }
    }
}
