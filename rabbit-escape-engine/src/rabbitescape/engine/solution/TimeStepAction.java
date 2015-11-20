package rabbitescape.engine.solution;

/**
 * Something that can be an action in a time step command, e.g.
 * "bash" for a SelectAction
 * "(2,3)" for a PlaceTokenAction
 *
 * Note: WaitAction and UntilAction DON'T implement this because while
 * they can be part of a command (and hence are CommandActions), they
 * can't be "executed" as part of a time step, because they span multiple
 * time steps.  Instead, the SolutionInterpreter "expands" them into
 * multiple (sometimes empty) time steps.
 */
public interface TimeStepAction
{
    void typeSwitch( TimeStepActionTypeSwitch timeStepActionTypeSwitch );
}
