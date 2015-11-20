package rabbitescape.engine.solution;

/**
 * Something that can be an action in a solution command, e.g.
 * "1" for a WaitAction
 * "until:Lost" for an UntilAction
 * "bash" for a SelectAction
 */
public interface CommandAction
{
    void typeSwitch( CommandActionTypeSwitch actionTypeSwitch );
}
