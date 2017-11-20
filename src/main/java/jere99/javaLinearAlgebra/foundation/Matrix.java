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
	private final double[][] contents;
	
	/**
	 * Cached copy of reduced row echelon form so it only needs to be calculated once.
	 * Note that if this Matrix is in reduced row echelon form, this field could be self-referential.
	 */
	private Matrix rref = null;
	
	/**
	 * Cached copy of inverse so it only needs to be calculated once.
	 * Note that if this Matrix is its own inverse, this field could be self-referential.
	 */
	private Matrix inverse = null;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * Initializes a Matrix with initial contents.
	 * 
	 * @param initialContents the initial contents for the Matrix
	 * @throws IllegalArgumentException if any of the following is true:
	 * <ul>
	 * <li>{@code initialConents} has length zero,</li>
	 * <li>{@code initialConents[0]} has length zero,</li>
	 * <li>the rows of {@code initialContents} do not all have the same length</li>
	 * </ul>
	 */
	public Matrix(double[][] initialContents) {
		if(initialContents.length == 0)
			throw new IllegalArgumentException("The parameter initialContents is invalid - it must have at least one row.");
		if(initialContents[0].length == 0)
			throw new IllegalArgumentException("The parameter initialContents is invalid - it must have at least one column.");
		for(double[] row : initialContents)
			if(row.length != initialContents[0].length)
				throw new IllegalArgumentException("The parameter initialContents is invalid - all of its rows must have the same length.");
		
		contents = initialContents;
	}
	
	/**
	 * Initializes a Matrix from its column vectors.
	 * 
	 * @param columns the Vectors which will form the columns of the initial contents of the Matrix
	 * @throws IllegalArgumentException if any of the following is true:
	 * <ul>
	 * <li>{@code columns} has length 0</li>
	 * <li>the Vectors in {@code columns} are not all in the same space</li>
	 * </ul>
	 */
	public Matrix(Vector[] columns) {
		if(columns.length == 0)
			throw new IllegalArgumentException("The parameter columns is invalid - it must have length of at least one.");
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
		result.rref = this.rref == null ? null : this == this.rref ? result : this.rref.clone();
		result.inverse = this.inverse == null ? null : this == this.inverse ? result : this.inverse.clone();
		return result;
	}
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * <p>
	 * The other object is considered "equal" if any of the following is true:
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
	 * Generates a string representation of the Matrix with the default precision of 3 places after the point.
	 */
	@Override
	public String toString() {
		return toString(3);
	}
	
	/**
	 * Generates a string representation of the Matrix with a particular precision.
	 * 
	 * @param precision the number of places after the point to be represented
	 */
	public String toString(int precision) {
		int[] widths = new int[columnCount()];
		for(double[] row : contents)
			for(int j = 0; j < columnCount(); j++) {
				double entry = row[j];
				int width = (Math.rint(entry) == entry ? String.format("%,d", (int)entry) : String.format("%,." + precision + "f", entry)).length();
				if(widths[j] < width)
					widths[j] = width;
			}
		int rowWidth = 4;
		for(int width : widths)
			rowWidth += width + 1;
		
		StringBuffer result = new StringBuffer(rowCount() * rowWidth);
		for(double[] row : contents) {
			result.append("|");
			for(int j = 0; j < columnCount(); j++) {
				result.append(' ');
				double entry = row[j];
				result.append((Math.rint(entry) == entry ? String.format("%," + widths[j] + "d", (int)entry) : String.format("%," + widths[j] + "." + precision + "f", entry)));
			}
			result.append(" |\n");
		}
		return result.toString();
	}
	
	//================================================================================
	// Accessor Methods
	//================================================================================
	
	/**
	 * Retrieves an element of this Matrix.
	 * 
	 * @param i the row of the element
	 * @param j the column of the element
	 * @return the entry at the i-th row and the j-th column
	 * @throws MatrixIndexOutOfBoundsException if any of the following is true:
	 * <ul>
	 * <li>{@code i} is negative or exceeds the valid indices of rows in this Matrix</li>
	 * <li>{@code j} is negative or exceeds the valid indices of columns in this Matrix</li>
	 * </ul>
	 */
	public double getEntry(int i, int j) {
		if(i < 0 || i >= rowCount())
			throw new MatrixIndexOutOfBoundsException(i, true);
		if(j < 0 || j >= columnCount())
			throw new MatrixIndexOutOfBoundsException(j, false);
		return contents[i][j];
	}
	
	/**
	 * Retrieves the row vector of one of the rows in this Matrix.
	 * 
	 * @param i the index of the row to retrieve
	 * @return the row vector of row {@code i} in this Matrix
	 * @throws MatrixIndexOutOfBoundsException if {@code i} is negative or exceeds the valid indices of rows in this Matrix
	 */
	public Vector getRowVector(int i) {
		if(i < 0 || i >= rowCount())
			throw new MatrixIndexOutOfBoundsException(i, true);
		
		double[] row = new double[columnCount()];
		for(int j = 0; j < row.length; j++)
			row[j] = contents[i][j];
		return new Vector(row);
	}
	
	/**
	 * Retrieves the column vector of one of the column in this Matrix.
	 * 
	 * @param j the index of the column to retrieve
	 * @return the column vector of column {@code j} in this Matrix
	 * @throws MatrixIndexOutOfBoundsException if {@code j} is negative or exceeds the valid indices of columns in this Matrix
	 */
	public Vector getColumnVector(int j) {
		if(j < 0 || j >= columnCount())
			throw new MatrixIndexOutOfBoundsException(j, false);
		
		double[] column = new double[rowCount()];
		for(int i = 0; i < column.length; i++)
			column[i] = contents[i][j];
		return new Vector(column);
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
	 * Determines if this Matrix is a square matrix,
	 * that is if it has the same number of rows as it does columns.
	 * @return true if this Matrix is square, false otherwise
	 */
	public boolean isSquare() {
		return rowCount() == columnCount();
	}
	
	/**
	 * Determines if this Matrix is diagonal,
	 * that is if:
	 * <ul>
	 * <li>it is square</li>
	 * <li>all of its entries which are not on the main diagonal are zeros</li>
	 * </ul>
	 * 
	 * @return true if this Matrix is diagonal, false otherwise
	 */
	public boolean isDiagonal() {
		if(!isSquare())
			return false;
		for(int i = 0; i < rowCount(); i++)
			for(int j = 0; j < columnCount(); j++)
				if(i != j && contents[i][j] != 0)
					return false;
		return true;
	}
	
	/**
	 * Determines if this Matrix is upper triangular,
	 * that is if:
	 * <ul>
	 * <li>it is square</li>
	 * <li>all of its entries which are below the main diagonal are zeros</li>
	 * </ul>
	 * 
	 * @return true if this Matrix is upper triangular, false otherwise
	 */
	public boolean isUpperTriangular() {
		if(!isSquare())
			return false;
		for(int i = 0; i < rowCount(); i++)
			for(int j = 0; j < i; j++)
				if(contents[i][j] != 0)
					return false;
		return true;
	}
	
	/**
	 * Determines if this Matrix is lower triangular,
	 * that is if:
	 * <ul>
	 * <li>it is square</li>
	 * <li>all of its entries which are above the main diagonal are zeros</li>
	 * </ul>
	 * 
	 * @return true if this Matrix is lower triangular, false otherwise
	 */
	public boolean isLowerTriangular() {
		if(!isSquare())
			return false;
		for(int i = 0; i < rowCount(); i++)
			for(int j = i + 1; j < columnCount(); j++)
				if(contents[i][j] != 0)
					return false;
		return true;
	}
	
	/**
	 * Determines if this Matrix is an identity Matrix,
	 * that is if:
	 * <ul>
	 * <li>it is square</li>
	 * <li>it is diagonal</li>
	 * <li>all of its entries which are on the main diagonal are ones</li>
	 * </ul>
	 * 
	 * @return true if this Matrix is diagonal, false otherwise
	 */
	public boolean isIndentityMatrix() {
		if(!isSquare())
			return false;
		for(int i = 0; i < rowCount(); i++)
			for(int j = 0; j < columnCount(); j++)
				if(i != j && contents[i][j] != 0 || i == j && contents[i][j] != 1)
					return false;
		return true;
	}
	
	/**
	 * Determines if this Matrix is a zero matrix,
	 * that is if all of its entries are zeros.
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
	 * Determines if this Matrix is in reduced row echelon form.
	 * 
	 * @return true if this Matrix is in reduced row echelon form, false otherwise
	 */
	public boolean isRref() {
		return this.equals(this.rref());
	}
	
	/**
	 * Determines if this system is consistent.
	 * Assumes that this instance is an augmented matrix which represents a system of linear equations.
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
	 * Determines if this system is consistent.
	 * Assumes that this instance is a coefficient matrix which when augmented with the passed column Vector represents a system of linear equations.
	 * 
	 * @param augment the column Vector to turn this coefficient matrix into an augmented matrix
	 * @return true if the system is consistent, false otherwise
	 * @throws IllegalArgumentException if the number of components of {@code augment} does not match the number of rows in this Matrix
	 */
	public boolean isConsistent(Vector augment) {
		return this.augment(augment).isConsistent();
	}
	
	/**
	 * Determines if this Matrix is a transition matrix (stochastic matrix),
	 * that is if all of its columns are distribution vectors.
	 * 
	 * @return true if this Matrix is a transition matrix, false otherwise
	 * @see Vector#isDistributionVector()
	 */
	public boolean isTransitionMatrix() {
		return isTransitionMatrix(false);
	}
	
	/**
	 * Determines if this Matrix is a positive transition matrix,
	 * that is if all of its columns are positive distribution vectors.
	 * 
	 * @return true if this Matrix is a transition matrix, false otherwise
	 * @see Vector#isPositiveDistributionVector()
	 */
	public boolean isPositiveTransitionMatrix() {
		return isTransitionMatrix(true);
	}
	
	/**
	 * Determines if this Matrix is a transition matrix (stochastic matrix),
	 * that is if all of its columns are distribution vectors.
	 * Can also determine if this Matrix is a positive transition matrix,
	 * that is if all of its columns are positive distribution vectors.
	 * 
	 * @param positive if true will test for a positive transition matrix, if false will test for any transition matrix
	 * @return true if this Matrix is a transition matrix, false otherwise
	 * @see Vector#isDistributionVector(boolean)
	 */
	public boolean isTransitionMatrix(boolean positive) {
		for(int j = 0; j < columnCount(); j++)
			if(!getColumnVector(j).isDistributionVector(positive))
				return false;
		return true;
	}
	
	/**
	 * Determines if this Matrix is invertible.
	 * This is equivalent to determining if this Matrix is an n x n Matrix of rank n.
	 * 
	 * @return true if this Matrix is invertible, false otherwise.
	 */
	public boolean isInvertible() {
		return isSquare() && rank() == rowCount();
	}
	
	//================================================================================
	// Elementary Row Operations
	//================================================================================
	
	/**
	 * Divides a row in this Matrix by a non-zero scalar.
	 * 
	 * @param i the index of the row to set
	 * @param scalar the non-zero number to divide by
	 * @throws ArithmeticException if {@code scalar == 0}
	 * @throws MatrixIndexOutOfBoundsException if {@code i] is negative or exceeds the valid indices of rows in this Matrix
	 */
	private void divideRow(int i, double scalar) {
		if(scalar == 0)
			throw new ArithmeticException("Cannot divide a row by 0.");
		if(i < 0 || i >= rowCount())
			throw new MatrixIndexOutOfBoundsException(i, true);
		
		for(int j = 0; j < contents[i].length; j++)
			contents[i][j] /= scalar;
	}
	
	/**
	 * Subtracts a multiple of one row in this Matrix from another one.
	 * 
	 * @param iTarget the row which will be subtracted from
	 * @param multiple the number to multiply the source row by before subtracting
	 * @param iSource the row to subtract from the target row
	 * @throws MatrixIndexOutOfBoundsException if {@code iTarget} or {@code iSource} is negative or exceeds the valid indices of rows in this Matrix
	 */
	private void subtractRow(int iTarget, double multiple, int iSource) {
		if(iTarget < 0 || iTarget >= rowCount())
			throw new MatrixIndexOutOfBoundsException(iTarget, true);
		if(iSource < 0 || iSource >= rowCount())
			throw new MatrixIndexOutOfBoundsException(iSource, true);
		
		for(int j = 0; j < contents[iTarget].length; j++)
			contents[iTarget][j] -= contents[iSource][j] * multiple;
	}
	
	/**
	 * Swaps the contents of two rows in this Matrix.
	 * 
	 * @param i1 the first row to swap
	 * @param i2 the second row to swap
	 * @throws MatrixIndexOutOfBoundsException if {@code i1} or {@code i2} is negative or exceeds the valid indices of rows in this Matrix
	 */
	private void swapRows(int i1, int i2) {
		if(i1 < 0 || i1 >= rowCount())
			throw new MatrixIndexOutOfBoundsException(i1, true);
		if(i2 < 0 || i2 >= rowCount())
			throw new MatrixIndexOutOfBoundsException(i2, true);
		
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
	 * @throws ArithmeticException if {@code m} and this Matrix have different dimensions
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
	 * @throws ArithmeticException if {@code m} and this Matrix have different dimensions
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
	 * @throws ArithmeticException if the number of components in {@code v} does not match the number of columns in this Matrix
	 */
	public Vector multiply(Vector v) {
		if(this.columnCount() != v.componentCount())
			throw new ArithmeticException("The number of columns in this Matrix must match the number of components in {@code v} to multiply them.");
		
		double[] product = new double[rowCount()];
		for(int i = 0; i < rowCount(); i++)
			product[i] = getRowVector(i).dotProduct(v);
		return new Vector(product);
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
	 * @throws ArithmeticException if the number of rows in {@code m} does not match the number of columns in this Matrix
	 */
	public Matrix multiply(Matrix m) {
		if(this.columnCount() != m.rowCount())
			throw new ArithmeticException("The number of columns in the first Matrix must match the number of rows in the second to multiply them.");
		
		double[][] product = new double[this.rowCount()][m.columnCount()];
		for(int j = 0; j < product[0].length; j++) {
			Vector columnVector = this.multiply(m.getColumnVector(j));
			for(int i = 0; i < product.length; i++)
				product[i][j] = columnVector.getComponent(i);
		}
		return new Matrix(product);
	}
	
	/**
	 * Creates a new Matrix object by augmenting this Matrix.
	 * The augment is added as an additional column Vector.
	 * Intended to be used to turn a coefficient Matrix into an augmented Matrix.
	 * 
	 * @param augment the column Vector to append to this Matrix
	 * @return a Matrix which is an augmented form of this Matrix
	 * @throws IllegalArgumentException if the number of components of {@code augment} does not match the number of rows in this Matrix
	 */
	public Matrix augment(Vector augment) {
		if(this.rowCount() != augment.componentCount())
			throw new IllegalArgumentException("Can only augment a Matrix with a column Vector which has the same number of components as the Matrix does rows.");
		double[][] augmentedContents = new double[rowCount()][columnCount() + 1];
		for(int i = 0; i < rowCount(); i++) {
			for(int j = 0; j < columnCount(); j++)
				augmentedContents[i][j] = contents[i][j];
			augmentedContents[i][columnCount()] = augment.getComponent(i);
		}
		return new Matrix(augmentedContents);
	}
	
	/**
	 * Uses Gauss-Jordan elimination to calculate the reduced row echelon form of this Matrix.
	 * The result is cached for future access.
	 * 
	 * @return the reduced row echelon form of this Matrix
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
	 * @return the rank of this Matrix
	 */
	public int rank() {
		return rank(false);
	}
	
	/**
	 * Calculates the rank of this Matrix.
	 * If this Matrix is flagged as being augmented, the last column will be ignored.
	 * 
	 * @param augmented true if this Matrix should be treated as an augmented matrix, false otherwise
	 * @return the rank of this Matrix
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
	 * Calculates the inverse of this Matrix, if it exists.
	 * The result is cached for future access.
	 * 
	 * @return the inverse of this Matrix if it exists, {@code null} otherwise
	 */
	public Matrix inverse() {
		if(inverse == null) {
			if(!isSquare())
				return null;
			Matrix appended = this.append(getIdentity(rowCount())).rref();
			Matrix left = appended.splice(0, this.columnCount());
			if(!left.isIndentityMatrix())
				return null;
			Matrix inverse = appended.splice(this.columnCount());
			this.inverse = inverse.equals(this) ? this : inverse;
		}
		return inverse;
	}
	
	/**
	 * Calculates the solution for that system, if it exists.
	 * Assumes that this instance is an augmented matrix which represents a system of linear equations.
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
	 * Calculates the solution for that system, if it exists.
	 * Assumes that this instance is a coefficient matrix which when augmented with the passed column Vector represents a system of linear equations.
	 * 
	 * @param augment the column Vector to turn this coefficient matrix into an augmented matrix
	 * @return the solution of the system if there is exactly one solution, null if there is no solution or infinitely many solutions
	 * @throws IllegalArgumentException if the number of components of {@code augment} does not match the number of rows in this Matrix
	 */
	public double[] findSolution(Vector augment) {
		return this.augment(augment).findSolution();
	}
	
	/**
	 * Appends another Matrix as additional columns to the right of this Matrix.
	 * The other Matrix must have the same number of rows as this Matrix.
	 * 
	 * @param m the Matrix to append
	 * @return a new Matrix which is the result of appending {@code m} to this Matrix
	 * @throws IllegalArgumentException if {@code m} does not have the same number of rows as this Matrix
	 */
	public Matrix append(Matrix m) {
		if(this.rowCount() != m.rowCount())
			throw new IllegalArgumentException("Can only append a Matrix to another Matrix if they have the same number of rows.");
		
		double[][] newContents = new double[rowCount()][this.columnCount() + m.columnCount()];
		for(int i = 0; i < newContents.length; i++)
			for(int j = 0; j < newContents[0].length; j++)
				newContents[i][j] = (j < this.columnCount() ? this.contents[i][j] : m.contents[i][j - this.columnCount()]);
		return new Matrix(newContents);
	}
	
	/**
	 * Splices a portion of the columns of this Matrix to form a new Matrix.
	 * Specifically, this portion is from the specified column to the last column of this Matrix.
	 * 
	 * @param startColumn the column before which to begin the splice
	 * @return the new Matrix formed as a result of the splice
	 * @throws MatrixIndexOutOfBoundsException if {@code startColumn} is negative or exceeds the valid indices of columns in this Matrix
	 */
	public Matrix splice(int startColumn) {
		return splice(startColumn, columnCount());
	}
	
	/**
	 * Splices a portion of the columns of this Matrix to form a new Matrix.
	 * Specifically, this portion is from the first specified column to the column before the second specified column.
	 * 
	 * @param startColumn the column before which to begin the splice
	 * @param endColumn the column before which to end the splice
	 * @return the new Matrix formed as a result of the splice
	 * @throws MatrixIndexOutOfBoundsException if any of the following is true:
	 * <ul>
	 * <li>{@code startColumn} is negative or exceeds the valid indices of columns in this Matrix</li>
	 * <li>{@code endColumn} is negative or exceeds the valid indices of columns in this Matrix</li>
	 * <li>{@code startColumn} is not less than {@code endColumn}</li>
	 * </ul>
	 */
	public Matrix splice(int startColumn, int endColumn) {
		if(startColumn < 0 || startColumn >= columnCount())
			throw new MatrixIndexOutOfBoundsException(startColumn, false);
		if(endColumn < 0 || endColumn >= columnCount())
			throw new MatrixIndexOutOfBoundsException(endColumn, false);
		if(startColumn >= endColumn)
			throw new MatrixIndexOutOfBoundsException("The startColumn must be less than the endColumn.");
		
		double[][] newContents = new double[rowCount()][endColumn - startColumn];
		for(int i = 0; i < newContents.length; i++)
			for(int j = 0; j < newContents[0].length; j++)
				newContents[i][j] = this.contents[i][j + startColumn];
		return new Matrix(newContents);
	}
	
}
