import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Date;


/**
 * 
 * This class acts as helper class.
 *
 */
public class XMLToCSV {

	private BufferedWriter fileWriter;
	private DocumentBuilderFactory dbFactory;
	private DocumentBuilder dBuilder;
	private Document doc;

	public static void main(String args[]) {
		// add file path if you want to test
		//File filexml = new File(filepath);
		//File filecsv = new File(filepath2);
		//XMLToCSV converter = new XMLToCSV();
		//converter.xmlToCsv(filexml, filecsv);
	}

	public XMLToCSV() {
		this.dbFactory = DocumentBuilderFactory.newInstance();
		try {
			this.dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String xmlToCsv(InputStream xmlInputStream, Date date) {
		StringBuffer buffer = new StringBuffer();
		try {
			doc = dBuilder.parse(xmlInputStream);			
			NodeList entryList = doc.getElementsByTagName("entry");
				
			System.out.println("Start XML Conversion");
			for (int index = 0; index < entryList.getLength(); index++) {
				Node entryItem = entryList.item(index);
				if (entryItem.getNodeType() == Node.ELEMENT_NODE) {
					Element entry = (Element) entryItem;
					//String updated = e.getElementsByTagName("updated").item(0).getTextContent();
					String title = entry.getElementsByTagName("title").item(0).getTextContent();
					//String entry = (i + "," + updated.substring(0, 10) + "," + title.replace(",", ""));
					
					// TODO: add more preprocessing, e.g. delete "", '', ...
					String entryCSV = ((index+1) + "," + date.toGMTString() + "," + title.replace(",", "").replace("\"", ""));
					buffer.append(entryCSV);
					buffer.append("\n");
				}
			}
			System.out.println("Stop XML Conversion");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	
	
	public void xmlToCsv(File xml, File csv) {

		try {
			doc = dBuilder.parse(xml);
			NodeList nodeList = doc.getElementsByTagName("entry");
			fileWriter = new BufferedWriter(new FileWriter(csv));

			System.out.println("Start Conversion");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) node;
					String updated = e.getElementsByTagName("updated").item(0).getTextContent();
					String title = e.getElementsByTagName("title").item(0).getTextContent();
					String entry = (i + "," + updated.substring(0, 10) + "," + title.replace(",", ""));
					System.out.println("Write entry into file: " + entry);
					fileWriter.write(entry);
					fileWriter.newLine();
				}
			}
			fileWriter.close();
			System.out.println("Stop Conversion");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
