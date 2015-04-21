package rabbitescape.render.gameloop;

public interface Physics
{
    long step( long simulation_time, long frame_start_time );
    int frameNumber();
    boolean gameRunning();
}
