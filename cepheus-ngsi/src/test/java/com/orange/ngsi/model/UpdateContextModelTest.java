package com.orange.ngsi.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.orange.ngsi.TestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;

import static com.orange.ngsi.Util.createUpdateContextTempSensor;
import static com.orange.ngsi.Util.xml;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
