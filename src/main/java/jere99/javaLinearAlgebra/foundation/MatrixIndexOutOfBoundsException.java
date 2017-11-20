package jere99.javaLinearAlgebra.foundation;

/**
 * Thrown by {@link Matrix} methods to indicate that an index is either negative or exceeds the valid indices of entries in the Matrix.
 * The invalid index could be in either the rows or the columns.
 * 
 * @author JeremiahDeGreeff
 * @see Matrix
 */
@SuppressWarnings("serial")
public class MatrixIndexOutOfBoundsException extends IndexOutOfBoundsException {
	
	/**
     * Constructs a {@code MatrixIndexOutOfBoundsException} with no detail message.
     */
    public MatrixIndexOutOfBoundsException() {
        super();
    }

    /**
     * Constructs a {@code MatrixIndexOutOfBoundsException} with the specified detail message.
     * 
     * @param s the detail message
     */
    public MatrixIndexOutOfBoundsException(String s) {
        super(s);
    }
	
	/**
	 * Constructs a new {@code MatrixIndexOutOfBoundsException} with arguments indicating the illegal index and whether it references a row or a column.
	 * 
	 * @param index the illegal index
	 * @param isRow true if a row index, false if a column index
	 */
    public MatrixIndexOutOfBoundsException(int index, boolean isRow) {
        super("Matrix " + (isRow ? "row" : "column") + " index out of range: " + index);
    }
	
}
