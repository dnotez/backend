package com.pl.web.cli;

import com.pl.dsl.cli.SaveCmdRequest;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author mamad
 * @since 13/12/14.
 */
public class FirstLineSaveCmdTitleBuilderTest {
    @Test
    public void testTitleOf() throws Exception {
        FirstLineSaveCmdTitleBuilder titleBuilder = new FirstLineSaveCmdTitleBuilder(10, false);
        assertThat(titleBuilder.titleOf(new SaveCmdRequest("u", "l", null)), equalTo(""));
        assertThat(titleBuilder.titleOf(new SaveCmdRequest("u", "l", "echo")), equalTo("echo"));
        assertThat(titleBuilder.titleOf(new SaveCmdRequest("u", "l", "sudo apt-get update; sudo apt-get upgrade")), equalTo("sudo..."));

        titleBuilder = new FirstLineSaveCmdTitleBuilder(10, true);
        assertThat(titleBuilder.titleOf(new SaveCmdRequest("u", null, null)), equalTo(""));
        assertThat(titleBuilder.titleOf(new SaveCmdRequest("u", "", null)), equalTo(""));
        assertThat(titleBuilder.titleOf(new SaveCmdRequest("u", "l", null)), equalTo("l"));
        assertThat(titleBuilder.titleOf(new SaveCmdRequest("u", "l", "echo")), equalTo("l:echo"));
        assertThat(titleBuilder.titleOf(new SaveCmdRequest("u", "l", "sudo apt-get update; sudo apt-get upgrade")), equalTo("l:sudo..."));

    }
}
