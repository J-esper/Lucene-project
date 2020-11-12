package com.javacodegeeks.snippets.core;
 
import java.io.File;
 
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
 
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
 
public class GetElementAttributesInSAXXMLParsing extends DefaultHandler {
     
    public static void main(String[] args) throws Exception {
         
        DefaultHandler handler = new GetElementAttributesInSAXXMLParsing();
 
        SAXParserFactory factory = SAXParserFactory.newInstance();
 
  factory.setValidating(false);
 
   
 
  SAXParser parser = factory.newSAXParser();
 
   
 
  parser.parse(new File("in.xml"), handler);
         
    }
     
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
         
        // get the number of attributes in the list
 
  int length = attributes.getLength();
 
 
  // process each attribute
 
  for (int i=0; i<length; i++) {
 
     
 
 
// get qualified (prefixed) name by index
 
 
String name = attributes.getQName(i);
 
 
System.out.println("Name:" + name);
 
 
 
 
 
// get attribute's value by index.
 
 
String value = attributes.getValue(i);
 
 
System.out.println("Value:" + value);
 
 
 
// get namespace URI by index (if parser is namespace-aware)
 
 
String nsUri = attributes.getURI(i);
 
 
System.out.println("NS Uri:" + nsUri);
 
 
 
// get local name by index
 
 
String lName = attributes.getLocalName(i);
 
 
System.out.println("Local Name:" + lName);
 
 
 
 
  }
         
    }
 
}