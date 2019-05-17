package rabbitescape.engine.config;

public interface IConfigUpgrade
{
    void run( IConfigStorage storage );
}
