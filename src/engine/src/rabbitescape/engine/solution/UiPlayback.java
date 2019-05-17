package rabbitescape.engine.solution;

/**
 * Interface for GeneralPhysics to initiate actions for demo mode.
 * @see SolutionInterpreter
 */
public interface UiPlayback
{
    void selectToken( SelectAction selectAction );

    void placeToken( PlaceTokenAction placeTokenAction );
}
