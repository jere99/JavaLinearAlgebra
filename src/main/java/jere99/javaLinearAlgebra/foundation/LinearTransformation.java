package jere99.javaLinearAlgebra.foundation;

/**
 * Defines a linear transformation.
 * A transformation, T, is linear from m-space to n-space if and only if:
 * <ul>
 * <li>T(v + w) = T(v) + T(w) for all vectors v and w in m-space</li>
 * <li>T(kv) = kT(v) for all vectors v in m-space and all scalars k</li>
 * </ul>
 * This is equivalent to saying that T transforms a vector in m-space into a vector in n-space by multiplying it by an n x m matrix.
 * 
 * @author JeremiahDeGreeff
 */
public class LinearTransformation {
	
	//================================================================================
	// Instance Variables
	//================================================================================
	
	/**
	 * The transformation Matrix, A, of this linear transformation.
	 * A linear transformation is defined as the result of multiplying this Matrix by an input vector.
	 */
	private final Matrix transformationMatrix;
	
	/**
	 * Cached copy of inverse so it only needs to be calculated once.
	 */
	private LinearTransformation inverse;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * Initializes a LinearTransformation with a specified transformation matrix.
	 * 
	 * @param transformationMatrix the transformation matrix for the new LinearTransformation
	 */
	public LinearTransformation(Matrix transformationMatrix) {
		this.transformationMatrix = transformationMatrix;
	}
	
	//================================================================================
	// Accessor Methods
	//================================================================================
	
	/**
	 * Retrieves the Matrix which represents this LinearTransformation.
	 * 
	 * @return the Matrix which represents this LinearTransformation.
	 */
	public Matrix getTransformationMatrix() {
		return transformationMatrix;
	}
	
	/**
	 * Determines the vector space in which a Vector must exist to be a valid input to this LinearTransformation.
	 * 
	 * @return the only valid vector space for an input Vector to this LinearTransformation
	 */
	public int getInputSpace() {
		return transformationMatrix.columnCount();
	}
	
	/**
	 * Determines the vector space in which any Vector that this LinearTransformation will output must exist.
	 * 
	 * @return the vector space for an output Vector from this LinearTransformation
	 */
	public int getOutputSpace() {
		return transformationMatrix.rowCount();
	}
	
	/**
	 * Determines if this LinearTransformation is invertible.
	 * 
	 * @return true if this LinearTransformation is invertible, false otherwise.
	 */
	public boolean isInvertible() {
		return transformationMatrix.isInvertible();
	}
	
	//================================================================================
	// Linear Transformation Operations
	//================================================================================
	
	/**
	 * Applies this linear transformation to a vector, that is multiplies a vector by this LinearTransformation's transformation matrix.
	 * This operation is not defined if the space which the vector is in does not correspond to the number of columns in the transformation matrix.
	 * 
	 * @param v the input vector
	 * @return a vector which is the result of the transformation.
	 * @throws ArithmeticException
	 */
	public Vector transform(Vector v) {
		if(v.componentCount() != this.getInputSpace())
			throw new ArithmeticException("This linear transformation can only transform a Vector that is in " + this.getInputSpace() + "-space");
		return transformationMatrix.multiply(v);
	}
	
	/**
	 * Calculates the inverse of this LinearTransformation, if it exists.
	 * The result is cached for future access.
	 * 
	 * @return the inverse of this LinearTransformation if it exists, {@code null} otherwise
	 */
	public LinearTransformation getInverse() {
		if(inverse == null)
			inverse = new LinearTransformation(transformationMatrix.inverse());
		return inverse;
	}
	
}
