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
	
	public ImageMatrix(HashMap<Integer, HashMap<Integer, BufferedImage>> mapping)
	/*
	 * Constructor that takes a hashmap and creates an ImageMatrix from it
	 */
	{
		int rowsMax = 0, colsMax = 0, rowsMin = 0, colsMin = 0;
		
		Object[] rowKeys = mapping.keySet().toArray();
		Object[] colKeys;
		
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
			
			System.out.println(currentMaxResult + " is the current max result");
			System.out.println(currentMinResult + " is the current min result");
			
			if (currentMaxResult > colsMax)
				colsMax = currentMaxResult;
			
			if (currentMinResult < colsMin)
				colsMin = currentMinResult;
			
		}
		
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
	
	@Override
	public String toString() {
		return "ImageMatrix [matrix=" + Arrays.toString(matrix) + "]";
	}
}
