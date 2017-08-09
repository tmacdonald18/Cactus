import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/*
 * ImageMatrix.java
 * Author:	Tyler MacDonald
 * Email:	tcmacd18@g.holycross.edu
 * Purpose:	Implement matrix operations in order to better manipulate the BufferedImage preview matrix
 */

public class ImageMatrix {

	private BufferedImage[][] matrix;	
	
	private int rows, cols;
	
	public ImageMatrix()
	/*
	 * Default constructor that creates a 1 by 1 matrix
	 */
	{
		rows = 1;
		cols = 1;
		matrix = new BufferedImage[rows][cols];
	}
	
	public ImageMatrix(HashMap<Integer, HashMap<Integer, BufferedImage>> mapping)
	/*
	 * Constructor that takes a hashmap and creates an ImageMatrix from it
	 */
	{
		int rowsMax = 0, colsMax = 0, rowsMin = 0, colsMin = Integer.MAX_VALUE;
		
		Object[] rowKeys = mapping.keySet().toArray();
		
		//Determine number of rows the matrix should have by taking the largest row number from the hashmap
		rowsMax = Collections.max(mapping.keySet());
		rowsMin = Collections.min(mapping.keySet());
		
		System.out.println("max rows is " + rowsMax + " and min rows is " + rowsMin);
		int currentMaxResult;
		int currentMinResult;
		
		//Determine number of columns the matrix should have by looping through each row value and getting the max of that hashtables keys
		//Then compare that result with the previously found, if it is bigger than set cols equal to that		
		for (int i = 0; i < rowKeys.length; i++) {
			
			currentMaxResult = Collections.max(mapping.get(rowKeys[i]).keySet());
			currentMinResult = Collections.min(mapping.get(rowKeys[i]).keySet());
			
			System.out.println(currentMaxResult + " is the current column max result");
			System.out.println(currentMinResult + " is the current column min result");
			
			if (currentMaxResult > colsMax)
				colsMax = currentMaxResult;
			
			if (currentMinResult < colsMin)
				colsMin = currentMinResult;
			
		}
		
		System.out.println("The amount of rows is equal to " + rowsMax + " minus " + rowsMin + " plus 1.");
		System.out.println("The amount of cols is equal to " + colsMax + " minus " + colsMin + " plus 1.");
		rows = (rowsMax - rowsMin) + 1;
		cols = (colsMax - colsMin) + 1;
		
		System.out.println("This many rows: " + rows + " and this many columns: " + cols);
		
		matrix = new BufferedImage[rows][cols];
		
		//Set the image matrix's values
		//For each matrix row, if the hashmap contains that key
		for (int matrixRow = 0; matrixRow < rows; matrixRow++) {
			
			int rowQuery = matrixRow + rowsMin;
			
			if (mapping.containsKey(rowQuery)) {
				for (int matrixCol = 0; matrixCol < cols; matrixCol++) {
					
					int colQuery = matrixCol + colsMin;
					
					if (mapping.get(rowQuery).containsKey(colQuery)) {
						matrix[matrixRow][matrixCol] = mapping.get(rowQuery).get(colQuery);
					}
				}
			}
		}
		
		
		System.out.println("Image Matrix Created.");
	}

	public void printThisMatrix() {
		System.out.println();
		for (int i = 0; i < rows; i++) {
			System.out.print("[");
			for (int j = 0; j < cols; j++) {
				if (matrix[i][j] == null)
					System.out.print("0, ");
				else
					System.out.print("1, ");
			}
			System.out.print("],");
			System.out.println();
		}
	}
	
	private void transpose(boolean positive)
	/*
	 * Takes the matrix and converts it to its transpose by swapping each row,col pair with the col,row pair
	 */
	{
		int newRows = cols;
		int newCols = rows;
		
		BufferedImage[][] newMatrix = new BufferedImage[newRows][newCols];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				
				BufferedImage img = matrix[i][j];
				
				if (img != null) {
					img = rotateImage(img, positive);
					newMatrix[j][i] = img;
				}
				
			}
		}
		
		matrix = newMatrix;
		
		rows = newRows;
		cols = newCols;
		
	}
	
	private void swapRows()
	/*
	 * Swaps each row of the matrix
	 */
	{
		for (int i = 0, k = matrix.length - 1; i < k; ++i, --k) {
			BufferedImage[] temp = matrix[i];
			matrix[i] = matrix[k];
			matrix[k] = temp;
		}
	}
	
	private BufferedImage rotateImage(BufferedImage image, boolean positive)
	/*
	 * Rotates an image 90 degrees clockwise or counterclockwise
	 */
	{
		AffineTransform at = new AffineTransform();
		
		//First translate the image to the center of the Tile
		at.translate(16, 16);
		
		//Determine if clockwise or counterclockwise rotation
		if (positive)
			at.rotate(Math.PI / 2);	
		else
			at.rotate(-Math.PI / 2);
		
		//Move back to the correct position
		at.translate(-16, -16);
		
		BufferedImage returnImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = (Graphics2D) returnImage.getGraphics();
		
		g.drawImage(image, at, null);
		
		g.dispose();
		
		return returnImage;
		
	}
	
	public void rotateLeft()
	/*
	 * Rotates matrix 90 degrees to the left
	 * Also rotates images accordingly
	 */
	{
		transpose(false);
		swapRows();
		System.out.println("Rotated left.");
	}
	
	public void rotateRight()
	/*
	 * Rotates matrix 90 degrees to the right
	 * Also rotates images accordingly
	 */
	{
		swapRows();
		transpose(true);
		System.out.println("Rotated right.");
	}
	
	public void clearPreview() 
	{
		matrix = null;
	}
	
	public BufferedImage getTile(int row, int col) 
	{
		return matrix[row][col];
	}
	
	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	@Override
	public String toString() {
		return "ImageMatrix [matrix=" + Arrays.toString(matrix) + "]";
	}
	
}
