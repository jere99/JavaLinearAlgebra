
/**
 * Defines a matrix and provides operations to perform on the matrix.
 * 
 * @author JeremiahDeGreeff
 */
public class Matrix implements Cloneable {
	
	private double[][] contents;
	
	/**
	 * Initializes an m x n Matrix without any initial contents.
	 * 
	 * @param n the number of rows in the matrix
	 * @param m the number of columns in the matrix
	 */
	public Matrix(int n, int m) {
		contents = new double[n][m];
	}
	
	/**
	 * Initializes a Matrix with initial contents.
	 * 
	 * @param initialContents the initial contents for the Matrix.
	 */
	public Matrix(double[][] initialContents) {
		contents = initialContents;
	}
	
	/**
	 * Creates and returns a Matrix with identical contents.
	 */
	@Override
	public Matrix clone() {
		double[][] copy = new double[contents.length][contents[0].length];
		for(int i = 0; i < contents.length; i++)
			for(int j = 0; j < contents[0].length; j++)
				copy[i][j] = contents[i][j];
		return new Matrix(copy);
	}
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * <p>
	 * The other object is considered "equal" if either:
	 * <ul>
	 * <li>The other object references this instance. In other words {@code this == obj} has the value {@code true}.</li>
	 * <li>The other object has contents identical to those of this instance.</li>
	 * </ul>
	 * </p>
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(!(obj instanceof Matrix))
			return false;
		Matrix matrix = (Matrix) obj;
		if(this.contents.length != matrix.contents.length || this.contents[0].length != matrix.contents[0].length)
			return false;
		for(int i = 0; i < contents.length; i++)
			for(int j = 0; j < contents[0].length; j++)
				if(this.contents[i][j] != matrix.contents[i][j])
					return false;
		return true;
	}
	
	/**
	 * Returns a string representation of the Matrix.
	 * 
	 * @return  a string representation of the Matrix.
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer((4 + 6 * contents[0].length) * contents.length);
		for(double[] row : contents) {
			result.append("|");
			for(double element : row)
				result.append(String.format("%-6s", (Math.rint(element) == element ? String.format("% d", (int)element) : String.format("% .3f", element))));
			result.append(" |\n");
		}
		return result.toString();
	}
	
	/**
	 * Sets a row in this Matrix.
	 * 
	 * @param i the index of the row to set
	 * @param newRow the contents of the new row
	 * @return the old row at the same index
	 * @throws IllegalArgumentException if {@code i} is negative or exceeds the valid indices of rows in this Matrix
	 */
	public double[] setRow(int i, double[] newRow) {
		if(i < 0 || i >= contents.length)
			throw new IllegalArgumentException("The paramter i was not in the valid range [0, " + (contents.length - 1) + "].");
		double[] old = contents[i];
		contents[i] = newRow;
		return old;
	}
	
	/**
	 * Sets the entire contents of this Matrix.
	 * 
	 * @param newContents the contents to set
	 * @return the old contents
	 */
	public double[][] setContents(double[][] newContents) {
		double[][] old = contents;
		contents = newContents;
		return old;
	}
	
	//================================================================================
    // Elementary Row Operations
    //================================================================================
	
	/**
	 * Divides a row in this Matrix by a non-zero scalar.
	 * 
	 * @param i the index of the row to set
	 * @param scalar the non-zero number to divide by
	 * @throws IllegalArgumentException if {@code scalar == 0} or if {@code i] is negative or exceeds the valid indices of rows in this Matrix
	 */
	public void divideRow(int i, double scalar) {
		if(scalar == 0)
			throw new IllegalArgumentException("Cannot divide a row by 0.");
		if(i < 0 || i >= contents.length)
			throw new IllegalArgumentException("The paramter i was not in the valid range [0, " + (contents.length - 1) + "].");
		
		for(int j = 0; j < contents[i].length; j++)
			contents[i][j] /= scalar;
	}
	
	/**
	 * Subtracts a multiple of one row in this Matrix from another one.
	 * 
	 * @param iTarget the row which will be subtracted from
	 * @param multiple the number to multiply the source row by before subtracting
	 * @param iSource the row to subtract from the target row
	 * @throws IllegalArgumentException if {@code iTarget} or {@code iSource} is negative or exceeds the valid indices of rows in this Matrix
	 */
	public void subtractRow(int iTarget, double multiple, int iSource) {
		if(iTarget < 0 || iTarget >= contents.length)
			throw new IllegalArgumentException("The paramter iTarget was not in the valid range [0, " + (contents.length - 1) + "].");
		if(iSource < 0 || iSource >= contents.length)
			throw new IllegalArgumentException("The paramter iSource was not in the valid range [0, " + (contents.length - 1) + "].");
		
		for(int j = 0; j < contents[iTarget].length; j++)
			contents[iTarget][j] -= contents[iSource][j] * multiple;
	}
	
	/**
	 * Swaps the contents of two rows in this Matrix.
	 * 
	 * @param i1 the first row to swap
	 * @param i2 the second row to swap
	 * @throws IllegalArgumentException if {@code i1} or {@code i2} is negative or exceeds the valid indices of rows in this Matrix
	 */
	public void swapRows(int i1, int i2) {
		if(i1 < 0 || i1 >= contents.length)
			throw new IllegalArgumentException("The paramter i1 was not in the valid range [0, " + (contents.length - 1) + "].");
		if(i2 < 0 || i2 >= contents.length)
			throw new IllegalArgumentException("The paramter i2 was not in the valid range [0, " + (contents.length - 1) + "].");
		
		double[] temp = contents[i1];
		contents[i1] = contents[i2];
		contents[i2] = temp;
	}
	
}
