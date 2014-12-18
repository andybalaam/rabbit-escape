package rabbitescape.engine.textworld;

import rabbitescape.engine.ChangeDescription;
import rabbitescape.engine.ChangeDescription.Change;

public class ChangeRenderer
{
    public static void render( Chars chars, ChangeDescription desc )
    {
        for ( ChangeDescription.Change change : desc.changes )
        {
            charForChange( change, chars );
        }
    }

    private static void charForChange( Change change, Chars chars )
    {
        switch( change.state )
        {
            case NOTHING:
                break;
            case RABBIT_WALKING_LEFT:
                chars.set( change.x-1, change.y, '<' );
                break;
            case RABBIT_TURNING_LEFT_TO_RIGHT:
                chars.set( change.x, change.y, '|' );
                break;
            case RABBIT_TURNING_LEFT_TO_RIGHT_RISING:
                chars.set( change.x, change.y, '|' );
                break;
            case RABBIT_TURNING_LEFT_TO_RIGHT_LOWERING:
                chars.set( change.x, change.y, '[' );
                break;
            case RABBIT_WALKING_RIGHT:
                chars.set( change.x + 1, change.y, '>' );
                break;
            case RABBIT_TURNING_RIGHT_TO_LEFT:
                chars.set( change.x, change.y, '?' );
                break;
            case RABBIT_TURNING_RIGHT_TO_LEFT_RISING:
                chars.set( change.x, change.y, '?' );
                break;
            case RABBIT_TURNING_RIGHT_TO_LEFT_LOWERING:
                chars.set( change.x, change.y, ']' );
                break;
            case RABBIT_RISING_RIGHT_START:
                chars.set( change.x + 1, change.y, '~' );
                break;
            case RABBIT_RISING_RIGHT_CONTINUE:
                chars.set( change.x + 1, change.y - 1, '$' );
                break;
            case RABBIT_RISING_RIGHT_END:
                chars.set( change.x + 1, change.y - 1, '\'' );
                break;
            case RABBIT_RISING_LEFT_START:
                chars.set( change.x - 1, change.y, '`' );
                break;
            case RABBIT_RISING_LEFT_CONTINUE:
                chars.set( change.x - 1, change.y - 1, '^' );
                break;
            case RABBIT_RISING_LEFT_END:
                chars.set( change.x - 1, change.y - 1, '!' );
                break;
            case RABBIT_LOWERING_RIGHT_START:
                chars.set( change.x + 1, change.y + 1, '-' );
                break;
            case RABBIT_LOWERING_RIGHT_CONTINUE:
                chars.set( change.x + 1, change.y + 1, '@' );
                break;
            case RABBIT_LOWERING_RIGHT_END:
                chars.set( change.x + 1, change.y, '_' );
                break;
            case RABBIT_LOWERING_LEFT_START:
                chars.set( change.x - 1, change.y + 1, '=' );
                break;
            case RABBIT_LOWERING_LEFT_CONTINUE:
                chars.set( change.x - 1, change.y + 1, '%' );
                break;
            case RABBIT_LOWERING_LEFT_END:
                chars.set( change.x - 1, change.y, '+' );
                break;
            case RABBIT_LOWERING_AND_RISING_RIGHT:
                chars.set( change.x + 1, change.y, ',' );
                break;
            case RABBIT_LOWERING_AND_RISING_LEFT:
                chars.set( change.x - 1, change.y, '.' );
                break;
            case RABBIT_RISING_AND_LOWERING_RIGHT:
                chars.set( change.x + 1, change.y, '&' );
                break;
            case RABBIT_RISING_AND_LOWERING_LEFT:
                chars.set( change.x - 1, change.y, 'm' );
                break;
            case RABBIT_FALLING:
                chars.set( change.x, change.y + 1, 'f' );
                chars.set( change.x, change.y + 2, 'f' );
                break;
            case RABBIT_FALLING_ONTO_LOWER_RIGHT:
                chars.set( change.x, change.y + 1, 'f' );
                chars.set( change.x, change.y + 2, 'e' );
                break;
            case RABBIT_FALLING_ONTO_LOWER_LEFT:
                chars.set( change.x, change.y + 1, 'f' );
                chars.set( change.x, change.y + 2, 's' );
                break;
            case RABBIT_FALLING_ONTO_RISE_RIGHT:
                chars.set( change.x, change.y + 1, 'f' );
                chars.set( change.x, change.y + 2, 'h' );
                break;
            case RABBIT_FALLING_ONTO_RISE_LEFT:
                chars.set( change.x, change.y + 1, 'f' );
                chars.set( change.x, change.y + 2, 'a' );
                break;
            case RABBIT_FALLING_1:
                chars.set( change.x, change.y + 1, 'f' );
                break;
            case RABBIT_FALLING_1_ONTO_LOWER_RIGHT:
                chars.set( change.x, change.y + 1, 'e' );
                break;
            case RABBIT_FALLING_1_ONTO_LOWER_LEFT:
                chars.set( change.x, change.y + 1, 's' );
                break;
            case RABBIT_FALLING_1_ONTO_RISE_RIGHT:
                chars.set( change.x, change.y + 1, 'h' );
                break;
            case RABBIT_FALLING_1_ONTO_RISE_LEFT:
                chars.set( change.x, change.y + 1, 'a' );
                break;
            case RABBIT_FALLING_1_TO_DEATH:
                chars.set( change.x, change.y + 1, 'x' );
                break;
            case RABBIT_DYING_OF_FALLING_2:
                chars.set( change.x, change.y, 'y' );
                break;
            case RABBIT_DYING_OF_FALLING:
                chars.set( change.x, change.y, 'X' );
                break;
            case RABBIT_ENTERING_EXIT:
                chars.set( change.x, change.y, 'R' );
                break;
            case RABBIT_BASHING_RIGHT:
                chars.set( change.x + 1, change.y, 'K' );
                break;
            case RABBIT_BASHING_LEFT:
                chars.set( change.x - 1, change.y, 'W' );
                break;
            case RABBIT_BASHING_UP_RIGHT:
                chars.set( change.x + 1, change.y - 1, 'K' );
                break;
            case RABBIT_BASHING_UP_LEFT:
                chars.set( change.x - 1, change.y - 1, 'W' );
                break;
            case RABBIT_BASHING_USELESSLY_RIGHT:
                chars.set( change.x + 1, change.y, 'I' );
                break;
            case RABBIT_BASHING_USELESSLY_LEFT:
                chars.set( change.x - 1, change.y, 'J' );
                break;
            case RABBIT_DIGGING:
                chars.set( change.x, change.y + 1, 'D' );
                break;
            case RABBIT_DIGGING_ON_SLOPE:
                chars.set( change.x, change.y, 'D' );
                break;
            case RABBIT_BRIDGING_RIGHT_1:
            case RABBIT_BRIDGING_DOWN_UP_RIGHT_1:
                chars.set( change.x + 1, change.y, 'B' );
                break;
            case RABBIT_BRIDGING_RIGHT_2:
            case RABBIT_BRIDGING_DOWN_UP_RIGHT_2:
                chars.set( change.x + 1, change.y, '[' );
                break;
            case RABBIT_BRIDGING_RIGHT_3:
            case RABBIT_BRIDGING_DOWN_UP_RIGHT_3:
                chars.set( change.x + 1, change.y, '{' );
                break;
            case RABBIT_BRIDGING_LEFT_1:
            case RABBIT_BRIDGING_DOWN_UP_LEFT_1:
                chars.set( change.x - 1, change.y, 'E' );
                break;
            case RABBIT_BRIDGING_LEFT_2:
            case RABBIT_BRIDGING_DOWN_UP_LEFT_2:
                chars.set( change.x - 1, change.y, ']' );
                break;
            case RABBIT_BRIDGING_LEFT_3:
            case RABBIT_BRIDGING_DOWN_UP_LEFT_3:
                chars.set( change.x - 1, change.y, '}' );
                break;
            case RABBIT_BRIDGING_UP_RIGHT_1:
                chars.set( change.x + 1, change.y - 1, 'B' );
                break;
            case RABBIT_BRIDGING_UP_RIGHT_2:
                chars.set( change.x + 1, change.y - 1, '[' );
                break;
            case RABBIT_BRIDGING_UP_RIGHT_3:
                chars.set( change.x + 1, change.y - 1, '{' );
                break;
            case RABBIT_BRIDGING_UP_LEFT_1:
                chars.set( change.x - 1, change.y - 1, 'E' );
                break;
            case RABBIT_BRIDGING_UP_LEFT_2:
                chars.set( change.x - 1, change.y - 1, ']' );
                break;
            case RABBIT_BRIDGING_UP_LEFT_3:
                chars.set( change.x - 1, change.y - 1, '}' );
                break;
            case RABBIT_BRIDGING_IN_CORNER_RIGHT_1:
                chars.set( change.x, change.y, 'B' );
                break;
            case RABBIT_BRIDGING_IN_CORNER_RIGHT_2:
                chars.set( change.x, change.y, '[' );
                break;
            case RABBIT_BRIDGING_IN_CORNER_RIGHT_3:
                chars.set( change.x, change.y, '{' );
                break;
            case RABBIT_BRIDGING_IN_CORNER_LEFT_1:
                chars.set( change.x, change.y, 'E' );
                break;
            case RABBIT_BRIDGING_IN_CORNER_LEFT_2:
                chars.set( change.x, change.y, ']' );
                break;
            case RABBIT_BRIDGING_IN_CORNER_LEFT_3:
                chars.set( change.x, change.y, '}' );
                break;
            case RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_1:
                chars.set( change.x, change.y - 1, 'B' );
                break;
            case RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_2:
                chars.set( change.x, change.y - 1, '[' );
                break;
            case RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_3:
                chars.set( change.x, change.y - 1, '{' );
                break;
            case RABBIT_BRIDGING_IN_CORNER_UP_LEFT_1:
                chars.set( change.x, change.y - 1, 'E' );
                break;
            case RABBIT_BRIDGING_IN_CORNER_UP_LEFT_2:
                chars.set( change.x, change.y - 1, ']' );
                break;
            case RABBIT_BRIDGING_IN_CORNER_UP_LEFT_3:
                chars.set( change.x, change.y - 1, '}' );
                break;
            case RABBIT_BLOCKING:
                chars.set( change.x, change.y, 'H' );
                break;
            case TOKEN_BASH_STILL:
            case TOKEN_DIG_STILL:
            case TOKEN_BRIDGE_STILL:
            case TOKEN_BLOCK_STILL:
            case TOKEN_CLIMB_STILL:
                break;
            case TOKEN_BASH_FALLING:
            case TOKEN_DIG_FALLING:
            case TOKEN_BRIDGE_FALLING:
            case TOKEN_BLOCK_FALLING:
            case TOKEN_CLIMB_FALLING:
                chars.set( change.x, change.y + 1, 'f' );
                break;
            case ENTRANCE:
                break;
            case EXIT:
                break;
            default:
                throw new AssertionError(
                    "Unknown Change state: " + change.state.name() );
        }
    }
}
