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
        CommandLineParameterSet.parse( args, present, notPresent, withParam );
                
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
        CommandLineParameterSet.parse( args, present, notPresent, withParam );
                
        assertThat( present.isPresent(), is( true ) );
        assertThat( notPresent.isPresent(), is( false ) );
        assertThat( withParam.isPresent(), is( true ) );
        assertThat( withParam.getValue(), equalTo( "0" ) );
    }
    
    @Test( expected = CommandLineOption.UnkownOption.class )
    public void Unknown_option_throws_exception()
    {
        CommandLineOption present = new CommandLineOption( "--present", false );
        String[] args = "-p --you-what".split( " " );
        CommandLineParameterSet.parse( args, present);
    }
}
