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
			
		if (colsMin == 0)
			colsMin = 1;
		if (rowsMin == 0)
			rowsMin = 1;
		
		int rows = rowsMax - rowsMin - 1;
		int cols = colsMax - colsMin - 1;
		
		System.out.println("This many rows: " + rows + " and this many columns: " + cols);
		
		matrix = new BufferedImage[rows][cols];
		
		int rowKey, colKey;
		
		//For each row key, loop through each column key and set the resulting BufferedImage value to the appropriate matrix position
		for (int i = 0; i < rowKeys.length; i++) {
			
			System.out.println("I am on row " + (i+1) + " out of " + rowKeys.length);
			
			colKeys = mapping.get(rowKeys[i]).keySet().toArray();
			rowKey = (int) rowKeys[i];
			
			for (int j = 0; j < colKeys.length; j++) {
				
				System.out.println("         I am on column " + (j+1) + " out of " + colKeys.length);
				colKey = (int) colKeys[j];
				
				matrix[i][j] = mapping.get(rowKeys[i]).get(colKeys[j]);
			}
		}
		
		System.out.println("Image Matrix Created.");
	}

	@Override
	public String toString() {
		return "ImageMatrix [matrix=" + Arrays.toString(matrix) + "]";
	}
}
