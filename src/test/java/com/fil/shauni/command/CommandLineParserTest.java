package com.fil.shauni.command;

import com.fil.shauni.exception.ShauniException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author FTestino
 */
public class CommandLineParserTest {

    private CommandLineSupporter clp;
    private String[] args;

    @Rule
    public ExpectedException exceptions = ExpectedException.none();

    @Before
    public void setUp() throws ShauniException {
        args = new String[]{"exp", "pippa", "ciao=ciao", "nodash=nodash", "-format=tab", "-cluster=2", "-parallel=2", "-directory=out", "-filename=%i-%d_%u", "-queries=\"SELECT COUNT(*) FROM dual HAVING COUNT(*)=1\"", "-cluster=3", "-now=n", "-test=1"};
        clp = new CommandLineSupporter(args);
    }

    @Test
    public void findParameter() throws ShauniException {
        int cluster = clp.<Integer>getValue("cluster", Integer.class);
        Assert.assertEquals(2, cluster);

        String value = clp.getValue("filename", String.class);
        Assert.assertEquals("%i-%d_%u", value);

        value = clp.getValue("queries", String.class);
        Assert.assertEquals("\"SELECT COUNT(*) FROM dual HAVING COUNT(*)=1\"", value);

        value = clp.getValue("foo", String.class);
        Assert.assertNull(value);

        value = clp.getValue("exp", String.class);
        Assert.assertNull(value);

        Boolean now = clp.getValue("now", Boolean.class);
        Assert.assertFalse(now);
    }

    @Test
    public void findNotSupportedParameterType() throws ShauniException {
        exceptions.expect(ShauniException.class);
        clp.getValue("test", Long.class);
    }

    @Test
    public void findCommandName() {
        String value = clp.getCommandName();
        Assert.assertEquals("exp", value);
    }
    
    @Test
    public void getCommand() {
        String cmd = clp.getCommand();
        Assert.assertEquals("exp pippa ciao=ciao nodash=nodash -format=tab -cluster=2 -parallel=2 -directory=out -filename=%i-%d_%u -queries=\"SELECT COUNT(*) FROM dual HAVING COUNT(*)=1\" -cluster=3 -now=n -test=1", cmd);
    }
    
    @Test
    public void getNoDashedParameters() {
        int count = clp.countNoDashedParameters();
        Assert.assertTrue(count == 2);
    }
}
