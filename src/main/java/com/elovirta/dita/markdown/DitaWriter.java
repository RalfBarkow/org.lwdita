package com.elovirta.dita.markdown;

import com.elovirta.dita.markdown.renderer.AttributablePart;
import com.elovirta.dita.markdown.renderer.NodeRendererContext;
import org.dita.dost.util.DitaClass;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.util.ArrayDeque;
import java.util.Deque;

import static javax.xml.XMLConstants.NULL_NS_URI;

public class DitaWriter {
    private NodeRendererContext context;
    private AttributablePart useAttributes;

    public DitaWriter(ContentHandler out) {
        this.contentHandler = out;
    }

    void setContext(NodeRendererContext context) {
        this.context = context;
    }

    public NodeRendererContext getContext() {
        return context;
    }

    public final Deque<String> tagStack = new ArrayDeque<>();
    public ContentHandler contentHandler;

    public void setContentHandler(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    // ContentHandler methods

    public void startElement(final DitaClass tag, final org.xml.sax.Attributes atts) {
        startElement(tag.localName, atts);
    }

    public void startElement(final String tag, final org.xml.sax.Attributes atts) {
        try {
            contentHandler.startElement(NULL_NS_URI, tag, tag, atts);
        } catch (final SAXException e) {
            throw new ParseException(e);
        }
        tagStack.addFirst(tag);
    }

    public void endElement() {
        if (!tagStack.isEmpty()) {
            endElement(tagStack.removeFirst());
        }
    }

    public void endElement(final DitaClass tag) {
        endElement(tag.localName);
    }

    public void endElement(final String tag) {
        try {
            contentHandler.endElement(NULL_NS_URI, tag, tag);
        } catch (final SAXException e) {
            throw new ParseException(e);
        }
    }

    public void characters(final char c) {
        try {
            contentHandler.characters(new char[]{c}, 0, 1);
        } catch (final SAXException e) {
            throw new ParseException(e);
        }
    }

    public void characters(final String t) {
        final char[] cs = t.toCharArray();
        try {
            contentHandler.characters(cs, 0, cs.length);
        } catch (final SAXException e) {
            throw new ParseException(e);
        }
    }

    public void close() {
        while (!tagStack.isEmpty()) {
            endElement();
        }
    }

    public void processingInstruction(String name, String data) {
        try {
            contentHandler.processingInstruction(name, data != null ? data : "");
        } catch (final SAXException e) {
            throw new ParseException(e);
        }
    }
}
