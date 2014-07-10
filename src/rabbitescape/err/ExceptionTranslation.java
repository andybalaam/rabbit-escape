package rabbitescape.err;

import static rabbitescape.util.Util.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import rabbitescape.util.NamedFieldFormatter;

public class ExceptionTranslation
{
    private static final String prefix = "rabbitescape.";

    public static String translate(
        RabbitEscapeException exception, Locale locale )
    {
        String key = stripPrefix( exception.getClass().getCanonicalName() );

        ResourceBundle bundle =
            ResourceBundle.getBundle( "rabbitescape.err.exceptions", locale );

        try
        {
            return substituteFields( bundle.getString( key ), exception );
        }
        catch( MissingResourceException e )
        {
            return extractFields( exception ).toString();
        }
    }

    private static String substituteFields(
        String format,
        RabbitEscapeException exception )
    {
        return NamedFieldFormatter.format( format, extractFields( exception ) );
    }

    private static Map<String, Object> extractFields(
        RabbitEscapeException exception )
    {
        Map<String, Object> ret = new HashMap<String, Object>();

        for ( Field field : exception.getClass().getFields() )
        {
            ret.put( field.getName(), fieldValue( field, exception ) );
        }

        return ret;
    }

    private static Object fieldValue(
        Field field,
        RabbitEscapeException exception )
    {

        try
        {
            return field.get( exception );
        }
        catch ( IllegalArgumentException e )
        {
            // Should never happen
            e.printStackTrace();

            return "<<ERROR FINDING VALUE>>";
        }
        catch ( IllegalAccessException e )
        {
            // Will happen if the field is private

            e.printStackTrace();

            return "<<ERROR FINDING VALUE - NOT PUBLIC IN EXCEPTION>>";
        }
    }

    private static String stripPrefix( String name )
    {
        reAssert( name.startsWith( prefix ) );

        return name.substring( prefix.length() );
    }
}
