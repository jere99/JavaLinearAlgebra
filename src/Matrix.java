
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
		if(this.rank != -1)
			result.rank = this.rank;
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
				result.append(String.format("%6s", (Math.rint(element) == element ? String.format("% d", (int)element) : String.format("% .3f", element))));
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
	 * Determines if this Matrix is a square matrix, that is if it has the same number of rows as it does columns.
	 * 
	 * @return true if this Matrix is square, false otherwise
	 */
	public boolean isSquare() {
		return contents.length == contents[0].length;
	}
	
	/**
	 * Determines if this Matrix is diagonal, that is if all of its entries which are not on the main diagonal are zeros.
	 * 
	 * @return true if this Matrix is diagonal, false otherwise
	 */
	public boolean isDiagonal() {
		for(int i = 0; i < contents.length; i++)
			for(int j = 0; j < contents[0].length; j++)
				if(i != j && contents[i][j] != 0)
					return false;
		return true;
	}
	
	/**
	 * Determines if this Matrix is upper triangular, that is if all of its entries which are below the main diagonal are zeros.
	 * 
	 * @return true if this Matrix is upper triangular, false otherwise
	 */
	public boolean isUpperTriangular() {
		for(int i = 0; i < contents.length; i++)
			for(int j = 0; j < i; j++)
				if(contents[i][j] != 0)
					return false;
		return true;
	}
	
	/**
	 * Determines if this Matrix is lower triangular, that is if all of its entries which are above the main diagonal are zeros.
	 * 
	 * @return true if this Matrix is lower triangular, false otherwise
	 */
	public boolean isLowerTriangular() {
		for(int i = 0; i < contents.length; i++)
			for(int j = i + 1; j < contents[0].length; j++)
				if(contents[i][j] != 0)
					return false;
		return true;
	}
	
	/**
	 * Determines if this Matrix is a zero matrix, that is if all of its entries are zeros.
	 * 
	 * @return true if this Matrix is a zero matrix, false otherwise
	 */
	public boolean isZero() {
		for(int i = 0; i < contents.length; i++)
			for(int j = 0; j < contents[0].length; j++)
				if(contents[i][j] != 0)
					return false;
		return true;
	}
	
	/**
	 * Retrieves the all the values in one row of this Matrix.
	 * 
	 * @param i the index of the row to retrieve
	 * @return an array of all the values in the specified row of this Matrix
	 * @throws IllegalArgumentException if {@code i} is negative or exceeds the valid indices of rows in this Matrix
	 */
	public double[] getRowVector(int i) {
		if(i < 0 || i >= contents.length)
			throw new IllegalArgumentException("The paramter i was not in the valid range [0, " + (contents.length - 1) + "].");
		return contents[i];
	}
	
	/**
	 * Retrieves the all the values in one column of this Matrix.
	 * 
	 * @param j the index of the column to retrieve
	 * @return an array of all the values in the specified column of this Matrix
	 * @throws IllegalArgumentException if {@code j} is negative or exceeds the valid indices of columns in this Matrix
	 */
	public double[] getColumnVector(int j) {
		if(j < 0 || j >= contents[0].length)
			throw new IllegalArgumentException("The paramter j was not in the valid range [0, " + (contents[0].length - 1) + "].");
		double[] column = new double[contents.length];
		for(int i = 0; i < column.length; i++)
			column[i] = contents[i][j];
		return column;
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
	 * Assumes that this instance is an augmented matrix which represents a system of linear equations.
	 * Calculates the solution for that system, if it exists.
	 * 
	 * @return the solution of the system if there is exactly one solution, null if there is no solution or infinitely many solutions
	 */
	public double[] findSolution() {
		if(rank() != contents[0].length - 1 || contents[0].length == 1) //forces generation of rref
			return null;
		//handles rank()'s counting a one in the final column of an augment matrix as a leading one
		for(int i = contents.length - 1; contents[i][contents[0].length - 2] == 0 && (contents[0].length == 2 || (contents[0].length  >= 3 && contents[i][contents[0].length - 3] == 0)); i--)
			if(contents[i][contents[0].length - 1] == 1)
				return null;
		double[] result = new double[contents.length];
		for(int i = 0; i < result.length; i++)
			result[i] = rref.contents[i][contents[0].length - 1];
		return result;
	}
	
	/**
	 * Assumes that this instance is a coefficient matrix which when augmented with the passed array represents a system of linear equations.
	 * Calculates the solution for that system, if it exists.
	 * 
	 * @param augment the column to turn this coefficient matrix into an augmented matrix
	 * @return the solution of the system if there is exactly one solution, null if there is no solution or infinitely many solutions
	 */
	public double[] findSolution(double[] augment) {
		double[][] augmentedContents = new double[contents.length][contents[0].length + 1];
		for(int i = 0; i < contents.length; i++) {
			for(int j = 0; j < contents[0].length; j++)
				augmentedContents[i][j] = contents[i][j];
			augmentedContents[i][augmentedContents[0].length - 1] = augment[i];
		}
		return new Matrix(augmentedContents).findSolution();
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
	
	/**
	 * Calculates the product of this Matrix and a scalar quantity.
	 * 
	 * @param scalar the scalar to multiply by
	 * @return the product of the Matrix and the scalar
	 */
	public Matrix scalarMultiply(double scalar) {
		Matrix product = this.clone();
		for(int i = 0; i < product.contents.length; i++)
			for(int j = 0; j < product.contents[0].length; j++)
				product.contents[i][j] *= scalar;
		return product;
	}
	
	/**
	 * Calculates the result of multiplying this Matrix by another Matrix.
	 * Note that this function is not commutative; in other words for Matrices m1 and m2
	 * <blockquote>
	 * m1.matrixMultiply(m2).equals(m2.matrixMultiply(m1))
	 * </blockquote>
	 * will not necessarily have a value of true.
	 * 
	 * @param m the Matrix to multiply by
	 * @return the product of the Matrices
	 */
	public Matrix matrixMultiply(Matrix m) {
		if(this.contents[0].length != m.contents.length)
			throw new ArithmeticException("The number of columns in the first Matrix must match the number of rows in the second to multiply them.");
		
		Matrix product = new Matrix(this.contents.length, m.contents[0].length);
		for(int i = 0; i < product.contents.length; i++)
			for(int j = 0; j < product.contents[0].length; j++)
				product.contents[i][j] = dotProduct(this.contents[i], m.getColumnVector(j));
		return product;
	}
	
	//================================================================================
	// Vector Operations
	//================================================================================
	
	/**
	 * Calculates the dot product of two vectors.
	 * The two vectors must have the same number of components.
	 * 
	 * @param v the first vector
	 * @param w the second vector
	 * @return the dot product of the vectors
	 * @throws ArithmeticException if the vectors have different numbers of components
	 */
	public static double dotProduct(double[] v, double[] w) {
		if(v.length != w.length)
			throw new ArithmeticException("The number of components in the two vectors must match to perform the dot product operation.");
		double sum = 0;
		for(int i = 0; i < v.length; i++)
			sum += v[i] * w[i];
		return sum;
	}
	
}
