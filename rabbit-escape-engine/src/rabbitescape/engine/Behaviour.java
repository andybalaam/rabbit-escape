package rabbitescape.engine;

import java.util.Map;

import rabbitescape.engine.ChangeDescription.State;

public interface Behaviour
{
    State newState( Rabbit rabbit, World world );
    boolean behave( World world, Rabbit rabbit, State state );
    void saveState( Map<String, String> saveState );
    void restoreFromState( Map<String, String> saveState );
}
