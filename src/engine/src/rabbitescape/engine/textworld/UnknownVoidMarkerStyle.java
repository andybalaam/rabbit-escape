package rabbitescape.engine.textworld;

import rabbitescape.engine.VoidMarkerStyle;
import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.util.Util;

public class UnknownVoidMarkerStyle extends RabbitEscapeException
{
    private static final long serialVersionUID = 1L;
    public final String wrongStyle, knownStyles;

    public UnknownVoidMarkerStyle( String wrongStyle )
    {
        this.wrongStyle = wrongStyle;
        VoidMarkerStyle.Style[] styles = VoidMarkerStyle.Style.values();
        knownStyles = Util.join( " ", styles );
    }

}
