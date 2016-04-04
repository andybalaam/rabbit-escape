package rabbitescape.render;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import rabbitescape.engine.CellularDirection;

public class TestVector2D
{
    @Test
    public void Vectors_add()
    {
        Vector2D a = new Vector2D( 1, 2 );
        Vector2D b = new Vector2D( 3, 4 );
        assertThat( a.add( b ), equalTo( new Vector2D( 4, 6 ) ) );
    }

    @Test
    public void Vectors_subtract()
    {
        Vector2D a = new Vector2D( 1, 2 );
        Vector2D b = new Vector2D( 3, 4 );
        assertThat( a.subtract( b ), equalTo( new Vector2D( -2, -2 ) ) );
    }

    @Test
    public void Vectors_multiply_with_double()
    {
        Vector2D a = new Vector2D( 4, 6 );
        assertThat( a.multiply( 1.5 ), equalTo( new Vector2D( 6, 9 ) ) );
    }

    @Test
    public void Vectors_multiply_with_int()
    {
        Vector2D a = new Vector2D( 4, 6 );
        assertThat( a.multiply( 2 ), equalTo( new Vector2D( 8, 12 ) ) );
    }

    @Test
    public void Vectors_can_be_divided()
    {
        Vector2D a = new Vector2D( 4, 6 );
        assertThat( a.divide( 2 ), equalTo( new Vector2D( 2, 3 ) ) );
    }

    @Test
    public void Vectors_can_be_created_from_cellular_directions()
    {
        Vector2D a = Vector2D.translate( CellularDirection.DOWN );
        assertThat( a, equalTo( new Vector2D( 0, 1 ) ) );
    }

    @Test
    public void Vector_angle_is_calculated()
    {
        Vector2D a = new Vector2D( 1000, (int) ( 1000 * Math.sqrt( 3.01 ) ) );
        assertThat( a.angle() , equalTo( 60 ) );
    }


    @Test
    public void Vector_square_magnitude_is_calculated()
    {
        Vector2D a = new Vector2D( 3, 4 );
        assertThat( a.magnitude2() , equalTo( 25 ) );
    }

    @Test
    public void Equal_if_direction_and_magnitude_are_equal()
    {
        Vector2D a = new Vector2D( 10, 10 );
        Vector2D b = new Vector2D( 10, 10 );
        assertThat( a.equals( b ), equalTo ( true ) );
    }

    @Test
    public void Unequal_if_different()
    {
        Vector2D a = new Vector2D( 10, 10 );
        Vector2D b = new Vector2D( 10, 11 );
        assertThat( a.equals( b ), equalTo ( false ) );
    }

    @Test
    public void ToString_formats_to_coordinates()
    {
        Vector2D a = new Vector2D( 3, 4 );
        assertThat( a.toString(), equalTo( "(0003,0004)" ) );
    }

}
