package rabbitescape.render;

public class Frame
{
    public final String name;
    public final int offsetX;
    public final int offsetY;

    public Frame( String name )
    {
        this( name, 0, 0 );
    }

    public Frame( String name, int offsetX )
    {
        this( name, offsetX, 0 );
    }

    public Frame( String name, int offsetX, int offsetY )
    {
        this.name = name;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}
