package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Token.Type.portal;

import java.util.Map;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;

public class Portaling extends Behaviour {
    @Override
    public void cancel() {
    }

    /*
     * pickUpToken calls world.changes.removeToken WHICH SHOULD NOT HAPPEN (it removes portal
     * token that rabbit acquired)
     * therefore we should not use t.pickUpToken
     */
    @Override
    public boolean checkTriggered(Rabbit rabbit, World world) {
        // if there are less than 2 tokens in the world, triggered <- false
        int portalCount = 0;
        for (Thing thing : world.things) {
            if (thing instanceof Token && ((Token) thing).type == Token.Type.portal) {
                portalCount++;
            }
        }
        // there need to be two portals in the map to be triggered
        if (portalCount < 2) {
            return false;
        }
        Token token = world.getTokenAt(rabbit.x, rabbit.y);
        return token != null && token.type == Token.Type.portal;
    }

    @Override
    public State newState(BehaviourTools t, boolean triggered) {
        if (triggered) {
            return t.rl(RABBIT_PORTALING_RIGHT, RABBIT_PORTALING_LEFT);
        }
        return null;
    }

    @Override
    public boolean behave(World world, Rabbit rabbit, State state) {
        if (state == RABBIT_PORTALING_RIGHT || state == RABBIT_PORTALING_LEFT) {
            Token currentPortal = world.getTokenAt(rabbit.x, rabbit.y );
            Token otherPortal = world.getOtherPortalToken(rabbit.x, rabbit.y);

            if(currentPortal != null && otherPortal != null) {
                // Teleport rabbit
                rabbit.x = otherPortal.x;
                rabbit.y = otherPortal.y;

                // update state to walking in the same direction
                rabbit.state = (rabbit.dir == Direction.RIGHT) ? RABBIT_WALKING_RIGHT : RABBIT_WALKING_LEFT;

                // remove both portals placed in the world
                world.changes.removeToken(currentPortal);
                world.changes.removeToken(otherPortal);

                return true;
            } else {
                // this part of code should not execute in normal situation
                rabbit.state = (rabbit.dir == Direction.RIGHT) ? RABBIT_WALKING_RIGHT : RABBIT_WALKING_LEFT;
                return true;
            }
        }
        return false;
    }

    @Override
    public void saveState(Map<String, String> saveState) {
        // No state to save for Portaling
    }

    @Override
    public void restoreFromState(Map<String, String> saveState) {
        // No state to restore for Portaling
    }
}