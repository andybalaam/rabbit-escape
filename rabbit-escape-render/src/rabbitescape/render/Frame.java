package rabbitescape.render;

public class Frame
{
    public final String name;
    public final int offsetX;
    public final int offsetY;
    public final String soundEffect;

    public Frame( String name )
    {
        this( name, 0, 0, null );
    }

    public Frame( String name, int offsetX )
    {
        this( name, offsetX, 0, null );
    }

    public Frame( String name, int offsetX, int offsetY, String soundEffect )
    {
        this.name = name;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.soundEffect = soundEffect;
    }
}
