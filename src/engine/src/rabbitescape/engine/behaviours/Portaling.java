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
        // there need to be two portals in the map
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

                // remove bothe portal placed in the world
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


/*
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

@Override
public boolean checkTriggered(Rabbit rabbit, World world) {
    // if there are less than 2 tokens in the world, triggered <- false
    if (world.portalTokens.size() < 2) {
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
    System.out.println("behave1");
    if (state == RABBIT_PORTALING_RIGHT || state == RABBIT_PORTALING_LEFT) {
        System.out.println("behave2");
        Token otherPortal = world.getOtherPortalToken(rabbit.x, rabbit.y);
        if (otherPortal != null) {
            // Teleport rabbit to other portal's position
            rabbit.x = otherPortal.x;
            rabbit.y = otherPortal.y;

            // Update rabbit's state to walking in the same direction
            rabbit.state = (rabbit.dir == Direction.RIGHT) ? RABBIT_WALKING_RIGHT : RABBIT_WALKING_LEFT;

            // Remove both portal tokens from the world
            world.changes.removeToken(world.portalTokens.get( 0 ));
            world.changes.removeToken(world.portalTokens.get( 1 ));

            return true;
        } else {
            // Only one portal token exists; do nothing
            System.out.println("otherportalisnull");
            return false;
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
 */


/*
package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Direction.RIGHT;

import java.util.Map;

import rabbitescape.engine.*;

public class Portaling extends Behaviour {
    private Token portal1;
    private Token portal2;

    @Override
    public void cancel() {
        portal1 = null;
        portal2 = null;
    }

    @Override
    public boolean checkTriggered(Rabbit rabbit, World world) {
        BehaviourTools t = new BehaviourTools(rabbit, world);
        Token token = world.getTokenAt(rabbit.x, rabbit.y);
        if (token != null && token.type == Token.Type.portal) {
            if (portal1 == null) {
                portal1 = token;
                return false; // Only one portal; do nothing.
            } else if (portal2 == null) {
                portal2 = token;
                return true; // Both portals exist; teleport.
            }
        }
        return false;
    }

    @Override
    public ChangeDescription.State newState(BehaviourTools t, boolean triggered) {
        if (triggered && portal1 != null && portal2 != null) {
            return t.rl(RABBIT_PORTALING_RIGHT, RABBIT_PORTALING_LEFT);
        }
        return null;
    }

    @Override
    public boolean behave(World world, Rabbit rabbit, ChangeDescription.State state) {
        if (state == RABBIT_PORTALING_RIGHT || state == RABBIT_PORTALING_LEFT) {
            Token targetPortal = (rabbit.x == portal1.x && rabbit.y == portal1.y) ? portal2 : portal1;
            rabbit.x = targetPortal.x;
            rabbit.y = targetPortal.y;
            return true;
        }
        return false;
    }

    @Override
    public void saveState(Map<String, String> saveState) {
        if (portal1 != null) {
            saveState.put("Portal1.x", Integer.toString(portal1.x));
            saveState.put("Portal1.y", Integer.toString(portal1.y));
        }
        if (portal2 != null) {
            saveState.put("Portal2.x", Integer.toString(portal2.x));
            saveState.put("Portal2.y", Integer.toString(portal2.y));
        }
    }

    @Override
    public void restoreFromState(Map<String, String> saveState) {
        if (saveState.containsKey("Portal1.x") && saveState.containsKey("Portal1.y")) {
            portal1 = new Token(
                Integer.parseInt(saveState.get("Portal1.x")),
                Integer.parseInt(saveState.get("Portal1.y")),
                Token.Type.portal
            );
        }
        if (saveState.containsKey("Portal2.x") && saveState.containsKey("Portal2.y")) {
            portal2 = new Token(
                Integer.parseInt(saveState.get("Portal2.x")),
                Integer.parseInt(saveState.get("Portal2.y")),
                Token.Type.portal
            );
        }
    }
}
*/