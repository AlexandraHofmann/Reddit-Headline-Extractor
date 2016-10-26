import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


/**
 * 
 * This class acts as helper class
 *
 */
public class XMLToCSV {

	private BufferedWriter fileWriter;
	
  public static void main(String args[]) {
		File filexml = new File("/Users/alexandrahofmann/Documents/Master Uni MA/Data Mining/Projekt/newsFeed.xml");
		File filecsv = new File("/Users/alexandrahofmann/Documents/Master Uni MA/Data Mining/Projekt/newsFeed.csv");
		XMLToCSV converter = new XMLToCSV();
		converter.xmlToCsv(filexml, filecsv);
  }

public void xmlToCsv(File xml, File csv) {

    try {
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(xml);
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
