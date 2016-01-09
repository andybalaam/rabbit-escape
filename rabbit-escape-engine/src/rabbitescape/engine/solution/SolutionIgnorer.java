package rabbitescape.engine.solution;

public class SolutionIgnorer implements SolutionRecorderTemplate
{
    public SolutionIgnorer()
    {
    }
    
    public void append( CommandAction a )
    {
    }
    
    public void append( SolutionCommand newCmd )
    {
    }
    
    public void appendStepEnd()
    {
    }
    
    public void append( Solution solution )
    {
    }
    
    public String getRecord()
    {
        return "";
    }
    
}

