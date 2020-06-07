package com.wallissoftware.wikiparser;

import com.google.gson.Gson;
import com.wallissoftware.byos.pack.UrlInfo;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Log
public class WikiHandler extends DefaultHandler {

    private final static Gson GSON = new Gson();

    private boolean firstNode = true;

    private Node node;

    private Writer writer;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        node.getText().append(new String(ch, start, length));
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        node = new Node(node, qName);

    }

    @SneakyThrows(IOException.class)
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("page".equals(qName)) {
            Optional<UrlInfo> urlInfo = node.getUrlInfo();
            if (urlInfo.isPresent()) {
                if (!firstNode) {
                    writer.append(",");
                } else {
                    firstNode = false;
                }
                writer.append(GSON.toJson(urlInfo.get()));
                log.info("Writing: " + urlInfo.get().getTitle());
            }
            node = new Node(null, qName);
        } else if (node.getParent() != null) {
            node = node.getParent();
        }
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        super.skippedEntity(name);
    }

    @SneakyThrows(IOException.class)
    @Override
    public void startDocument() throws SAXException {
        File file = new File("./wikidump/byos.json");
        file.delete();
        this.writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8));
        writer.append("[");
    }

    @SneakyThrows(IOException.class)
    @Override
    public void endDocument() throws SAXException {
        writer.append("]");
        this.writer.close();
    }
}
