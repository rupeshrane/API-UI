package org.sodales;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.io.StringReader;

public class XMLFilePathHandler {
    ObjectMapper mapper;
    InputStream inputStream;

    // Reads the XML content based on the XPath expression
    public String readXmlPath(String xmlContent, String xpathExpression) {
        try {
            // Parse XML content
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlContent)));

            // Evaluate XPath expression
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr = xpath.compile(xpathExpression);
            String result = (String) expr.evaluate(document, XPathConstants.STRING);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

