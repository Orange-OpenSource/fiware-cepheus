/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;

import static com.orange.ngsi.Util.createUpdateContextTempSensor;
import static org.junit.Assert.assertEquals;

/**
 * Created by pborscia on 17/09/2015.
 */
public class UpdateContextModelTest {

    private ObjectMapper xmlmapper = new XmlMapper();

    @Test
    public void serializationXML() throws IOException, URISyntaxException, XPathExpressionException {

        String xml = xmlmapper.writeValueAsString(createUpdateContextTempSensor(0));

        String xpathExpr = "/updateContextRequest/contextElementList/contextElement[1]/entityId/id";
        XPath xPath = XPathFactory.newInstance().newXPath();
        String value = xPath.evaluate(xpathExpr, new InputSource(new StringReader(xml)));
        assertEquals("S1", value);
    }

}
