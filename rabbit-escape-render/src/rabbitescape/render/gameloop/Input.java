package rabbitescape.render.gameloop;

public interface Input
{
    void waitMs( long wait_time );
    long timeNow();
    void dispose();
}

