package com.orange.espr4fastdata;

import com.orange.espr4fastdata.exception.PersistenceException;
import com.orange.espr4fastdata.persistence.Persistence;
import com.orange.espr4fastdata.util.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by pborscia on 30/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class PersistenceTest {

    @Autowired
    Persistence persistence;

    private Util util = new Util();

    @Test
    public void saveloadConfiguration(){

        File confFile = new File("target/esper4fastdata.json");
        if (confFile.exists()) {
            confFile.delete();
        }

        try {
            persistence.saveConfiguration(util.getBasicConf());
        } catch (PersistenceException e) {
            Assert.fail("Not expected PersistenceException");
        }

        assertEquals(true,confFile.exists());

        try {
            persistence.loadConfiguration();
        } catch (PersistenceException e) {
            Assert.fail("Not expected PersistenceException");
        }

    }
}
