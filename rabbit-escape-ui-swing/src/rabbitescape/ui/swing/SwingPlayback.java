package rabbitescape.ui.swing;

import rabbitescape.engine.solution.PlaceTokenAction;
import rabbitescape.engine.solution.SelectAction;
import rabbitescape.engine.solution.UiPlayback;

public class SwingPlayback implements UiPlayback
{
    final private SwingGameLaunch launch;

    public SwingPlayback( SwingGameLaunch launch )
    {
        this.launch = launch;
    }

    @Override
    public void selectToken( SelectAction selectAction )
    {
        launch.solutionRecorder.append( selectAction );
        launch.getUi().chooseAbility( selectAction.type );
    }

    @Override
    public void placeToken( PlaceTokenAction placeTokenAction )
    {
        launch.getUi().addToken( placeTokenAction.x, placeTokenAction.y );
    }



}
