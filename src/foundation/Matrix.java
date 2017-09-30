package foundation;

/**
 * Defines a matrix and provides matrix operations.
 * 
 * @author JeremiahDeGreeff
 */
public class Matrix implements Cloneable {
	
	//================================================================================
	// Instance Variables
	//================================================================================
	
	/**
	 * The main contents of this n x m Matrix.
	 */
	private double[][] contents;
	
	/**
	 * Cached reduced row echelon form so it only needs to be calculated once.
	 * If this Matrix is in reduced row echelon form, this field could be self-referential.
	 */
	private Matrix rref = null;
	
	/**
	 * Cached value for the rank of the Matrix so it only needs to be calculated once.
	 */
	private int rank = -1;
	
	//================================================================================
	// Constructors
	//================================================================================
	
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
	 * @param initialContents the initial contents for the Matrix
	 * @throws IllegalArgumentException if the rows of {@code initialContents} do not all have the same length
	 */
	public Matrix(double[][] initialContents) {
		for(double[] row : initialContents)
			if(row.length != initialContents[0].length)
				throw new IllegalArgumentException("The parameter initialContents is invalid - all of its rows must have the same length.");
		
		contents = initialContents;
	}
	
	//================================================================================
	// Overrides
	//================================================================================
	
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
		result.rref = this.rref == null ? null : this.equals(this.rref) ? result : this.rref.clone();
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
	 * <li>The other object is a {@code Matrix} and has contents identical to those of this instance.</li>
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
	 * Generates a string representation of the Matrix.
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
	
	//================================================================================
	// Mutator Methods
	//================================================================================
	
	/**
	 * Sets a value in this Matrix.
	 * 
	 * @param i the index of the row to set
	 * @param j the index of the column to set
	 * @param newValue the new value to set
	 * @return the old value at the same indices
	 * @throws IllegalArgumentException
	 * 		if {@code i} is negative or exceeds the valid indices of rows in this Matrix
	 * 		or {@code j} is negative or exceeds the valid indices of rows in this Matrix
	 */
	public double setValue(int i, int j, double newValue) {
		if(i < 0 || i >= contents.length)
			throw new IllegalArgumentException("The paramter i was not in the valid range [0, " + (contents.length - 1) + "].");
		if(j < 0 || j >= contents[0].length)
			throw new IllegalArgumentException("The paramter j was not in the valid range [0, " + (contents[0].length - 1) + "].");
		
		clearCache();
		double old = contents[i][j];
		contents[i][j] = newValue;
		return old;
	}
	
	/**
	 * Sets a row in this Matrix.
	 * 
	 * @param i the index of the row to set
	 * @param newRow the contents of the new row
	 * @return the old row at the same index
	 * @throws IllegalArgumentException
	 * 		if {@code i} is negative or exceeds the valid indices of rows in this Matrix
	 * 		or the length of {@code newRow} is not the same as the length of the other rows in this Matrix
	 */
	public double[] setRow(int i, double[] newRow) {
		if(i < 0 || i >= contents.length)
			throw new IllegalArgumentException("The paramter i was not in the valid range [0, " + (contents.length - 1) + "].");
		if(newRow.length != contents[0].length)
			throw new IllegalArgumentException("The parameter newRow has an invalid length - it must be length: " + contents[0].length + ".");
		
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
	 * @throws IllegalArgumentException if the rows of {@code newContents} do not all have the same length
	 */
	public double[][] setContents(double[][] newContents) {
		for(double[] row : newContents)
			if(row.length != newContents[0].length)
				throw new IllegalArgumentException("The parameter newContents is invalid - all of its rows must have the same length.");
		
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
	// Accessor Methods
	//================================================================================
	
	/**
	 * Retrieves the the row vector of one of the rows in this Matrix.
	 * 
	 * @param i the index of the row to retrieve
	 * @return the row vector of row {@code i} in this Matrix
	 * @throws IllegalArgumentException if {@code i} is negative or exceeds the valid indices of rows in this Matrix
	 */
	public Vector getRowVector(int i) {
		if(i < 0 || i >= contents.length)
			throw new IllegalArgumentException("The paramter i was not in the valid range [0, " + (contents.length - 1) + "].");
		
		return new Vector(contents[i]);
	}
	
	/**
	 * Retrieves the the column vector of one of the column in this Matrix.
	 * 
	 * @param j the index of the column to retrieve
	 * @return the column vector of column {@code j} in this Matrix
	 * @throws IllegalArgumentException if {@code j} is negative or exceeds the valid indices of columns in this Matrix
	 */
	public Vector getColumnVector(int j) {
		if(j < 0 || j >= contents[0].length)
			throw new IllegalArgumentException("The paramter j was not in the valid range [0, " + (contents[0].length - 1) + "].");
		
		double[] column = new double[contents.length];
		for(int i = 0; i < column.length; i++)
			column[i] = contents[i][j];
		return new Vector(column);
	}
	
	/**
	 * Retrieves the contents of this Matrix.
	 * 
	 * @return the contents of this Matrix
	 */
	public double[][] getContents() {
		return contents;
	}
	
	/**
	 * Determines if this Matrix is a square matrix, that is if:
	 * <ul>
	 * <li>it has the same number of rows as it does columns</li>
	 * </ul>
	 * @return true if this Matrix is square, false otherwise
	 */
	public boolean isSquare() {
		return contents.length == contents[0].length;
	}
	
	/**
	 * Determines if this Matrix is diagonal, that is if:
	 * <ul>
	 * <li>it is square</li>
	 * <li>all of its entries which are not on the main diagonal are zeros</li>
	 * </ul>
	 * 
	 * @return true if this Matrix is diagonal, false otherwise
	 */
	public boolean isDiagonal() {
		if(contents.length != contents[0].length)
			return false;
		for(int i = 0; i < contents.length; i++)
			for(int j = 0; j < contents[0].length; j++)
				if(i != j && contents[i][j] != 0)
					return false;
		return true;
	}
	
	/**
	 * Determines if this Matrix is upper triangular, that is if:
	 * <ul>
	 * <li>it is square</li>
	 * <li>all of its entries which are below the main diagonal are zeros</li>
	 * </ul>
	 * 
	 * @return true if this Matrix is upper triangular, false otherwise
	 */
	public boolean isUpperTriangular() {
		if(contents.length != contents[0].length)
			return false;
		for(int i = 0; i < contents.length; i++)
			for(int j = 0; j < i; j++)
				if(contents[i][j] != 0)
					return false;
		return true;
	}
	
	/**
	 * Determines if this Matrix is lower triangular, that is if:
	 * <ul>
	 * <li>it is square</li>
	 * <li>all of its entries which are above the main diagonal are zeros</li>
	 * </ul>
	 * 
	 * @return true if this Matrix is lower triangular, false otherwise
	 */
	public boolean isLowerTriangular() {
		if(contents.length != contents[0].length)
			return false;
		for(int i = 0; i < contents.length; i++)
			for(int j = i + 1; j < contents[0].length; j++)
				if(contents[i][j] != 0)
					return false;
		return true;
	}
	
	/**
	 * Determines if this Matrix is an identity Matrix, that is if:
	 * <ul>
	 * <li>it is square</li>
	 * <li>it is diagonal</li>
	 * <li>all of its entries which are on the main diagonal are ones</li>
	 * </ul>
	 * 
	 * @return true if this Matrix is diagonal, false otherwise
	 */
	public boolean isIndentityMatrix() {
		if(contents.length != contents[0].length)
			return false;
		for(int i = 0; i < contents.length; i++)
			for(int j = 0; j < contents[0].length; j++)
				if(i != j && contents[i][j] != 0 || i == j && contents[i][j] != 1)
					return false;
		return true;
	}
	
	/**
	 * Determines if this Matrix is a zero matrix, that is if all of its entries are zeros.
	 * 
	 * @return true if this Matrix is a zero matrix, false otherwise
	 */
	public boolean isZero() {
		for(double[] row : contents)
			for(double element : row)
				if(element != 0)
					return false;
		return true;
	}
	
	/**
	 * @return true if this Matrix is in reduced row echelon form, false otherwise
	 */
	public boolean isRref() {
		return this.equals(this.rref());
	}
	
	/**
	 * Assumes that this instance is an augmented matrix which represents a system of linear equations.
	 * Determines whether or not this system is consistent.
	 * 
	 * @return true if the system is consistent, false otherwise
	 */
	public boolean isConsistent() {
		if(rank(true) == contents.length || contents[0].length == 1) //call to rank forces generation of rref
			return true;
		for(int i = contents.length - 1; rref.contents[i][contents[0].length - 2] == 0; i--)
			if(rref.contents[i][contents[0].length - 1] == 1)
				return false;
		return true;
	}
	
	/**
	 * Assumes that this instance is a coefficient matrix which when augmented with the passed array represents a system of linear equations.
	 * Determines whether or not this system is consistent.
	 * 
	 * @param augment the column to turn this coefficient matrix into an augmented matrix
	 * @return true if the system is consistent, false otherwise
	 */
	public boolean isConsistent(double[] augment) {
		return augment(augment).isConsistent();
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
	 * Creates a new Matrix object by augmenting this Matrix.
	 * The augment is added as an additional column.
	 * Intended to be used to turn a coefficient Matrix into an augmented Matrix.
	 * 
	 * @param augment the column vector to append to this Matrix
	 * @return a Matrix which is an augmented form of this Matrix
	 */
	public Matrix augment(double[] augment) {
		double[][] augmentedContents = new double[contents.length][contents[0].length + 1];
		for(int i = 0; i < contents.length; i++) {
			for(int j = 0; j < contents[0].length; j++)
				augmentedContents[i][j] = contents[i][j];
			augmentedContents[i][augmentedContents[0].length - 1] = augment[i];
		}
		return new Matrix(augmentedContents);
	}
	
	/**
	 * Uses Gauss-Jordan elimination to determine the reduced row echelon form of this Matrix.
	 * 
	 * @return the reduced row echelon form
	 */
	public Matrix rref() {
		if(rref == null) {
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
			
			this.rref = rref.equals(this) ? this : rref;
		}
		return rref;
	}
	
	/**
	 * Calculates the rank of this Matrix.
	 * Equivalent to calling {@code rank(false)}
	 * 
	 * @return the rank
	 */
	public int rank() {
		return rank(false);
	}
	
	/**
	 * Calculates the rank of this Matrix.
	 * If this Matrix is flagged as being augmented, the last column will be ignored.
	 * 
	 * @param augmented true if this Matrix should be treated as an augmented matrix, false otherwise
	 * @return the rank
	 */
	public int rank(boolean augmented) {
		if(!augmented && rank != -1)
			return rank;
		Matrix rref = rref();
		int rank = 0, i = 0, j = 0;
		while(i < rref.contents.length) {
			while(j < rref.contents[0].length - (augmented ? 1 : 0)) {
				if(rref.contents[i][j] == 1) {
					rank++;
					j++;
					break;
				}
				j++;
			}
			i++;
		}
		
		if(!augmented) //only cache rank if not flagged as augmented
			this.rank = rank;
		return rank;
	}
	
	/**
	 * Assumes that this instance is an augmented matrix which represents a system of linear equations.
	 * Calculates the solution for that system, if it exists.
	 * 
	 * @return the solution of the system if there is exactly one solution, null if there is no solution or infinitely many solutions
	 */
	public double[] findSolution() {
		if(rank(true) != contents[0].length - 1 || contents[0].length == 1) //call to rank forces generation of rref
			return null;
		for(int i = contents.length - 1; rref.contents[i][contents[0].length - 2] == 0; i--)
			if(rref.contents[i][contents[0].length - 1] == 1)
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
		return augment(augment).findSolution();
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
	 * Calculates the sum of this Matrix and another Matrix.
	 * The two Matrices must have the same dimensions.
	 * 
	 * @param m the Matrix to add
	 * @return the sum of the two Matrices
	 * @throws ArithmeticException if the Matrices have different dimensions
	 */
	public Matrix add(Matrix m) {
		if(this.contents.length != m.contents.length || this.contents[0].length != m.contents[0].length)
			throw new ArithmeticException("Cannot add or subtract Matricies of different dimensions.");
		
		Matrix sum = this.clone();
		for(int i = 0; i < sum.contents.length; i++)
			for(int j = 0; j < sum.contents[0].length; j++)
				sum.contents[i][j] += m.contents[i][j];
		return sum;
	}
	
	/**
	 * Calculates the difference of this Matrix and another Matrix.
	 * The two Matrices must have the same dimensions.
	 * 
	 * @param m the Matrix to subtract
	 * @return the difference of the two Matrices
	 * @throws ArithmeticException if the Matrices have different dimensions
	 */
	public Matrix subtract(Matrix m) {
		return this.add(m.scalarMultiply(-1));
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
				product.contents[i][j] = this.getRowVector(i).dotProduct(m.getColumnVector(j));
		return product;
	}
	
}
