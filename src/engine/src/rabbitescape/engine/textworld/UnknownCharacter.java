package rabbitescape.engine.textworld;

import static rabbitescape.engine.util.Util.*;
import rabbitescape.engine.err.RabbitEscapeException;

public class UnknownCharacter extends RabbitEscapeException
{
    private static final long serialVersionUID = 1L;

    public final String lines;
    public final int lineNum;
    public final char character;

    public UnknownCharacter( String[] lines, int lineNum, int charNum )
    {
        this.lines = join( "\n", lines );
        this.lineNum = lineNum + 1; // Human-readable line number
        this.character = getNth( asChars( lines[lineNum] ), charNum );
    }
}
