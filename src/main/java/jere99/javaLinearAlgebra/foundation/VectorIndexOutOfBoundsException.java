package jere99.javaLinearAlgebra.foundation;

/**
 * Thrown by {@link Vector} methods to indicate that an index is either negative or exceeds the valid indices of components in the Vector.
 * 
 * @author JeremiahDeGreeff
 * @see Vector
 */
@SuppressWarnings("serial")
public class VectorIndexOutOfBoundsException extends IndexOutOfBoundsException {
	
	/**
     * Constructs a {@code VectorIndexOutOfBoundsException} with no detail message.
     */
    public VectorIndexOutOfBoundsException() {
        super();
    }

    /**
     * Constructs a {@code VectorIndexOutOfBoundsException} with the specified detail message.
     * 
     * @param s the detail message
     */
    public VectorIndexOutOfBoundsException(String s) {
        super(s);
    }
	
	/**
	 * Constructs a new VectorIndexOutOfBoundsException with an argument indicating the illegal index.
	 * 
	 * @param index the illegal index
	 */
    public VectorIndexOutOfBoundsException(int index) {
        super("Vector index out of range: " + index);
    }
	
}
