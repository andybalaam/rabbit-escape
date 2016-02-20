package rabbitescape.engine.util;

public class WaterUtil
{
    /** The maximum capacity of water that can be held in a quarter empty tile without it overflowing. */
    public static final int QUARTER_CAPACITY = 25;
    /** The maximum capacity of water that can be held in a half empty tile without it overflowing. */
    public static final int HALF_CAPACITY = QUARTER_CAPACITY * 2;
    /** The maximum capacity of water that can be held in an empty tile without it overflowing. */
    public static final int MAX_CAPACITY = HALF_CAPACITY * 2;
    /** The default rate at which pipes produce water. */
    public static final int SOURCE_RATE = 50;
}
