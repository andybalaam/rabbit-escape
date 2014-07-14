package rabbitescape.engine;

import rabbitescape.engine.ChangeDescription.State;

public interface Behaviour
{
    State newState( Rabbit rabbit, World world );
    boolean behave( Rabbit rabbit, State state );
}
