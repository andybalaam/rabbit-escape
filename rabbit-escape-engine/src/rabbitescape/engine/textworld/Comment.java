package rabbitescape.engine.textworld;

public class Comment
{
    public final static String WORLD_ASCII_ART = "world-ascii-art";
    public final String text;
    public final String keyFollowing;

    private Comment(String text, String keyFollowing)
    {
        this.text = text;
        this.keyFollowing = keyFollowing;
    }

    public static Comment createUnlinkedComment( String text )
    {
        return new Comment( text, null);
    }

    public boolean isUnlinked()
    {
        return null == keyFollowing; 
    }

    public Comment link( String key )
    {
        return new Comment( this.text, key );
    }

    public boolean follows( String key )
    {
        if ( null == keyFollowing )
        {
            return false;
        }
        // Compare the starts of the keys only so hint.1 equals hint.1.code etc.
        int l1 = key.length(), l2 = keyFollowing.length();
        int shortestLength = l1 > l2 ? l2 : l1;
        return key.substring( 0, shortestLength ).equals( 
            keyFollowing.substring( 0, shortestLength ) );
    }
}
