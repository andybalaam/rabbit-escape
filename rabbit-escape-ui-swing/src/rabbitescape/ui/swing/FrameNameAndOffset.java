package rabbitescape.ui.swing;

public class FrameNameAndOffset
{
    public final String name;
    public final int offsetX;
    public final int offsetY;

    public FrameNameAndOffset( String name )
    {
        this( name, 0, 0 );
    }

    public FrameNameAndOffset( String name, int offsetX )
    {
        this( name, offsetX, 0 );
    }

    public FrameNameAndOffset( String name, int offsetX, int offsetY )
    {
        this.name = name;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

}
