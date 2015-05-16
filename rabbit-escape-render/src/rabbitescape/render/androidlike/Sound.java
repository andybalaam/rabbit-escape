package rabbitescape.render.androidlike;

public interface Sound
{
    void play( String soundEffect );
    void mute( boolean muted );
    void dispose();
}
