package rabbitescape.render;

import rabbitescape.engine.CellularDirection;

import static rabbitescape.engine.CellularDirection.LEFT;
import static rabbitescape.engine.CellularDirection.RIGHT;

/** A position within a cell. */
public enum CellPosition
{
    BOTTOM_LEFT( LEFT ),
    BOTTOM_RIGHT( RIGHT ),
    TOP_LEFT( LEFT ),
    TOP_RIGHT( RIGHT ),
    TOP_MIDDLE( null );

    public CellularDirection leftRightness;

    CellPosition(CellularDirection leftRightness)
    {
        this.leftRightness = leftRightness;
    }
}
