package rabbitescape.engine.textworld;

/**
 * A water description line must contain all the information needed to create
 * the {@link WaterRegion}.
 */
public class InvalidWaterDescription extends IncorrectLine
{
    private static final long serialVersionUID = 1L;

    public InvalidWaterDescription( String[] lines, int lineNum )
    {
        super( lines, lineNum );
    }
}

