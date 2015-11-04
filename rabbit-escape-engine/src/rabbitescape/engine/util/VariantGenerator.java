package rabbitescape.engine.util;

/**
 * A predictable random number generator - we don't care about its quality -
 * we just don't want it to change if e.g. the Java library is updated.
 *
 * Algorithm is due to Marsaglia: the MWC (multiply with carry) algorithm.
 * http://www.codeproject.com/Articles/25172/Simple-Random-Number-Generation
 */
public class VariantGenerator
{
    private int seed_z;
    private int seed_w;

    public VariantGenerator( int seed )
    {
        this.seed_z = seed;
        this.seed_w = 3463;
    }

    public int next( int i )
    {
        seed_z = 36969 * ( seed_z & 65535 ) + ( seed_z >> 16 );
        seed_w = 18000 * ( seed_w & 65535 ) + ( seed_w >> 16 );
        return ( ( seed_z << 16 ) + seed_w ) % i;
    }
}
