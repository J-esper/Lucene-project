package assignment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.FileInputStream;
import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.hamcrest.core.IsNull;

/* This code is based on:
 * - Demo Lucene
 * - https://examples.javacodegeeks.com/core-java/xml/sax/get-element-attributes-in-sax-xml-parsing 
 * 
 */
public class IndexXML extends DefaultHandler {
	
	private static Analyzer analyzer;
	private static Directory dir;
	private static IndexWriterConfig iwc;
	private static IndexWriter writer;
	
	
	public static void main(String[] args) throws Exception {
		String usage = "java assignment.IndexXML" + " [-index INDEX_PATH] [-xml XML_PATH] [-update]\n\n"
		+ "This indexes the documents in XML_PATH, creating a Lucene index"
		+ "in INDEX_PATH that can be searched with SearchIndex";
		
		String indexPath = "index";
		boolean create = true;
		
		String XMLpath = "D:\\School\\Master Data Science\\Information Retrieval\\subset.xml";

		DefaultHandler handler = new IndexXML();

		SAXParserFactory factory = SAXParserFactory.newInstance();

		factory.setValidating(false);

		SAXParser parser = factory.newSAXParser();

		for (int i = 0; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				indexPath = args[i + 1];
				i++;
			} else if ("-xml".equals(args[i])) {
				XMLpath = args[i + 1];
				i++;
			} else if ("-update".equals(args[i])) {
				create = false;
			}
		}

		if (XMLpath == null) {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}

		final Path docDir = Paths.get(XMLpath);
		if (!Files.isReadable(docDir)) {
			System.out.println("Document directory '" + docDir.toAbsolutePath()
					+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			dir = FSDirectory.open(Paths.get(indexPath));
//			analyzer = new StandardAnalyzer();
			analyzer = new EnglishAnalyzer();
			iwc = new IndexWriterConfig(analyzer);

			if (create) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			// Optional: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer. But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			//
			// iwc.setRAMBufferSizeMB(256.0);

			writer = new IndexWriter(dir, iwc);
			
			// startElement will go over the XML file
			parser.parse(new File(XMLpath), handler);

			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}

	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// get the number of attributes in the list
		int length = attributes.getLength();
		// process each attribute
		
		if(length == 0) return;
		
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		Document doc = new Document();
		try {
			for (int i = 0; i < length; i++) {
				// get qualified (prefixed) name by index
				String name = attributes.getQName(i);
				
				// get attribute's value by index.
				String value = attributes.getValue(i);
				
				if(name.equalsIgnoreCase("Id")) {
					// IntPoint
					int intVal = Integer.parseInt(value);
					doc.add(new IntPoint(name, intVal));
					doc.add(new StoredField(name, intVal));
				} else if(name.equalsIgnoreCase("PostTypeId")) {
					// IntPoint
					int intVal = Integer.parseInt(value);
					doc.add(new IntPoint(name, intVal));
					doc.add(new StoredField(name, intVal));
				} else if(name.equalsIgnoreCase("AcceptedAnswerId")) {
					// IntPoint
					int intVal = Integer.parseInt(value);
					doc.add(new IntPoint(name, intVal));
					doc.add(new StoredField(name, intVal));
				} else if(name.equalsIgnoreCase("CreationDate")) {
					// LongPoint
					Date datetime = df.parse(value);
					doc.add(new LongPoint(name, datetime.getTime()));
					doc.add(new StoredField(name, datetime.getTime()));
				} else if(name.equalsIgnoreCase("Score")) {
					// IntPoint
					int intVal = Integer.parseInt(value);
					doc.add(new IntPoint(name, intVal));
					doc.add(new StoredField(name, intVal));
				} else if(name.equalsIgnoreCase("ViewCount")) {
					// IntPoint
					int intVal = Integer.parseInt(value);
					doc.add(new IntPoint(name, intVal));
					doc.add(new StoredField(name, intVal));
				} else if(name.equalsIgnoreCase("Body")) {
					// TextField
					doc.add(new TextField(name, value, Field.Store.YES));
				} else if(name.equalsIgnoreCase("OwnerUserId")) {
					// IntPoint
					int intVal = Integer.parseInt(value);
					doc.add(new IntPoint(name, intVal));
					doc.add(new StoredField(name, intVal));
				} else if(name.equalsIgnoreCase("LastEditorUserId")) {
					// IntPoint
					int intVal = Integer.parseInt(value);
					doc.add(new IntPoint(name, intVal));
					doc.add(new StoredField(name, intVal));
				} else if(name.equalsIgnoreCase("LastEditorDisplayName")) {
					// TextField
					doc.add(new TextField(name, value, Field.Store.YES));
				} else if(name.equalsIgnoreCase("LastEditDate")) {
					// LongPoint
					Date datetime = df.parse(value);
					doc.add(new LongPoint(name, datetime.getTime()));
					doc.add(new StoredField(name, datetime.getTime()));
				} else if(name.equalsIgnoreCase("LastActivityDate")) {
					// LongPoint
					Date datetime = df.parse(value);
					doc.add(new LongPoint(name, datetime.getTime()));
					doc.add(new StoredField(name, datetime.getTime()));
				} else if(name.equalsIgnoreCase("Title")) {
					// TextField
					doc.add(new TextField(name, value, Field.Store.YES));
				} else if(name.equalsIgnoreCase("Tags")) {
					// TextField
					doc.add(new TextField(name, value, Field.Store.YES));
				} else if(name.equalsIgnoreCase("AnswerCount")) {
					// IntPoint
					int intVal = Integer.parseInt(value);
					doc.add(new IntPoint(name, intVal));
					doc.add(new StoredField(name, intVal));
				} else if(name.equalsIgnoreCase("CommentCount")) {
					// IntPoint
					int intVal = Integer.parseInt(value);
					doc.add(new IntPoint(name, intVal));
					doc.add(new StoredField(name, intVal));
				} else if(name.equalsIgnoreCase("FavoriteCount")) {
					// IntPoint
					int intVal = Integer.parseInt(value);
					doc.add(new IntPoint(name, intVal));
					doc.add(new StoredField(name, intVal));
				} else if(name.equalsIgnoreCase("CommunityOwnedDate")) {
					// LongPoint
					Date datetime = df.parse(value);
					doc.add(new LongPoint(name, datetime.getTime()));
					doc.add(new StoredField(name, datetime.getTime()));
				} else if(name.equalsIgnoreCase("ContentLicense")) {
					// Nothing
				} else if(name.equalsIgnoreCase("ParentId")) {
					// IntPoint
					int intVal = Integer.parseInt(value);
					doc.add(new IntPoint(name, intVal));
					doc.add(new StoredField(name, intVal));
				} else {
					// Nothing
				}
			}
	
			
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
		        System.out.println("adding: " + doc.get("Id"));
		        writer.addDocument(doc);
		      } else {
		        System.out.println("updating not implemented");
		      }
		
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
}

