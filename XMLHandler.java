import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * XMLHandler.java
 * Author:	Tyler MacDonald
 * Email:	tcmacd18@g.holycross.edu
 * Purpose:	Handle saving to XML and loading from XML.
 */
public class XMLHandler {
	
	private Document doc;
	
	private File fXmlFile;
	
	public XMLHandler()
	/*
	 * Default constructor creates an XMLHandler for creating XML Files
	 */
	{
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.newDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public XMLHandler(String filePath)
	/*
	 * Constructor that opens an XMLFile to read from
	 */
	{
		try {
			fXmlFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadFromFile(String path, Container contentPane)
	/*
	 * Takes a path to a file and a the contentpane and loads into it
	 */
	{
		doc.getDocumentElement().normalize();
		
		System.out.println("Root Element: " + doc.getDocumentElement().getNodeName());
		
		NodeList nList = doc.getElementsByTagName("LevelGrid");
		System.out.println("--------------");
		
		for (int temp = 0; temp < nList.getLength(); temp++) {
			
			Node nNode = nList.item(temp);
			
			System.out.println("\nCurrent Element: " + nNode.getNodeName());
			
			System.out.println(nNode.getChildNodes());
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element eElement = (Element) nNode;
				
				System.out.println(eElement.getElementsByTagName("Layer"));
				
			}
			
			
		}
			
	}
	
	public int getRows(String gridName)
	/*
	 * Gets the number of rows in the given saved GridUI
	 */
	{		
		
		NodeList nList = doc.getElementsByTagName(gridName);
		
		for (int x = 0; x < nList.getLength(); x++) {
			Node nNode = nList.item(x);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				return Integer.parseInt(nNode.getFirstChild().getTextContent());
			}
		}
		
		return -1;
	}
	
	public int getCols(String gridName)
	/*
	 * Gets the number of columns in the given saved GridUI
	 */
	{
		NodeList nList = doc.getElementsByTagName(gridName);
		
		for (int x = 0; x < nList.getLength(); x++) {
			Node nNode = nList.item(x);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				return Integer.parseInt(nNode.getFirstChild().getNextSibling().getTextContent());
			}
		}
		
		return -1;
	}
	
	public int getLayers(String gridName)
	/*
	 * Gets the number of layers in the given saved GridUI
	 */
	{
		NodeList nList = doc.getElementsByTagName(gridName);
		
		for (int x = 0; x < nList.getLength(); x++) {
			Node nNode = nList.item(x);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				return Integer.parseInt(nNode.getFirstChild().getNextSibling().getNextSibling().getTextContent());
			}
		}
		
		return -1;
	}
	
	public BufferedImage[][][] getImages(int layers, int rows, int cols)
	/*
	 * In a given grid, gets a specific save tile and converts it to a BufferedImage
	 */
	{
		//Layers count starts at 0 so add 1
		layers = layers + 1;
		
		BufferedImage[][][] images = new BufferedImage[layers][rows][cols];
		
		for (int k = 0; k < layers; k++) {
			
			System.out.println("ON THE FIRST LAYER");
			
			//Get the first row from the given layer
			Node row = doc.getElementsByTagName("Layer_" + k).item(0).getFirstChild();
			
			System.out.println(row.getNodeName());
			
			for (int i = 0; i < rows; i++) { 
				
				//Get first column node
				Node col = row.getFirstChild().getNextSibling();
				
				System.out.println(col.getNodeName());
				
				for (int j = 0; j < cols; j++) {
					
					Node tileImg = col.getFirstChild().getNextSibling();
					System.out.println(tileImg.getTextContent());
					
					if (!tileImg.getTextContent().contains("NULL"))
						images[k][i][j] = convertStringToImage(tileImg.getTextContent());
					
					col = col.getNextSibling();
				
				}
				
				row = row.getNextSibling();
			
			}
			
		}
		
		return images;
	}
	
	public void saveToFile(String path, int totalLayers, GridUI levelBuilderGrid, GridUI tileChooserGrid)
	/*
	 * Given a path, saves contentPane to that file
	 */
	{
		int rows = levelBuilderGrid.getRows();
		int cols = levelBuilderGrid.getCols();
		
		try {
			
			//Root element
			Element rootElement = doc.createElement("Level");
			doc.appendChild(rootElement);
			
			//LevelGrid element
			Element levelGrid = doc.createElement("LevelGrid");
			rootElement.appendChild(levelGrid);
			
			Element rowCount = doc.createElement("Rows");
			rowCount.appendChild(doc.createTextNode(Integer.toString(rows)));
			levelGrid.appendChild(rowCount);
			
			Element colCount = doc.createElement("Cols");
			colCount.appendChild(doc.createTextNode(Integer.toString(cols)));
			levelGrid.appendChild(colCount);
			
			Element layersCount = doc.createElement("LayersCount");
			layersCount.appendChild(doc.createTextNode(Integer.toString(totalLayers)));
			levelGrid.appendChild(layersCount);
			
			System.out.println("Saving Level");
			
			//Add all tiles to it
			for (int k = 0; k < totalLayers + 1; k++) {
				Element layer = doc.createElement("Layer_" + k);
				levelGrid.appendChild(layer);
				
				for (int i = 0; i < rows; i++) {
					//Create new row elements to hold columns
					Element tileRow = doc.createElement("Row");
					tileRow.appendChild(doc.createTextNode(Integer.toString(i)));
					layer.appendChild(tileRow);
					
					for (int j = 0; j < cols; j++) {
						//Create new column element to hold tile data
						Element tileCol = doc.createElement("Col");
						tileCol.appendChild(doc.createTextNode(Integer.toString(j)));
						tileRow.appendChild(tileCol);
						
						BufferedImage curImg = levelBuilderGrid.getTile(i, j).getImage(k);
						
						Element tileImg = doc.createElement("TileImg");
						
						if (curImg == null) {
							tileImg.appendChild(doc.createTextNode("NULL"));
						} else {
							tileImg.appendChild(doc.createTextNode(convertImageToString(curImg)));
						}
						
						tileCol.appendChild(tileImg);
					}
				}
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("C:\\Users\\N0286782\\Documents\\saveFile.xml"));
			
			transformer.transform(source, result);
			
			System.out.println("File Saved!");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private BufferedImage convertStringToImage(String base64Image)
	/*
	 * Takes a base64 encoded png image byte stream and converts it to a BufferedImage
	 */
	{
		byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64Image);
		
		InputStream in = new ByteArrayInputStream(imageBytes);
		BufferedImage img;
		
		try {
			img = ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
			img = null;
		}
		
		System.out.println(img.toString());
		
		return img;
	}
	
	private String convertImageToString(BufferedImage img)
	/*
	 * Takes a BufferedImage and converts it's binary to base64
	 */
	{
		String imageAsString = "";
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try {
			ImageIO.write(img, "png", bos);
			byte[] imageBytes = bos.toByteArray();
			
			imageAsString = DatatypeConverter.printBase64Binary(imageBytes);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return imageAsString;
	}
}
