package jere99.javaLinearAlgebra.foundation;

/**
 * Defines a linear transformation.
 * A transformation <em>T</em> from <html>&#x211D<sup><em>m</em></sup></hmtl></li> to <html>&#x211D<sup><em>n</em></sup></hmtl></li> is linear if and only if:
 * <ul>
 * <li>for all vectors <em>v</em> and <em>w</em> in <html>&#x211D<sup><em>m</em></sup></hmtl></li>
 * <blockquote><em>T</em>(<em>v</em> + <em>w</em>) = <em>T</em>(<em>v</em>) + <em>T</em>(<em>w</em>)</blockquote>
 * </li>
 * <li>for all vectors <em>v</em> in <html>&#x211D<sup><em>m</em></sup></hmtl> and all scalars <em>k</em>
 * <blockquote><em>T</em>(<em>kv</em>) = <em>kT</em>(<em>v</em>)</blockquote>
 * </li>
 * </ul>
 * This is equivalent to saying that <em>T</em> transforms a vector in <html>&#x211D<sup><em>m</em></sup></hmtl></li> into a vector in <html>&#x211D<sup><em>n</em></sup></hmtl></li> by multiplying it by an <em>n</em> x <em>m</em> matrix.
 * 
 * @author JeremiahDeGreeff
 * @see Vector
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
	 * Determines the VectorSpace in which a Vector must exist to be a valid input to this LinearTransformation.
	 * 
	 * @return the only valid VectorSpace for an input Vector to this LinearTransformation
	 */
	public VectorSpace getDomainSpace() {
		return VectorSpace.getRealSpace(transformationMatrix.columnCount());
	}
	
	/**
	 * Determines the VectorSpace in which any Vector that this LinearTransformation will output must exist.
	 * 
	 * @return the VectorSpace for an output Vector from this LinearTransformation
	 */
	public VectorSpace getTargetSpace() {
		return VectorSpace.getRealSpace(transformationMatrix.rowCount());
	}
	
	/**
	 * Calculates the kernel of this LinearTransformation,
	 * that is the subspace of the domain space of this LinearTransformation, <em>T</em>, which contains all the Vectors, <em>x</em>, in the domain space for which
	 * <blockquote><em>T</em>(<em>x</em>) = 0</blockquote>
	 * 
	 * @return the kernel of this LinearTransformation
	 */
	public VectorSpace getKernel() {
		return transformationMatrix.findKernel();
	}
	
	/**
	 * Calculates the image of this LinearTransformation,
	 * that is the subspace of the target space of this LinearTransformation, T, which contains all the vectors, v, in the target space for which there exists some vector, x, for which
	 * <blockquote><em>T</em>(<em>x</em>) = <em>v</em></blockquote>
	 * 
	 * @return the image of this LinearTransformation
	 */
	public VectorSpace getImage() {
		Vector[] columns = new Vector[transformationMatrix.columnCount()];
		for(int j = 0; j < columns.length; j++)
			columns[j] = transformationMatrix.getColumnVector(j);
		return new VectorSpace(new Basis(Vector.removeRedundant(columns)));
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
		if(v.componentCount() != getDomainSpace().dimension())
			throw new ArithmeticException("This linear transformation can only transform a Vector that is in " + getDomainSpace().dimension() + "-space");
		
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
