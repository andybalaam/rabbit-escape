package rabbitescape.engine.textworld;

import static rabbitescape.engine.util.Util.*;
import rabbitescape.engine.err.RabbitEscapeException;

public class IncorrectLine extends RabbitEscapeException
{
    private static final long serialVersionUID = 1L;

    public final String lines;
    public final int lineNum;
    public final String line;

    public IncorrectLine( String[] lines, int lineNum )
    {
        this.lines = join( "\n", lines );
        this.lineNum = lineNum + 1; // Human-readable line number
        this.line = lines[lineNum];
    }
}
