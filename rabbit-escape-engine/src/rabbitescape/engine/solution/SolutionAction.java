package rabbitescape.engine.solution;

import rabbitescape.engine.World;

/**
 * Example actions are wait X time steps, select a token and place a token.
 */
public interface SolutionAction
{

    /*
     * Return the string representing this action to form part of the solution 
     * in a .rel file.
     */
    String relFormat( boolean firstInCommand );
    
    /**
     * Implementing classes must perform the action upon the world.
     */
    void perform( SandboxGame sbg, World world );
}
