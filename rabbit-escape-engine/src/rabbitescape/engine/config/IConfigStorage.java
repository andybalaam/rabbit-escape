package rabbitescape.engine.config;

public interface IConfigStorage
{
    public void set( String key, String value );
    public String get( String key );
    void save( Config config );
}
