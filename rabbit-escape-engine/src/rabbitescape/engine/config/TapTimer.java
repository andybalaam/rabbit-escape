package rabbitescape.engine.config;

public class TapTimer
{
    /** Tap or click time intervals in ms */
    private static long intervals[] = new long[10]; // java inits array to zero.

    // Holst's Mars
    private static final float[] pattern =
        { 1.0f, 1.0f, 1.0f, 3.0f, 3.0f, 1.5f, 1.5f };

    public static boolean matched = checkEnv();

    private static long prevTime = 0;

    private static int nextI = 0;


    public static boolean checkEnv()
    {
        String tap = System.getenv("TAP");
        if ( null != tap && tap.equals( "Mars" ) )
        {
            setMars();
            return true;
        }
        return false;
    }

    public static void newTap()
    {
        long now = System.currentTimeMillis();
        intervals[nextI++] = now - prevTime ;
        nextI = nextI < intervals.length ? nextI : 0;
        prevTime = now;
        checkRhythm();
    }

    private static void checkRhythm()
    {
        int refIntervalI = nextI - 1;
        refIntervalI = refIntervalI >= 0 ? refIntervalI : intervals.length - 1;
        float refInterval = intervals[refIntervalI];

        int intervalI = nextI - 2;
        for ( int patternI = pattern.length - 2 ;
              patternI >= 0 ;
              patternI--, intervalI-- )
        {
            intervalI = intervalI >= 0 ? intervalI : intervals.length - 1;
            float relativeInterval =
                intervals[intervalI] * pattern[pattern.length - 1]
                / refInterval ;
            if ( Math.abs( relativeInterval - pattern[patternI] ) > 0.3 )
            {
                return;
            }
        }
        setMars();
    }

    public static void setMars()
    {
        System.out.println( "Mars" );
        matched = true;
    }
}
