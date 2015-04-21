package rabbitescape.ui.android;

public class Input
{
    private final WorldSaver worldSaver;

    public Input( WorldSaver worldSaver )
    {
        this.worldSaver = worldSaver;
    }

    public void waitMs( long wait_time )
    {
        if ( wait_time > 0 )
        {
            worldSaver.waitUnlessSaveSignal( wait_time );
        }
        worldSaver.check();
    }
}
