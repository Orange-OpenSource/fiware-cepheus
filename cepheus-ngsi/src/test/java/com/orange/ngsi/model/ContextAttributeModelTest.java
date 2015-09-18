package com.orange.ngsi.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by pborscia on 16/09/2015.
 */
public class ContextAttributeModelTest {

    @Test
    public void deserializationXMLContextAttribute() throws IOException {

        String xml =
                "        <contextAttribute>\n" +
                        "        <name>A</name>\n" +
                        "        <type>T</type>\n" +
                        "        <value>22</value>\n" +
                        "        </contextAttribute>\n";

        ObjectMapper xmlmapper = new XmlMapper();
        ContextAttribute contextAttribute = xmlmapper.readValue(xml, ContextAttribute.class);
        assertEquals("A", contextAttribute.getName());
        assertEquals("T", contextAttribute.getType());
        assertEquals("22", contextAttribute.getValue());
    }

    @Test
    public void serializationXMLContextAttribute() throws IOException, XPathExpressionException {

        ContextAttribute contextAttribute = new ContextAttribute("A", "T", "22");

        ObjectMapper xmlmapper = new XmlMapper();
        String xml = xmlmapper.writeValueAsString(contextAttribute);

        assertTrue(xml.contains("A"));
        assertTrue(xml.contains("T"));
        assertTrue(xml.contains("22"));

        String xpathExpr = "/ContextAttribute/name";
        XPath xPath = XPathFactory.newInstance().newXPath();
        assertEquals("A", xPath.evaluate(xpathExpr, new InputSource(new StringReader(xml))));
    }
}
