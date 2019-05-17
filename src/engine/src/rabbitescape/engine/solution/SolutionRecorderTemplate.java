package rabbitescape.engine.solution;

public interface SolutionRecorderTemplate
{
    public void append( CommandAction a );
    public void append( SolutionCommand newCmd );
    public void appendStepEnd();
    public void append( Solution solution );
    public String getRecord();
}
