
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
		String lineBreak = "|\t";
		for(int j = 0; j < contents[0].length; j++)
			lineBreak += "\t";
		lineBreak += "|\n";
		
		String result = lineBreak;
		for(double[] row : contents) {
			result += "|\t";
			for(double value : row)
				result += (value == Math.rint(value) ? (int)value : value + "") + "\t";
			result += "|\n" + lineBreak;
		}
		return result;
	}
	
}
