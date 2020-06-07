package com.wallissoftware.wikiparser;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WikiParser {

    public static void main(String... params) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        SAXParser saxParser = factory.newSAXParser();

        InputStream wikidump = new FileInputStream( "./wikidump/enwiki-20200401-pages-articles-multistream.xml");
        WikiHandler handler = new WikiHandler();
        saxParser.parse(wikidump, handler);

    }
}
