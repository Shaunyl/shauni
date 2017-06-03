package com.fil.shauni;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Chiara
 */
public class ConfigurationTest {
    @Test
    public void getProperty() {
        String value = Project.getProperty("database.name");
        Assert.assertEquals("oracle", value);
    }
    
    @Test
    public void getPropertyNotExistent() {
        String value = Project.getProperty("foo.property");
        Assert.assertNull(value);
    }
}
