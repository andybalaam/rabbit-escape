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

    public boolean isFollowedBy( String key )
    {
        if ( null == keyFollowing )
        {
            return false;
        }
        return LineProcessor.stripCodeSuffix( key ).equals( 
            LineProcessor.stripCodeSuffix( keyFollowing ) );
    }
    
    
}
