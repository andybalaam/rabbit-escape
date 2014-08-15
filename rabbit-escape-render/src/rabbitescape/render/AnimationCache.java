package rabbitescape.render;

import java.util.HashMap;
import java.util.Map;

public class AnimationCache
{
    private final String[] names;
    private final Map<String, Animation> animations;

    public AnimationCache( AnimationLoader animationLoader )
    {
        this.names = AnimationLoader.listAll();
        this.animations = new HashMap<>();

        for ( String name : names )
        {
            if ( !name.equals( AnimationLoader.NONE ) )
            {
                this.animations.put( name, AnimationLoader.load( name ) );
            }
        }
    }

    public String[] listAll()
    {
        return names;
    }

    public Animation get( String animationName )
    {
        return animations.get( animationName );
    }
}
