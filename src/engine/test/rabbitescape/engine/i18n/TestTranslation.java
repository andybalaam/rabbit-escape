package rabbitescape.engine.i18n;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Locale;

import org.junit.Test;

import rabbitescape.engine.i18n.Translation;

public class TestTranslation
{
    @Test
    public void Unknown_bundle_means_key_is_returned()
    {
        Translation.Instance trans = new Translation.Instance(
            "nonexistent_bundle", Locale.ENGLISH );

        assertThat( trans.t( "Some key" ), equalTo( "Some key" ) );
    }

    @Test
    public void Unknown_key_means_key_is_returned()
    {
        Translation.Instance trans = new Translation.Instance(
            "rabbitescape.engine.i18n.testtranslations", Locale.ENGLISH );

        assertThat( trans.t( "Some key" ), equalTo( "Some key" ) );
    }

    @Test
    public void Unknown_locale_means_key_is_returned()
    {
        String bundleName;
        Locale defaultLocale = Locale.getDefault();
        if ( !defaultLocale.equals( Locale.FRANCE ) )
        {
            bundleName = "rabbitescape.engine.i18n.testtranslationsfranceonly";
        }
        else
        {
            bundleName = "rabbitescape.engine.i18n.testtranslationsspainonly";
        }

        // Ask for chinese translations in a bundle that doesn't supply a
        // Chinese value or a 'backup' value in the default locale.
        Translation.Instance trans = new Translation.Instance(
            bundleName, Locale.CHINA );

        assertThat( trans.t( "test_key_1" ), equalTo( "test_key_1" ) );
    }

    @Test
    public void Known_bundle_and_key_means_value_is_returned()
    {
        Translation.Instance trans = new Translation.Instance(
            "rabbitescape.engine.i18n.testtranslations", Locale.ENGLISH );

        assertThat( trans.t( "test_key_1" ), equalTo( "Test Value 1" ) );
    }

    @Test
    public void Sanitising_key_means_to_lower_case_with_underscores()
    {
        assertThat(
            Translation.Instance.sanitise( "foo! bar% Baz$ BASH£ Quux\" =" ),
            equalTo(                       "foo__bar__baz__bash__quux___" )
        );
    }

    @Test
    public void Sanitised_version_of_key_is_looked_up()
    {
        Translation.Instance trans = new Translation.Instance(
            "rabbitescape.engine.i18n.testtranslations", Locale.ENGLISH );

        assertThat(
            trans.t( "foo! bar% Baz$ BASH£ Quux\" =" ),
            equalTo( "Test Value 2" )
        );
    }
}
