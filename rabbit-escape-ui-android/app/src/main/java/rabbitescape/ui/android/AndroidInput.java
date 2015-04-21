package rabbitescape.ui.android;

public class AndroidInput implements Input
{
    private final WorldSaver worldSaver;

    public AndroidInput( WorldSaver worldSaver )
    {
        this.worldSaver = worldSaver;
    }

    @Override
    public void waitMs( long wait_time )
    {
        if ( wait_time > 0 )
        {
            worldSaver.waitUnlessSaveSignal( wait_time );
        }
        worldSaver.check();
    }
}
