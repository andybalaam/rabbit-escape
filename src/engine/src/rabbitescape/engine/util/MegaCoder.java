package rabbitescape.engine.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Obfuscate strings like a champion.
 * <p>
 * Swaps characters within the string around, swaps well-known characters with
 * each other, and transposes the string by exchanging characters with other
 * characters.
 * <p>
 * The MegaCoder is licensed here for use as part of rabbit-escape, but is also
 * declared public domain, so that it may be used, without attribution, by
 * anyone who needs obfuscation slightly more powerful than rot13, but less
 * portable than base64. (For clarity, the MegaCoder is this single file).
 * <p>
 * The MegaCoder shuffle algorithm:
 * <pre>
 * Input: An array of length l, A integer key k
 * i = 0
 * while at least two array entries remain unswapped:
 *   if array entry i has not been swapped:
 *     j = (floor(length / 2) + 3i + k) mod l
 *     while array entry j has already been swapped:
 *       j = (j + 1) mod l
 *     swap entries i and j in the array
 *   i = i + 1
 * Output: The modified array
 * </pre>
 * The MegaCoder replace algorithm:
 * <pre>
 * Input: An key array k of length l, An array of characters x
 * for i in [0, floor(l / 2)]:
 *   a = entry i in k; b = entry (l - 1 - i) in k
 *   replace all instances of a with b, and b with a, in the array x
 * Output: The modified array x
 * </pre>
 * The MegaCoder uniquify algorithm:
 * <pre>
 * Input: An array of characters x
 * Output: The set of characters in x sorted by value
 * </pre>
 * The MegaCoder encode algorithm:
 * <pre>
 * Input: An array of characters x of length l
 * k1 = uniquify(x)
 * x = replace(k1, x)
 * k2 = shuffle(C, l) where C is the array of ASCII characters from
 *      space (0x20) to tilde (0x7e)
 * x = replace(k2, x)
 * x = shuffle(x, 0)
 * Output: The modified array x
 * </pre>
 * The MegaCoder decode algorithm:
 * <pre>
 * Input: An array of characters x of length l
 * x = shuffle(x, 0)
 * k2 = shuffle(C, l) where C is the array of ASCII characters from
 *      space (0x20) to tilde (0x7e)
 * x = replace(k2, x)
 * k1 = uniquify(x)
 * x = replace(k1, x)
 * Output: The modified array x
 * </pre>
 *
 * @author tttppp
 */
public class MegaCoder
{
    /** A list of common ASCII characters that can be swapped with each
     *  other. */
    private static final char[] COMMON_CHARACTERS = new char[] {
        ' ', '!', '"', '#', '$', '%', '&',
        '\'', '(', ')', '*', '+', ',',
        '-', '.', '/', '0', '1', '2', '3',
        '4', '5', '6', '7', '8', '9', ':',
        ';', '<', '=', '>', '?', '@', 'A',
        'B', 'C', 'D', 'E', 'F', 'G', 'H',
        'I', 'J', 'K', 'L', 'M', 'N', 'O',
        'P', 'Q', 'R', 'S', 'T', 'U', 'V',
        'W', 'X', 'Y', 'Z', '[', '\\',
        ']', '^', '_', '`', 'a', 'b', 'c',
        'd', 'e', 'f', 'g', 'h', 'i', 'j',
        'k', 'l', 'm', 'n', 'o', 'p', 'q',
        'r', 's', 't', 'u', 'v', 'w', 'x',
        'y', 'z', '{', '|', '}', '~' };

    /** Private constructor for util class. */
    private MegaCoder()
    {
    }

    /**
     * Encode a string using the MegaCoder obfuscation routine.
     *
     * @param input
     *            The plaintext to encode.
     * @return The obfuscated ciphertext.
     */
    public static String encode( String input )
    {
        char[] charArray = input.toCharArray();

        char[] sortedCharsUsed = getSortedUniqueChars( charArray );
        charArray = replaceUsingCharacterList( sortedCharsUsed, charArray );

        charArray = replaceUsingCharacterList(
            shuffle( COMMON_CHARACTERS, charArray.length ), charArray );

        charArray = shuffle( charArray, 0 );

        return String.valueOf( charArray );
    }

    /**
     * Decode a string using the MegaCoder obfuscation routine.
     *
     * @param input
     *            The ciphertext to decode.
     * @return The plaintext.
     */
    public static String decode( String input )
    {
        char[] charArray = input.toCharArray();

        charArray = shuffle( charArray, 0 );

        charArray = replaceUsingCharacterList(
            shuffle( COMMON_CHARACTERS, charArray.length ), charArray );

        char[] sortedCharsUsed = getSortedUniqueChars( charArray );
        charArray = replaceUsingCharacterList( sortedCharsUsed, charArray );

        return String.valueOf( charArray );
    }

    /**
     * Create a new character array containing every character that appears in a
     * given array once. The new array will be sorted by natural order.
     *
     * @param charArray
     *            The character array to process.
     * @return An array containing the sorted set of characters in the input.
     */
    private static char[] getSortedUniqueChars( char[] charArray )
    {
        // Create a sorted copy of the input array.
        char[] sortedCharsUsed = copyArray( charArray );
        Arrays.sort( sortedCharsUsed );

        // Handle the edge case of an empty array.
        if ( sortedCharsUsed.length == 0 )
        {
            return sortedCharsUsed;
        }

        // Create a 'uniquified' copy of the array.
        int uniqueCharCount = 1;
        for ( int i = 0; i < sortedCharsUsed.length - 1; i++ )
        {
            if ( sortedCharsUsed[i] != sortedCharsUsed[i + 1] )
            {
                uniqueCharCount++;
            }
        }
        char[] uniqueSortedCharsUsed = new char[uniqueCharCount];
        uniqueSortedCharsUsed[0] = sortedCharsUsed[0];
        int uniqueArrayIndex = 1;
        for ( int i = 0; i < sortedCharsUsed.length - 1; i++ )
        {
            if ( sortedCharsUsed[i] != sortedCharsUsed[i + 1] )
            {
                uniqueSortedCharsUsed[uniqueArrayIndex] =
                    sortedCharsUsed[i + 1];
                uniqueArrayIndex++;
            }
        }

        return uniqueSortedCharsUsed;
    }

    /**
     * Replace characters in a string using a key.
     *
     * @param charsListKey
     *            A list of distinct characters. The first will be swapped with
     *            the last, and so on. If there is an odd number of characters
     *            then the middle character will not be swapped at all.
     * @param input
     *            The string to replace the characters in.
     */
    private static char[] replaceUsingCharacterList(
        char[] charsListKey,
        char[] input )
    {
        char[] output = input;
        int distinctCharacters = charsListKey.length;
        for ( int i = 0; i < distinctCharacters / 2; i++ )
        {
            output = exchangeCharacters( output, charsListKey[i],
                charsListKey[distinctCharacters - i - 1] );
        }
        return output;
    }

    /**
     * Swap two characters with each other in a given characters list.
     *
     * @param chars
     *            The list of characters to search through.
     * @param a
     *            A character.
     * @param b
     *            Another character.
     * @return The input, but with all occurrences of a and b exchanged.
     */
    private static char[] exchangeCharacters(
        char[] chars,
        char a,
        char b )
    {
        for ( int i = 0; i < chars.length; i++ )
        {
            if ( chars[i] == a )
            {
                chars[i] = b;
            }
            else if ( chars[i] == b )
            {
                chars[i] = a;
            }
        }
        return chars;
    }

    /**
     * Shuffle the characters in the given string.
     *
     * @param input
     *            The string to be shuffled.
     * @param key
     *            A value that changes the shuffling algorithm.
     * @return The string with letters swapped around a bit.
     */
    private static char[] shuffle( char[] input, int key )
    {
        char[] output = copyArray( input );
        Set<Integer> swappedIndexes = new HashSet<>();
        int length = output.length;
        for ( int i = 0; swappedIndexes.size() < ( length / 2 ) * 2; i++ )
        {
            if ( !swappedIndexes.contains( i ) )
            {
                int j = ( ( length / 2 ) + i * 3 + key ) % length;
                while ( swappedIndexes.contains( j ) || j == i )
                {
                    j = ( j + 1 ) % length;
                }

                Character temp = output[i];
                output[i] = output[j];
                output[j] = temp;

                swappedIndexes.add( i );
                swappedIndexes.add( j );
            }
        }
        return output;
    }

    private static char[] copyArray( char[] input )
    {
        char[] ret = new char[ input.length ];
        System.arraycopy( input, 0, ret, 0, ret.length );
        return ret;
    }
}
