package rabbitescape.ui.swing;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

import rabbitescape.engine.util.Util;
import rabbitescape.render.Vertex;
import rabbitescape.render.androidlike.Canvas;
import rabbitescape.render.androidlike.Path;
import rabbitescape.render.androidlike.Rect;

public class SwingCanvas implements Canvas<SwingBitmap, SwingPaint>
{
    private final Graphics2D gfx;
    private final int width;
    private final int height;

    public SwingCanvas( Graphics2D gfx, int width, int height )
    {
        this.gfx = gfx;
        this.width = width;
        this.height = height;
    }

    @Override
    public void drawBitmap(
        SwingBitmap bitmap, float left, float top, SwingPaint paint )
    {
        this.gfx.setComposite( AlphaComposite.SrcAtop );
        this.gfx.drawImage( bitmap.image, (int)left, (int)top, null );
    }

    @Override
    public int width()
    {
        return width;
    }

    @Override
    public int height()
    {
        return height;
    }

    @Override
    public void drawColor( SwingPaint paint )
    {
        this.gfx.setPaint( paint.color );
        this.gfx.fillRect( 0, 0, width, height );
    }

    @Override
    public void drawLine(
        float startX,
        float startY,
        float stopX,
        float stopY,
        SwingPaint paint )
    {
        this.gfx.setPaint( paint.color );
        this.gfx.drawLine( (int)startX, (int)startY, (int)stopX, (int)stopY );
    }

    @Override
    public void drawPath( Path path, SwingPaint paint )
    {
        this.gfx.setPaint( paint.color );
        List<Vertex> vertices = path.getVertices();
        java.awt.Polygon poly = new java.awt.Polygon();
        for ( Vertex v : vertices)
        {
            poly.addPoint( (int)v.x, (int)v.y );
        }
        switch ( paint.getStyle() )
        {
        case FILL:
            this.gfx.fillPolygon( poly );
            break;
        case STROKE:
            this.gfx.drawPolygon( poly );
            break;
        default:
            throw new RuntimeException( "Unknown paint style" );
        }
    }

    @Override
    public void drawText(String text, float x, float y, SwingPaint paint)
    {
        this.gfx.setPaint( paint.color );
        String[] lines = Util.split( text, "\n" );
        int h = this.gfx.getFontMetrics().getHeight();
        for ( int i = 0; i < lines.length ; i++ )
        {
            this.gfx.drawString( lines[i], x, y + ( 1 + i ) * h );
        }
    }

    public int stringWidth( String text )
    {
        return gfx.getFontMetrics().stringWidth( text );
    }

    public int stringHeight()
    {
        return gfx.getFontMetrics().getHeight();
    }

    @Override
    public void drawRect(Rect rect, SwingPaint paint)
    {
        Rectangle r = new Rectangle(
            rect.left, rect.top,
            rect.right - rect.left, rect.bottom - rect.top );
        this.gfx.setPaint( paint.color );
        this.gfx.fill( r );
    }
}
