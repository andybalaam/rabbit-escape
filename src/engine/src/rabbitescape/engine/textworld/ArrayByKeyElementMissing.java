package rabbitescape.engine.textworld;

/**
 * A world has solutions 1 and 3, but no 2, for example.
 * Also they must be in order. solution 2 followed by solution 1 is illegal.
 */
public class ArrayByKeyElementMissing extends IncorrectLine
{
    private static final long serialVersionUID = 1L;

    public ArrayByKeyElementMissing( String[] lines, int lineNum )
    {
        super( lines, lineNum );
    }
}

