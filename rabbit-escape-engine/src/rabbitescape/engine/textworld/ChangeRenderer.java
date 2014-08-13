package rabbitescape.engine.textworld;

import rabbitescape.engine.ChangeDescription;
import rabbitescape.engine.ChangeDescription.Change;

public class ChangeRenderer
{
    public static void render( char[][] chars, ChangeDescription desc )
    {
        for ( ChangeDescription.Change change : desc.changes )
        {
            charForChange( change, chars );
        }
    }

    private static void charForChange( Change change, char[][] chars )
    {
        switch( change.state )
        {
            case NOTHING:
                break;
            case RABBIT_WALKING_LEFT:
                chars[change.y][change.x-1] = '<';
                break;
            case RABBIT_TURNING_LEFT_TO_RIGHT:
                chars[change.y][change.x] = '|';
                break;
            case RABBIT_TURNING_LEFT_TO_RIGHT_RISING:
                chars[change.y][change.x] = '{';
                break;
            case RABBIT_TURNING_LEFT_TO_RIGHT_LOWERING:
                chars[change.y][change.x] = '[';
                break;
            case RABBIT_WALKING_RIGHT:
                chars[change.y][change.x + 1] = '>';
                break;
            case RABBIT_TURNING_RIGHT_TO_LEFT:
                chars[change.y][change.x] = '?';
                break;
            case RABBIT_TURNING_RIGHT_TO_LEFT_RISING:
                chars[change.y][change.x] = '}';
                break;
            case RABBIT_TURNING_RIGHT_TO_LEFT_LOWERING:
                chars[change.y][change.x] = ']';
                break;
            case RABBIT_RISING_RIGHT_START:
                chars[change.y][change.x + 1] = '~';
                break;
            case RABBIT_RISING_RIGHT_CONTINUE:
                chars[change.y - 1][change.x + 1] = '$';
                break;
            case RABBIT_RISING_RIGHT_END:
                chars[change.y - 1][change.x + 1] = '\'';
                break;
            case RABBIT_RISING_LEFT_START:
                chars[change.y][change.x - 1] = '`';
                break;
            case RABBIT_RISING_LEFT_CONTINUE:
                chars[change.y - 1][change.x - 1] = '^';
                break;
            case RABBIT_RISING_LEFT_END:
                chars[change.y - 1][change.x - 1] = '!';
                break;
            case RABBIT_LOWERING_RIGHT_START:
                chars[change.y + 1][change.x + 1] = '-';
                break;
            case RABBIT_LOWERING_RIGHT_CONTINUE:
                chars[change.y + 1][change.x + 1] = '@';
                break;
            case RABBIT_LOWERING_RIGHT_END:
                chars[change.y][change.x + 1] = '_';
                break;
            case RABBIT_LOWERING_LEFT_START:
                chars[change.y + 1][change.x - 1] = '=';
                break;
            case RABBIT_LOWERING_LEFT_CONTINUE:
                chars[change.y + 1][change.x - 1] = '%';
                break;
            case RABBIT_LOWERING_LEFT_END:
                chars[change.y][change.x - 1] = '+';
                break;
            case RABBIT_LOWERING_AND_RISING_RIGHT:
                chars[change.y][change.x + 1] = ',';
                break;
            case RABBIT_LOWERING_AND_RISING_LEFT:
                chars[change.y][change.x - 1] = '.';
                break;
            case RABBIT_RISING_AND_LOWERING_RIGHT:
                chars[change.y][change.x + 1] = '&';
                break;
            case RABBIT_RISING_AND_LOWERING_LEFT:
                chars[change.y][change.x - 1] = '*';
                break;
            case RABBIT_FALLING:
                chars[change.y + 1][change.x] = 'f';
                chars[change.y + 2][change.x] = 'f';
                break;
            case RABBIT_FALLING_ONTO_LOWER_RIGHT:
                chars[change.y + 1][change.x] = 'f';
                chars[change.y + 2][change.x] = 'e';
                break;
            case RABBIT_FALLING_ONTO_LOWER_LEFT:
                chars[change.y + 1][change.x] = 'f';
                chars[change.y + 2][change.x] = 's';
                break;
            case RABBIT_FALLING_ONTO_RISE_RIGHT:
                chars[change.y + 1][change.x] = 'f';
                chars[change.y + 2][change.x] = 'd';
                break;
            case RABBIT_FALLING_ONTO_RISE_LEFT:
                chars[change.y + 1][change.x] = 'f';
                chars[change.y + 2][change.x] = 'a';
                break;
            case RABBIT_FALLING_1:
                chars[change.y + 1][change.x] = 'f';
                break;
            case RABBIT_FALLING_1_ONTO_LOWER_RIGHT:
                chars[change.y + 1][change.x] = 'e';
                break;
            case RABBIT_FALLING_1_ONTO_LOWER_LEFT:
                chars[change.y + 1][change.x] = 's';
                break;
            case RABBIT_FALLING_1_ONTO_RISE_RIGHT:
                chars[change.y + 1][change.x] = 'd';
                break;
            case RABBIT_FALLING_1_ONTO_RISE_LEFT:
                chars[change.y + 1][change.x] = 'a';
                break;
            case RABBIT_FALLING_1_TO_DEATH:
                chars[change.y + 1][change.x] = 'x';
                break;
            case RABBIT_DYING_OF_FALLING_2:
                chars[change.y][change.x] = 'y';
                break;
            case RABBIT_DYING_OF_FALLING:
                chars[change.y][change.x] = 'X';
                break;
            case RABBIT_ENTERING_EXIT:
                chars[change.y][change.x] = 'R';
                break;
            case RABBIT_BASHING_RIGHT:
                chars[change.y][change.x + 1] = 'K';
                break;
            case RABBIT_BASHING_LEFT:
                chars[change.y][change.x - 1] = 'W';
                break;
            case RABBIT_BASHING_USELESSLY_RIGHT:
                chars[change.y][change.x + 1] = 'I';
                break;
            case RABBIT_BASHING_USELESSLY_LEFT:
                chars[change.y][change.x - 1] = 'J';
                break;
            case TOKEN_BASH_STILL:
                break;
            case TOKEN_BASH_FALLING:
                chars[change.y + 1][change.x] = 'z';
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
