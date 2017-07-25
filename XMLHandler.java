import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.bind.DatatypeConverter;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * XMLHandler.java
 * Author:	Tyler MacDonald
 * Email:	tcmacd18@g.holycross.edu
 * Purpose:	Handle saving to XML and loading from XML.
 */
public class XMLHandler {
	
	public void loadFromFile(String path, Container contentPane)
	/*
	 * Takes a path to a file and a the contentpane and loads into it
	 */
	{
		
	}
	
	public void saveToFile(String path, int totalLayers, GridUI levelBuilderGrid, GridUI tileChooserGrid)
	/*
	 * Given a path, saves contentPane to that file
	 */
	{
		int rows = levelBuilderGrid.getRows();
		int cols = levelBuilderGrid.getCols();
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			//Root element
			Document doc = docBuilder.newDocument();
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
			
			//Add all tiles to it
			for (int k = 0; k < totalLayers; k++) {
				Element layer = doc.createElement("Layer");
				Attr layerNum = doc.createAttribute("Number");
				layerNum.setValue(Integer.toString(k));
				layer.setAttributeNode(layerNum);
				levelGrid.appendChild(layer);
				
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < cols; j++) {
						BufferedImage curImg = levelBuilderGrid.getTile(i, j).getImage(k);
						
						Element tile = doc.createElement("Tile");
						
						Attr row = doc.createAttribute("row");
						row.setValue(Integer.toString(i));
						tile.setAttributeNode(row);
						
						Attr col = doc.createAttribute("col");
						col.setValue(Integer.toString(j));
						tile.setAttributeNode(col);
						
						if (curImg == null) {
							tile.appendChild(doc.createTextNode("NULL"));
						} else {
							tile.appendChild(doc.createTextNode(convertImageToString(curImg)));
						}
						
						layer.appendChild(tile);
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
