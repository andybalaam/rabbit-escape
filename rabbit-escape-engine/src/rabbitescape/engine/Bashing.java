package rabbitescape.engine;

import static rabbitescape.engine.BehaviourTools.*;
import static rabbitescape.engine.ChangeDescription.State.*;

import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.Token.Type;
import rabbitescape.engine.err.RabbitEscapeException;

public class Bashing implements Behaviour
{
    public static class UnknownTokenType extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final Type type;

        public UnknownTokenType( Type type )
        {
            this.type = type;
        }
    }


    @Override
    public State newState( Rabbit rabbit, World world )
    {
        Token token = world.getTokenAt( rabbit.x, rabbit.y );
        if ( token != null )
        {
            world.removeThing( token );

            switch ( token.type )
            {
                case bash:
                {
                    return rl(
                        rabbit,
                        RABBIT_BASHING_USELESSLY_RIGHT,
                        RABBIT_BASHING_USELESSLY_LEFT
                    );
                }
                default:
                {
                    throw new UnknownTokenType( token.type );
                }
            }
        }
        return null;
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        switch ( state )
        {
            case RABBIT_BASHING_USELESSLY_RIGHT:
            case RABBIT_BASHING_USELESSLY_LEFT:
            {
                return true;
            }
            default:
            {
                return false;
            }
        }
    }
}
