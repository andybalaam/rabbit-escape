package rabbitescape.engine.config;

public interface IConfig
{
    public void set( String key, String value );
    public String get( String key );
    public void save();
}
