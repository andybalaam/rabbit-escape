package rabbitescape.ui.android;

public interface Graphics
{
    void draw( int frame );

    void rememberScrollPos();

    void drawIfScrolled( int frame );
}
