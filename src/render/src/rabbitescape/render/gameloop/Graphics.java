package rabbitescape.render.gameloop;

public interface Graphics
{
    void draw( int frame );
    void rememberScrollPos();
    void drawIfScrolled( int frame );
    void dispose();
}

