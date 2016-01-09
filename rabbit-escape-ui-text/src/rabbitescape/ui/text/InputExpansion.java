package rabbitescape.ui.text;

public class InputExpansion
{

    public final String character, expansion;

    public final static InputExpansion[] expansions = new InputExpansion[] {
        new InputExpansion( "b", "bash" ),
        new InputExpansion( "d", "dig" ),
        new InputExpansion( "i", "bridge" ),
        new InputExpansion( "k", "block" ),
        new InputExpansion( "c", "climb" ),
        new InputExpansion( "p", "explode" ),
        new InputExpansion( "l", "brolly" )
    };

    public InputExpansion( String character, String expansion )
    {
        this.character = character;
        this.expansion = expansion;
    }

    @Override
    public String toString()
    {
        return character + ": " + expansion ;
    }

}
