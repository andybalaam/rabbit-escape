package rabbitescape.ui.swing;

import rabbitescape.render.gameloop.Input;

import java.util.Date;

public class SwingInput implements Input
{
    @Override
    public void waitMs( long wait_time )
    {
        if ( wait_time < 0 )
        {
            return;
        }

        try
        {
            Thread.sleep( wait_time );
        }
        catch ( InterruptedException ignored )
        {
        }
    }

    @Override
    public long timeNow()
    {
        return new Date().getTime();
    }

    @Override
    public void dispose()
    {
    }
}
