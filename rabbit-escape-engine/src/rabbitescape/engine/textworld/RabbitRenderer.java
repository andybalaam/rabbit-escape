package rabbitescape.engine.textworld;

import static rabbitescape.engine.Direction.*;

import java.util.List;

import rabbitescape.engine.Rabbit;

public class RabbitRenderer
{
    public static void render( char[][] chars, List<Rabbit> rabbits )
    {
        for ( Rabbit rabbit : rabbits )
        {
            chars[ rabbit.y ][ rabbit.x ] = charForRabbit( rabbit );
        }
    }

    private static char charForRabbit( Rabbit rabbit )
    {
        return rabbit.dir == RIGHT ? 'r' : 'j';
    }
}
