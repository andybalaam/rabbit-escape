package rabbitescape.engine.textworld;

/**
 * @brief An exception representing redundant metadata.
 * Two ":name=" entries in a .rel file, for example.
 */
public class DuplicateMetaKey extends IncorrectLine
{
    private static final long serialVersionUID = 1L;

    public DuplicateMetaKey( String[] lines, int lineNum )
    {
        super( lines, lineNum );
    }
}
