package rabbitescape.ui.swing;

import rabbitescape.engine.solution.AssertStateAction;
import rabbitescape.engine.solution.PlaceTokenAction;
import rabbitescape.engine.solution.SelectAction;
import rabbitescape.engine.solution.SolutionExceptions.UnexpectedState;
import rabbitescape.engine.solution.TimeStepActionTypeSwitch;

/**
 * This class is not a funky dance. It is the bridge back from GeneralPhysics
 * to the swing package that communicates actions like token drops to the 
 * ui when running in demo mode.
 */
public class SwingTimeStepActionTypeSwitch implements TimeStepActionTypeSwitch
{
    final private SwingGameLaunch launch;
    
    public SwingTimeStepActionTypeSwitch( SwingGameLaunch launch )
    {
        this.launch = launch;
    }

    @Override
    public void caseSelectAction( SelectAction selectAction )
    {
        launch.getUi().chooseAbility( selectAction.type );
    }

    @Override
    public void caseAssertStateAction( AssertStateAction targetStateAction )
        throws UnexpectedState
    {
     // AssertStateAction ignored by swing demo mode.
    }

    @Override
    public void casePlaceTokenAction( PlaceTokenAction placeTokenAction )
    {
        launch.getUi().addToken( placeTokenAction.x, placeTokenAction.y );
    }

    

}
