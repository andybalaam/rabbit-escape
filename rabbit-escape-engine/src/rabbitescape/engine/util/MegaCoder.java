package rabbitescape.engine.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
 * 
 * @author tttppp
 */
public class MegaCoder
{
    /** A list of common ASCII characters that can be swapped with each other. */
    private static final List<Character> COMMON_CHARACTERS = Arrays.asList(
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
        'y', 'z', '{', '|', '}', '~' );

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
        List<Character> characterList = stringToCharacters( input );

        List<Character> sortedCharactersUsed = new ArrayList<>(
            new TreeSet<Character>( characterList ) );
        characterList = replaceUsingCharacterList( sortedCharactersUsed,
            characterList );

        characterList = replaceUsingCharacterList(
            shuffle( COMMON_CHARACTERS, characterList.size() ), characterList );

        characterList = shuffle( characterList, 0 );

        return charactersToString( characterList );
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
        List<Character> characterList = stringToCharacters( input );

        characterList = shuffle( characterList, 0 );

        characterList = replaceUsingCharacterList(
            shuffle( COMMON_CHARACTERS, characterList.size() ), characterList );

        List<Character> sortedCharactersUsed = new ArrayList<>(
            new TreeSet<Character>( characterList ) );
        characterList = replaceUsingCharacterList( sortedCharactersUsed,
            characterList );

        return charactersToString( characterList );
    }

    /**
     * Replace characters in a string using a key.
     * 
     * @param characterListKey
     *            A list of characters. The first will be swapped with the last,
     *            and so on. If there is an odd number of characters then the
     *            middle character will not be swapped at all.
     * @param input
     *            The string to replace the characters in.
     */
    private static List<Character> replaceUsingCharacterList(
        List<Character> characterListKey,
        List<Character> input )
    {
        List<Character> output = input;
        int distinctCharacters = characterListKey.size();
        for ( int i = 0; i < distinctCharacters / 2; i++ )
        {
            output = exchangeCharacters( output, characterListKey.get( i ),
                characterListKey.get( distinctCharacters - i - 1 ) );
        }
        return output;
    }

    /**
     * Swap two characters with each other in a given characters list.
     * 
     * @param input
     *            The list of characters to search through.
     * @param a
     *            A character.
     * @param b
     *            Another character.
     * @return The input, but with all occurances of a and b exchanged.
     */
    private static List<Character> exchangeCharacters(
        List<Character> input,
        Character a,
        Character b )
    {
        List<Character> output = new ArrayList<>();
        for ( Character c : input )
        {
            if ( c.equals( a ) )
            {
                output.add( b );
            }
            else if ( c.equals( b ) )
            {
                output.add( a );
            }
            else
            {
                output.add( c );
            }
        }
        return output;
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
    private static List<Character> shuffle( List<Character> input, int key )
    {
        List<Character> output = new ArrayList<>( input );
        Set<Integer> swappedIndexes = new HashSet<>();
        int length = output.size();
        for ( int i = 0; swappedIndexes.size() < ( length / 2 ) * 2; i++ )
        {
            if ( !swappedIndexes.contains( i ) )
            {
                int j = ( ( length / 2 ) + i * 3 + key ) % length;
                while ( swappedIndexes.contains( j ) || j == i )
                {
                    j = ( j + 1 ) % length;
                }

                Character temp = output.get( i );
                output.set( i, output.get( j ) );
                output.set( j, temp );

                swappedIndexes.add( i );
                swappedIndexes.add( j );
            }
        }
        return output;
    }

    /**
     * Convert a string to a list of characters.
     * 
     * @param input
     *            The string to convert.
     * @return A list of characters that comprise the string.
     */
    private static List<Character> stringToCharacters( String input )
    {
        char[] charArray = input.toCharArray();
        List<Character> characterList = new ArrayList<>();
        for ( int i = 0; i < charArray.length; i++ )
        {
            characterList.add( charArray[i] );
        }
        return characterList;
    }

    /**
     * Create a string from the concatenation of a list of characters.
     * 
     * @param characterList
     *            The list of characters.
     * @return A string.
     */
    private static String charactersToString( List<Character> characterList )
    {
        StringBuilder sb = new StringBuilder();
        for ( Character c : characterList )
        {
            sb.append( c );
        }
        return sb.toString();
    }
}
