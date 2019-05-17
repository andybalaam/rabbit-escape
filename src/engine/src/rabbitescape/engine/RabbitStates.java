package rabbitescape.engine;

import rabbitescape.engine.util.Position;

public class RabbitStates
{
    public static Position whereBridging( StateAndPosition change )
    {
        switch( change.state )
        {
            case RABBIT_BRIDGING_DOWN_UP_RIGHT_1:
            case RABBIT_BRIDGING_DOWN_UP_RIGHT_2:
            case RABBIT_BRIDGING_DOWN_UP_RIGHT_3:
            case RABBIT_BRIDGING_RIGHT_1:
            case RABBIT_BRIDGING_RIGHT_2:
            case RABBIT_BRIDGING_RIGHT_3:
                return new Position( change.x + 1, change.y );

            case RABBIT_BRIDGING_DOWN_UP_LEFT_1:
            case RABBIT_BRIDGING_DOWN_UP_LEFT_2:
            case RABBIT_BRIDGING_DOWN_UP_LEFT_3:
            case RABBIT_BRIDGING_LEFT_1:
            case RABBIT_BRIDGING_LEFT_2:
            case RABBIT_BRIDGING_LEFT_3:
                return new Position( change.x - 1, change.y );

            case RABBIT_BRIDGING_UP_RIGHT_1:
            case RABBIT_BRIDGING_UP_RIGHT_2:
            case RABBIT_BRIDGING_UP_RIGHT_3:
                return new Position( change.x + 1, change.y - 1 );

            case RABBIT_BRIDGING_UP_LEFT_1:
            case RABBIT_BRIDGING_UP_LEFT_2:
            case RABBIT_BRIDGING_UP_LEFT_3:
                return new Position( change.x - 1, change.y - 1 );

            case RABBIT_BRIDGING_IN_CORNER_LEFT_1:
            case RABBIT_BRIDGING_IN_CORNER_LEFT_2:
            case RABBIT_BRIDGING_IN_CORNER_LEFT_3:
            case RABBIT_BRIDGING_IN_CORNER_RIGHT_1:
            case RABBIT_BRIDGING_IN_CORNER_RIGHT_2:
            case RABBIT_BRIDGING_IN_CORNER_RIGHT_3:
                return new Position( change.x, change.y );
            case RABBIT_BRIDGING_IN_CORNER_UP_LEFT_1:
            case RABBIT_BRIDGING_IN_CORNER_UP_LEFT_2:
            case RABBIT_BRIDGING_IN_CORNER_UP_LEFT_3:
            case RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_1:
            case RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_2:
            case RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_3:
                return new Position( change.x, change.y - 1 );
            default:
                return null;
        }
    }

    public static char bridgingStage( ChangeDescription.State state )
    {
        switch ( state )
        {
            case RABBIT_BRIDGING_RIGHT_1:
            case RABBIT_BRIDGING_DOWN_UP_RIGHT_1:
            case RABBIT_BRIDGING_UP_RIGHT_1:
            case RABBIT_BRIDGING_IN_CORNER_RIGHT_1:
            case RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_1:
                return 'B';
            case RABBIT_BRIDGING_LEFT_1:
            case RABBIT_BRIDGING_DOWN_UP_LEFT_1:
            case RABBIT_BRIDGING_UP_LEFT_1:
            case RABBIT_BRIDGING_IN_CORNER_LEFT_1:
            case RABBIT_BRIDGING_IN_CORNER_UP_LEFT_1:
                return 'E';
            case RABBIT_BRIDGING_RIGHT_2:
            case RABBIT_BRIDGING_DOWN_UP_RIGHT_2:
            case RABBIT_BRIDGING_UP_RIGHT_2:
            case RABBIT_BRIDGING_IN_CORNER_RIGHT_2:
            case RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_2:
                return '[';
            case RABBIT_BRIDGING_LEFT_2:
            case RABBIT_BRIDGING_DOWN_UP_LEFT_2:
            case RABBIT_BRIDGING_UP_LEFT_2:
            case RABBIT_BRIDGING_IN_CORNER_LEFT_2:
            case RABBIT_BRIDGING_IN_CORNER_UP_LEFT_2:
                return ']';
            case RABBIT_BRIDGING_RIGHT_3:
            case RABBIT_BRIDGING_DOWN_UP_RIGHT_3:
            case RABBIT_BRIDGING_UP_RIGHT_3:
            case RABBIT_BRIDGING_IN_CORNER_RIGHT_3:
            case RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_3:
                return '{';
            case RABBIT_BRIDGING_LEFT_3:
            case RABBIT_BRIDGING_DOWN_UP_LEFT_3:
            case RABBIT_BRIDGING_UP_LEFT_3:
            case RABBIT_BRIDGING_IN_CORNER_LEFT_3:
            case RABBIT_BRIDGING_IN_CORNER_UP_LEFT_3:
                return '}';
            default:
                return ' ';
        }
    }
}
