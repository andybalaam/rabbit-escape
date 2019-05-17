package rabbitescape.engine.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class TestCommandLineOption
{

    @Test
    public void Parse_options_short_form()
    {
        CommandLineOption present = new CommandLineOption( "--present", false );
        CommandLineOption notPresent = new CommandLineOption( "--not-present", false );
        CommandLineOption withParam = new CommandLineOption( "--with-param", true );

        String[] args = "-p -w 0".split( " " );
        CommandLineOptionSet.parse( args, present, notPresent, withParam );

        assertThat( present.isPresent(), is( true ) );
        assertThat( notPresent.isPresent(), is( false ) );
        assertThat( withParam.isPresent(), is( true ) );
        assertThat( withParam.getValue(), equalTo( "0" ) );
    }

    @Test
    public void Parse_options_long_form()
    {
        CommandLineOption present = new CommandLineOption( "--present", false );
        CommandLineOption notPresent = new CommandLineOption( "--not-present", false );
        CommandLineOption withParam = new CommandLineOption( "--with-param", true );

        String[] args = "--present --with-param 0".split( " " );
        CommandLineOptionSet.parse( args, present, notPresent, withParam );

        assertThat( present.isPresent(), is( true ) );
        assertThat( notPresent.isPresent(), is( false ) );
        assertThat( withParam.isPresent(), is( true ) );
        assertThat( withParam.getValue(), equalTo( "0" ) );
    }

    @Test( expected = CommandLineOption.UnknownOption.class )
    public void Unknown_option_throws_exception()
    {
        CommandLineOption present = new CommandLineOption( "--present", false );
        String[] args = "-p --you-what".split( " " );
        CommandLineOptionSet.parse( args, present );
    }

    @Test( expected = CommandLineOption.OptionRequiresParameter.class )
    public void Missing_parameter_throws_exception_last_arg()
    {
        CommandLineOption needsParam = new CommandLineOption( "--needs-param", true );

        String[] args = "-n".split( " " );
        CommandLineOptionSet.parse( args, needsParam );
    }

    @Test( expected = CommandLineOption.OptionRequiresParameter.class )
    public void Missing_parameter_throws_exception()
    {
        CommandLineOption needsParam = new CommandLineOption( "--needs-param", true );
        CommandLineOption bob = new CommandLineOption( "--bob", false );

        String[] args = "-n -b".split( " " );
        CommandLineOptionSet.parse( args, bob, needsParam );
    }

    @Test ( expected = CommandLineOption.OptionDoesNotTakeParameter.class )
    public void Option_does_not_take_parameter()
    {
        CommandLineOption begin = new CommandLineOption( "--begin", false );

        String[] args = "--begin0".split( " " );
        CommandLineOptionSet.parse( args, begin );
    }

    @Test
    public void Concatenated_parameters()
    {
        CommandLineOption begin = new CommandLineOption( "--begin", true );
        CommandLineOption end = new CommandLineOption( "--end", true );

        String[] args = "--begin0 -e100".split( " " );
        CommandLineOptionSet.parse( args, begin, end );

        assertThat( begin.getValue(), equalTo( "0" ) );
        assertThat( end.getValue(), equalTo( "100" ) );
    }

    @Test
    public void Concatenated_parameters_with_equals()
    {
        CommandLineOption begin = new CommandLineOption( "--begin", true );
        CommandLineOption end = new CommandLineOption( "--end", true );

        String[] args = "--begin=0 -e=100".split( " " );
        CommandLineOptionSet.parse( args, begin, end );

        assertThat( begin.getValue(), equalTo( "0" ) );
        assertThat( end.getValue(), equalTo( "100" ) );
    }

    @Test( expected = java.lang.AssertionError.class )
    public void Assertion_for_duplicate_short_form()
    {
        CommandLineOption test = new CommandLineOption( "--test", false );
        CommandLineOption template = new CommandLineOption( "--template", true );

        String[] args = "-t --template foo.rel".split( " " );
        // AssertionError here as both --test and --template can be -t.
        CommandLineOptionSet.parse( args, test, template );
    }
}
