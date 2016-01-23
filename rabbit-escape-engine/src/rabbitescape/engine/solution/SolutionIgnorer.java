package rabbitescape.engine.solution;

public class SolutionIgnorer implements SolutionRecorderTemplate
{
    public SolutionIgnorer()
    {
    }

    @Override
    public void append( CommandAction a )
    {
    }

    @Override
    public void append( SolutionCommand newCmd )
    {
    }

    @Override
    public void appendStepEnd()
    {
    }

    @Override
    public void append( Solution solution )
    {
    }

    @Override
    public String getRecord()
    {
        return "";
    }

}

