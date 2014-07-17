package rabbitescape.engine;

import rabbitescape.engine.ChangeDescription.State;

public interface Behaviour
{
    State newState( Rabbit rabbit, World world );
    boolean behave( World world, Rabbit rabbit, State state );
}
