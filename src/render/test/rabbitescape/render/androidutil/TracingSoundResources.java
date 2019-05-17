package rabbitescape.render.androidutil;

public class TracingSoundResources implements SoundResources<String>
{
    public String calls = "";

    @Override
    public void start( String activity )
    {
        calls += " start " + activity;
    }

    @Override
    public void pause()
    {
        calls += " pause";
    }

    @Override
    public void dispose()
    {
        calls += " dispose";
    }
}
