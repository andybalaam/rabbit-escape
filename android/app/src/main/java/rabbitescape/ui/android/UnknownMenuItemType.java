package rabbitescape.ui.android;

import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.menu.MenuItem;

public class UnknownMenuItemType extends RabbitEscapeException
{
    private static final long serialVersionUID = 1L;

    public final String name;
    public final String type;

    public UnknownMenuItemType( MenuItem item )
    {
        this.name = item.name;
        this.type = item.type.name();
    }
}
