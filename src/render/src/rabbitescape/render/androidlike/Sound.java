package rabbitescape.render.androidlike;

public interface Sound
{
    void setMusic( String music );
    void playSound( String soundEffect );
    void mute( boolean muted );
    void dispose();
}
