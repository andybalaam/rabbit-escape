package rabbitescape.engine.textworld;

public class BadStateMap extends IncorrectLine
{
    private static final long serialVersionUID = 1L;

    public String stateMap;

    public BadStateMap( String stateMap, String[] lines, int lineNum )
    {
        super( lines, lineNum );
        this.stateMap = stateMap;
    }

}
