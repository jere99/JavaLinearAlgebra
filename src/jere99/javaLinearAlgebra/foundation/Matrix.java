package jere99.javaLinearAlgebra.foundation;

/**
 * Defines a matrix and provides matrix operations.
 * 
 * @author JeremiahDeGreeff
 */
public class Matrix implements Cloneable {
	
	//================================================================================
	// Static Methods
	//================================================================================
	
	/**
	 * Generates the n x n identity Matrix.
	 * 
	 * @param n the dimensions of the Matrix
	 * @return the n x n identity Matrix
	 */
	public static Matrix getIdentity(int n) {
		double[][] contents = new double[n][n];
		for(int k = 0; k < n; k++)
			contents[k][k] = 1;
		return new Matrix(contents);
	}
	
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
	
	/**
	 * Initializes a Matrix from its column vectors.
	 * 
	 * @param columns the Vectors which will form the columns of the initial contents of the Matrix
	 * @throws IllegalArgumentException if the Vectors in {@code vectors} are not all in the same space
	 */
	public Matrix(Vector[] columns) {
		for(Vector v : columns)
			if(v.componentCount() != columns[0].componentCount())
				throw new IllegalArgumentException("The parameter columns is invalid - all of its Vectors must be in the same space.");
		
		contents = new double[columns[0].componentCount()][columns.length];
		for(int i = 0; i < rowCount(); i++)
			for(int j = 0; j < columnCount(); j++)
				contents[i][j] = columns[j].getComponent(i);
	}
	
	//================================================================================
	// Overrides
	//================================================================================
	
	/**
	 * Creates and returns a Matrix with identical contents.
	 */
	@Override
	public Matrix clone() {
		double[][] copy = new double[rowCount()][columnCount()];
		for(int i = 0; i < rowCount(); i++)
			for(int j = 0; j < columnCount(); j++)
				copy[i][j] = contents[i][j];
		Matrix result = new Matrix(copy);
		result.rref = this.rref == null ? null : this.equals(this.rref) ? result : this.rref.clone();
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
		if(this.rowCount() != matrix.rowCount() || this.columnCount() != matrix.columnCount())
			return false;
		for(int i = 0; i < rowCount(); i++)
			for(int j = 0; j < columnCount(); j++)
				if(this.contents[i][j] != matrix.contents[i][j])
					return false;
		return true;
	}
	
	/**
	 * Generates a string representation of the Matrix.
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer((4 + 6 * columnCount()) * rowCount());
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
		if(i < 0 || i >= rowCount())
			throw new IllegalArgumentException("The paramter i was not in the valid range [0, " + (rowCount() - 1) + "].");
		if(j < 0 || j >= columnCount())
			throw new IllegalArgumentException("The paramter j was not in the valid range [0, " + (columnCount() - 1) + "].");
		
		clearCache();
		double old = contents[i][j];
		contents[i][j] = newValue;
		return old;
	}
	
	/**
	 * Sets a row Vector in this Matrix.
	 * 
	 * @param i the index of the row to set
	 * @param newRow the new row vector
	 * @return the old row at the same index
	 * @throws IllegalArgumentException
	 * 		if {@code i} is negative or exceeds the valid indices of rows in this Matrix
	 * 		or the number of components in {@code newRow} is not the same as the length of the rows in this Matrix
	 */
	public double[] setRowVector(int i, Vector newRow) {
		if(i < 0 || i >= rowCount())
			throw new IllegalArgumentException("The paramter i was not in the valid range [0, " + (rowCount() - 1) + "].");
		if(newRow.componentCount() != columnCount())
			throw new IllegalArgumentException("The parameter newRow has an invalid length - it must be length: " + columnCount() + ".");
		
		clearCache();
		double[] old = contents[i];
		contents[i] = newRow.getComponents();
		return old;
	}
	
	/**
	 * Sets a column Vector in this Matrix.
	 * 
	 * @param j the index of the column to set
	 * @param newColumn the new column Vector
	 * @return the old column at the same index
	 * @throws IllegalArgumentException
	 * 		if {@code j} is negative or exceeds the valid indices of columns in this Matrix
	 * 		or the number of components in {@code newColumn} is not the same as the length of the columns in this Matrix
	 */
	public double[] setColumnVector(int j, Vector newColumn) {
		if(j < 0 || j >= columnCount())
			throw new IllegalArgumentException("The paramter j was not in the valid range [0, " + (columnCount() - 1) + "].");
		if(newColumn.componentCount() != rowCount())
			throw new IllegalArgumentException("The parameter newColumn has an invalid length - it must be length: " + rowCount() + ".");
		
		clearCache();
		double[] old = new double[rowCount()];
		for(int i = 0; i < rowCount(); i++) {
			old[i] = contents[i][j];
			contents[i][j] = newColumn.getComponent(i);
		}
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
		if(i < 0 || i >= rowCount())
			throw new IllegalArgumentException("The paramter i was not in the valid range [0, " + (rowCount() - 1) + "].");
		
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
		if(j < 0 || j >= columnCount())
			throw new IllegalArgumentException("The paramter j was not in the valid range [0, " + (columnCount() - 1) + "].");
		
		double[] column = new double[rowCount()];
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
	 * Retrieves the number of rows in this Matrix.
	 * 
	 * @return the number of rows in this Matrix
	 */
	public int rowCount() {
		return contents.length;
	}
	
	/**
	 * Retrieves the number of columns in this Matrix.
	 * 
	 * @return the number of columns in this Matrix
	 */
	public int columnCount() {
		return contents[0].length;
	}
	
	/**
	 * Determines if this Matrix is a square matrix, that is if:
	 * <ul>
	 * <li>it has the same number of rows as it does columns</li>
	 * </ul>
	 * @return true if this Matrix is square, false otherwise
	 */
	public boolean isSquare() {
		return rowCount() == columnCount();
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
		if(rowCount() != columnCount())
			return false;
		for(int i = 0; i < rowCount(); i++)
			for(int j = 0; j < columnCount(); j++)
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
		if(rowCount() != columnCount())
			return false;
		for(int i = 0; i < rowCount(); i++)
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
		if(rowCount() != columnCount())
			return false;
		for(int i = 0; i < rowCount(); i++)
			for(int j = i + 1; j < columnCount(); j++)
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
		if(rowCount() != columnCount())
			return false;
		for(int i = 0; i < rowCount(); i++)
			for(int j = 0; j < columnCount(); j++)
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
		if(rank(true) == rowCount() || columnCount() == 1) //call to rank forces generation of rref
			return true;
		for(int i = rowCount() - 1; rref.contents[i][rref.columnCount() - 2] == 0; i--)
			if(rref.contents[i][rref.columnCount() - 1] == 1)
				return false;
		return true;
	}
	
	/**
	 * Assumes that this instance is a coefficient matrix which when augmented with the passed column Vector represents a system of linear equations.
	 * Determines whether or not this system is consistent.
	 * 
	 * @param augment the column Vector to turn this coefficient matrix into an augmented matrix
	 * @return true if the system is consistent, false otherwise
	 * @throws IllegalArgumentException if the number of components of {@code augment} does not equal the number or rows in this Matrix
	 */
	public boolean isConsistent(Vector augment) {
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
		if(i < 0 || i >= rowCount())
			throw new IllegalArgumentException("The paramter i was not in the valid range [0, " + (rowCount() - 1) + "].");
		
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
		if(iTarget < 0 || iTarget >= rowCount())
			throw new IllegalArgumentException("The paramter iTarget was not in the valid range [0, " + (rowCount() - 1) + "].");
		if(iSource < 0 || iSource >= rowCount())
			throw new IllegalArgumentException("The paramter iSource was not in the valid range [0, " + (rowCount() - 1) + "].");
		
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
		if(i1 < 0 || i1 >= rowCount())
			throw new IllegalArgumentException("The paramter i1 was not in the valid range [0, " + (rowCount() - 1) + "].");
		if(i2 < 0 || i2 >= rowCount())
			throw new IllegalArgumentException("The paramter i2 was not in the valid range [0, " + (rowCount() - 1) + "].");
		
		double[] temp = contents[i1];
		contents[i1] = contents[i2];
		contents[i2] = temp;
	}
	
	//================================================================================
	// Matrix Operations
	//================================================================================
	
	/**
	 * Calculates the sum of this Matrix and another Matrix.
	 * The two Matrices must have the same dimensions.
	 * 
	 * @param m the Matrix to add
	 * @return the sum of the two Matrices
	 * @throws ArithmeticException if the Matrices have different dimensions
	 */
	public Matrix add(Matrix m) {
		if(this.rowCount() != m.rowCount() || this.columnCount() != m.columnCount())
			throw new ArithmeticException("Cannot add or subtract Matricies of different dimensions.");
		
		Matrix sum = this.clone();
		for(int i = 0; i < sum.rowCount(); i++)
			for(int j = 0; j < sum.columnCount(); j++)
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
		return this.add(m.multiply(-1));
	}
	
	/**
	 * Calculates the product of this Matrix and a scalar quantity.
	 * 
	 * @param scalar the scalar to multiply by
	 * @return the product of the Matrix and the scalar
	 */
	public Matrix multiply(double scalar) {
		Matrix product = this.clone();
		for(int i = 0; i < product.rowCount(); i++)
			for(int j = 0; j < product.columnCount(); j++)
				product.contents[i][j] *= scalar;
		return product;
	}
	
	/**
	 * Calculates the result of multiplying this Matrix by a Vector.
	 * This is equivalent to multiplying this Matrix by another Matrix which has only one column.
	 * The number of columns in this Matrix must match the number of components in the Vector.
	 * The resulting Matrix will have dimensions n x 1, with n being the number of rows in this Matrix.
	 * 
	 * @param v the Vector to multiply by
	 * @return the product of this Matrix and the {@code v}
	 * @throws ArithmeticException if the number of columns in this Matrix does not match the number of components in {@code v}
	 */
	public Vector multiply(Vector v) {
		if(this.columnCount() != v.componentCount())
			throw new ArithmeticException("The number of columns in this Matrix must match the number of components in {@code v} to multiply them.");
		
		Vector product = new Vector(this.rowCount());
		for(int i = 0; i < product.componentCount(); i++)
			product.setComponent(i, this.getRowVector(i).dotProduct(v));
		return product;
	}
	
	/**
	 * Calculates the result of multiplying this Matrix by another Matrix.
	 * The number of columns in this Matrix must match the number of rows in the other Matrix.
	 * The resulting Matrix will have dimensions n x m, with n being the number of rows in this Matrix and m being the number of columns in the other Matrix.
	 * Note that this function is not commutative; in other words for Matrices m1 and m2
	 * <blockquote>
	 * m1.matrixMultiply(m2).equals(m2.matrixMultiply(m1))
	 * </blockquote>
	 * will not necessarily have a value of true.
	 * 
	 * @param m the Matrix to multiply by
	 * @return the product of the Matrices
	 * @throws ArithmeticException if the number of columns in this Matrix does not match the number of rows in {@code m}
	 */
	public Matrix multiply(Matrix m) {
		if(this.columnCount() != m.rowCount())
			throw new ArithmeticException("The number of columns in the first Matrix must match the number of rows in the second to multiply them.");
		
		Matrix product = new Matrix(this.rowCount(), m.columnCount());
		for(int j = 0; j < product.columnCount(); j++)
			product.setColumnVector(j, this.multiply(m.getColumnVector(j)));
		return product;
	}
	
	/**
	 * Creates a new Matrix object by augmenting this Matrix.
	 * The augment is added as an additional column Vector.
	 * Intended to be used to turn a coefficient Matrix into an augmented Matrix.
	 * 
	 * @param augment the column Vector to append to this Matrix
	 * @return a Matrix which is an augmented form of this Matrix
	 * @throws IllegalArgumentException if the number of components of {@code augment} does not equal the number or rows in this Matrix
	 */
	public Matrix augment(Vector augment) {
		if(this.rowCount() != augment.componentCount())
			throw new IllegalArgumentException("Can only augment a Matrix with a column Vector with which has the same number of components as the Matrix does rows.");
		double[][] augmentedContents = new double[rowCount()][columnCount() + 1];
		for(int i = 0; i < rowCount(); i++) {
			for(int j = 0; j < columnCount(); j++)
				augmentedContents[i][j] = contents[i][j];
			augmentedContents[i][columnCount()] = augment.getComponent(i);
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
			
			for(int i = 0, j = 0; i < rref.rowCount(); i++) {
				while(j < rref.columnCount() && rref.contents[i][j] == 0) {
					for(int iCheck = i + 1; iCheck < rref.rowCount(); iCheck++)
						if(rref.contents[iCheck][j] != 0) {
							rref.swapRows(i, iCheck);
							break;
						}
					if(rref.contents[i][j] == 0)
						j++;
				}
				if(j == rref.columnCount())
					break;
				rref.divideRow(i, rref.contents[i][j]);
				for(int iReduce = 0; iReduce < rref.rowCount(); iReduce++)
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
		Matrix rref = rref();
		int rank = 0, i = 0, j = 0;
		while(i < rref.rowCount()) {
			while(j < rref.columnCount() - (augmented ? 1 : 0)) {
				if(rref.contents[i][j] == 1) {
					rank++;
					j++;
					break;
				}
				j++;
			}
			i++;
		}
		return rank;
	}
	
	/**
	 * Assumes that this instance is an augmented matrix which represents a system of linear equations.
	 * Calculates the solution for that system, if it exists.
	 * 
	 * @return the solution of the system if there is exactly one solution, null if there is no solution or infinitely many solutions
	 */
	public double[] findSolution() {
		if(rank(true) != columnCount() - 1 || columnCount() == 1) //call to rank forces generation of rref
			return null;
		for(int i = rref.rowCount() - 1; rref.contents[i][rref.columnCount() - 2] == 0; i--)
			if(rref.contents[i][rref.columnCount() - 1] == 1)
				return null;
		double[] result = new double[rref.rowCount()];
		for(int i = 0; i < result.length; i++)
			result[i] = rref.contents[i][rref.columnCount() - 1];
		return result;
	}
	
	/**
	 * Assumes that this instance is a coefficient matrix which when augmented with the passed column Vector represents a system of linear equations.
	 * Calculates the solution for that system, if it exists.
	 * 
	 * @param augment the column Vector to turn this coefficient matrix into an augmented matrix
	 * @return the solution of the system if there is exactly one solution, null if there is no solution or infinitely many solutions
	 * @throws IllegalArgumentException if the number of components of {@code augment} does not equal the number or rows in this Matrix
	 */
	public double[] findSolution(Vector augment) {
		return augment(augment).findSolution();
	}
	
}
