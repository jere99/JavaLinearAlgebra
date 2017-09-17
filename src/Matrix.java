
/**
 * Defines a matrix and provides operations to perform on the matrix.
 * 
 * @author JeremiahDeGreeff
 */
public class Matrix implements Cloneable {
	
	/**
	 * The main contents of this n x m Matrix.
	 */
	private double[][] contents;
	
	/**
	 * Cached reduced row echelon form so it only needs to be calculated once.
	 */
	private Matrix rref = null;
	
	/**
	 * Cached value for the rank of the Matrix so it only needs to be calculated once.
	 */
	private int rank = -1;
	
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
		Matrix result = new Matrix(copy);
		if(this.rref != null)
			result.rref = this.rref.clone();
		return result;
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
		clearCache();
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
		clearCache();
		double[][] old = contents;
		contents = newContents;
		return old;
	}
	
	/**
	 * Resets the cached rref and rank fields.
	 * Should be called whenever the contents of this Matrix change by means of anything other than an elementary row operation.
	 */
	private void clearCache() {
		rref = null;
		rank = -1;
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
	
	//================================================================================
    // Matrix Operations
    //================================================================================
	
	/**
	 * Uses Gauss-Jordan elimination to determine the reduced row echelon form of this Matrix.
	 * 
	 * @return the reduced row echelon form
	 */
	public Matrix rref() {
		if(rref != null)
			return rref;
		Matrix rref = this.clone();
		
		for(int i = 0, j = 0; i < rref.contents.length; i++) {
			while(j < rref.contents[0].length && rref.contents[i][j] == 0) {
				for(int iCheck = i + 1; iCheck < contents.length; iCheck++)
					if(rref.contents[iCheck][j] != 0) {
						rref.swapRows(i, iCheck);
						break;
					}
				if(rref.contents[i][j] == 0)
					j++;
			}
			if(j == rref.contents[0].length)
				break;
			rref.divideRow(i, rref.contents[i][j]);
			for(int iReduce = 0; iReduce < rref.contents.length; iReduce++)
				if(iReduce != i)
					rref.subtractRow(iReduce, rref.contents[iReduce][j], i);
		}
		
		this.rref = rref;
		return rref;
	}
	
	/**
	 * @return true if this Matrix is in reduced row echelon form, false otherwise
	 */
	public boolean isRref() {
		return this.equals(this.rref());
	}
	
	/**
	 * Calculates the rank of this Matrix.
	 * 
	 * @return the rank
	 */
	public int rank() {
		if(rank != -1)
			return rank;
		Matrix rref = rref();
		int rank = 0, i = 0, j = 0;
		while(i < rref.contents.length) {
			while(j < rref.contents[0].length) {
				if(rref.contents[i][j] == 1) {
					rank++;
					j++;
					break;
				}
				j++;
			}
			i++;
		}
		
		this.rank = rank;
		return rank;
	}
	
	/**
	 * Calculates the sum of this Matrix and another Matrix.
	 * 
	 * @param m the Matrix to add
	 * @return the sum of the two Matrices
	 * @throws ArithmeticException if the matrices have different dimensions
	 */
	public Matrix add(Matrix m) {
		if(this.contents.length != m.contents.length || this.contents[0].length != m.contents[0].length)
			throw new ArithmeticException("Cannot add Matricies of different dimensions.");
		
		Matrix sum = this.clone();
		for(int i = 0; i < sum.contents.length; i++)
			for(int j = 0; j < sum.contents[0].length; j++)
				sum.contents[i][j] += m.contents[i][j];
		return sum;
	}
	
}
