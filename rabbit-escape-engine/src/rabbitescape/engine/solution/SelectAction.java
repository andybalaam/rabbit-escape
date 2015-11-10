package rabbitescape.engine.solution;

import rabbitescape.engine.Token;

public class SelectAction implements SolutionAction
{
    public final Token.Type type;

    public SelectAction( Token.Type type )
    {
        this.type = type;
    }

    @Override
    public String relFormat( boolean firstInCommand )
    {
        if ( firstInCommand )
        {
            return type.name();
        }
        return SolutionFactory.ACTION_DELIMITER + type.name();
    }

    @Override
    public String toString()
    {
        return "SelectAction( " + type.name() + " )";
    }

    @Override
    public boolean equals( Object otherObj )
    {
        if ( ! ( otherObj instanceof SelectAction ) )
        {
            return false;
        }
        SelectAction other = (SelectAction)otherObj;

        return ( type == other.type );
    }

    @Override
    public int hashCode()
    {
        return type.hashCode();
    }

    @Override
    public void typeSwitch( ActionTypeSwitch actionTypeSwitch )
    {
        actionTypeSwitch.caseSelectAction( this );
    }
}
