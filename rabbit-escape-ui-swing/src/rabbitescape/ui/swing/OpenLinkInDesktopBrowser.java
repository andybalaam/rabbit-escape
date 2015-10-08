package rabbitescape.ui.swing;

import java.awt.Component;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Listener that opens the user's browser when the user clicks a hyperlink.
 */
public class OpenLinkInDesktopBrowser implements HyperlinkListener
{
    private final Component m_parent;

    public OpenLinkInDesktopBrowser( Component parent )
    {
        m_parent = parent;
    }

    @Override
    public void hyperlinkUpdate( HyperlinkEvent event )
    {
        if ( event.getEventType() == HyperlinkEvent.EventType.ACTIVATED )
        {
            if ( Desktop.isDesktopSupported() )
            {
                try
                {
                    Desktop.getDesktop().browse( event.getURL().toURI() );
                }
                catch ( IOException | URISyntaxException e )
                {
                    e.printStackTrace();

                    JOptionPane.showMessageDialog(
                        m_parent,
                        "Error opening link: "  + e.getMessage(),
                        "Failed to open URL",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }
}