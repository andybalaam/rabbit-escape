package rabbitescape.engine;

import static rabbitescape.engine.BehaviourTools.*;
import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Direction.*;

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
                    if ( world.getBlockAt( destX( rabbit ), rabbit.y ) != null )
                    {
                        return rl(
                            rabbit,
                            RABBIT_BASHING_RIGHT,
                            RABBIT_BASHING_LEFT
                        );
                    }
                    else
                    {
                        return rl(
                            rabbit,
                            RABBIT_BASHING_USELESSLY_RIGHT,
                            RABBIT_BASHING_USELESSLY_LEFT
                        );
                    }
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
            case RABBIT_BASHING_RIGHT:
            case RABBIT_BASHING_LEFT:
            {
                world.removeBlockAt( destX( rabbit ), rabbit.y );
                return true;
            }
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

    private int destX( Rabbit rabbit )
    {
        return ( rabbit.dir == RIGHT ) ? rabbit.x + 1 : rabbit.x - 1;
    }
}
