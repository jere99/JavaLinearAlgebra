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
 * @see Matrix
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
	 * The Basis used to define the domain space of this LinearTransformation.
	 */
	private final Basis domainBasis;
	
	/**
	 * The Basis used to define the target space of this LinearTransformation.
	 */
	private final Basis targetBasis;
	
	/**
	 * Cached copy of inverse so it only needs to be calculated once.
	 */
	private LinearTransformation inverse;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * Initializes a LinearTransformation with a specified transformation matrix.
	 * Uses the standard Bases for both the target space and the domain space.
	 * 
	 * @param transformationMatrix the transformation matrix for the new LinearTransformation
	 */
	public LinearTransformation(Matrix transformationMatrix) {
		this(transformationMatrix, null, null);
	}
	
	/**
	 * Initializes a LinearTransformation with a specified transformation matrix and common Basis for both the domain space and the target space.
	 * 
	 * @param transformationMatrix the transformation matrix for the new LinearTransformation
	 * @param basis the Basis used to define both the domain space and the target space of the new LinearTransformation, a value of {@code null} will be interpreted as the standard basis of each of the domain space and the target space
	 * @throws IllegalArgumentException if any of the following is true:
	 * <ul>
	 * <li>the number of columns in {@code transformationMatrix} is not the same as the dimension of the space defined by {@code basis}</li>
	 * <li>the number of rows in {@code transformationMatrix} is not the same as the dimension of the space defined by {@code basis}</li>
	 * <li>{@code basis} does not span the entire domain space</li>
	 * <li>{@code basis} does not span the entire target space</li>
	 * </ul>
	 */
	public LinearTransformation(Matrix transformationMatrix, Basis basis) {
		this(transformationMatrix, basis, basis);
	}
	
	/**
	 * Initializes a LinearTransformation with a specified transformation matrix, domain space basis, and target space basis.
	 * 
	 * @param transformationMatrix the transformation matrix for the new LinearTransformation
	 * @param domainBasis the Basis used to define the domain space of the new LinearTransformation, a value of {@code null} will be interpreted as the standard basis of the domain space
	 * @param domainBasis the Basis used to define the target space of the new LinearTransformation, a value of {@code null} will be interpreted as the standard basis of the target space
	 * @throws IllegalArgumentException if any of the following is true:
	 * <ul>
	 * <li>the number of columns in {@code transformationMatrix} is not the same as the dimension of the space defined by {@code domainBasis}</li>
	 * <li>the number of rows in {@code transformationMatrix} is not the same as the dimension of the space defined by {@code targetBasis}</li>
	 * <li>{@code domainBasis} does not span the entire domain space</li>
	 * <li>{@code targetBasis} does not span the entire target space</li>
	 * </ul>
	 */
	public LinearTransformation(Matrix transformationMatrix, Basis domainBasis, Basis targetBasis) {
		if(domainBasis != null && transformationMatrix.columnCount() != domainBasis.inSpace())
			throw new IllegalArgumentException("The number of columns of the parameter transformationMatrix must match the dimension of the space in which the parameter domainBasis exists.");
		if(targetBasis != null && transformationMatrix.rowCount() != targetBasis.inSpace())
			throw new IllegalArgumentException("The number of rows of the parameter transformationMatrix must match the dimension of the space in which the parameter targetBasis exists.");
		if(domainBasis != null && !domainBasis.spansFullSpace())
			throw new IllegalArgumentException("The parameter domainBasis must span the entire domain space.");
		if(targetBasis != null && !targetBasis.spansFullSpace())
			throw new IllegalArgumentException("The parameter targetBasis must span the entire target space.");
		
		this.transformationMatrix = transformationMatrix;
		this.domainBasis = domainBasis == null ? Basis.standardBasis(transformationMatrix.columnCount()) : domainBasis;
		this.targetBasis = targetBasis == null ? Basis.standardBasis(transformationMatrix.rowCount()) : targetBasis;
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
		return new VectorSpace(domainBasis);
	}
	
	/**
	 * Determines the VectorSpace in which any Vector that this LinearTransformation will output must exist.
	 * 
	 * @return the VectorSpace for an output Vector from this LinearTransformation
	 */
	public VectorSpace getTargetSpace() {
		return new VectorSpace(targetBasis);
	}
	
	/**
	 * Calculates the kernel of this LinearTransformation,
	 * that is the subspace of the domain space of this LinearTransformation, <em>T</em>, which contains all the Vectors, <em>x</em>, in the domain space for which
	 * <blockquote><em>T</em>(<em>x</em>) = 0</blockquote>
	 * 
	 * @return the kernel of this LinearTransformation
	 */
	public VectorSpace getKernel() {
		return transformationMatrix.findKernel(domainBasis);
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
	 * Determines if this LinearTransformation is an isomorphism,
	 * that is if it is invertible.
	 * 
	 * @return true if this LinearTransformation is invertible, false otherwise.
	 */
	public boolean isIsomorphism() {
		return transformationMatrix.isInvertible();
	}
	
	/**
	 * Calculates the inverse of this LinearTransformation, if it exists.
	 * The result is cached for future access.
	 * 
	 * @return the inverse of this LinearTransformation if it exists, {@code null} if it does not exist
	 */
	public LinearTransformation getInverse() {
		if(inverse == null) {
			Matrix transformationMatrixInverse = transformationMatrix.inverse();
			if(transformationMatrixInverse != null)
				inverse = new LinearTransformation(transformationMatrixInverse, targetBasis, domainBasis);
		}
		return inverse;
	}
	
	//================================================================================
	// Linear Transformation Operations
	//================================================================================
	
	/**
	 * Applies this linear transformation to a vector, that is multiplies a Vector by this LinearTransformation's transformation matrix.
	 * This operation is not defined if the space which the vector is in does not correspond to the number of columns in the transformation matrix.
	 * 
	 * @param v the input vector
	 * @return a Vector which is the result of the transformation.
	 * @throws ArithmeticException if the input vector is not in the correct space
	 */
	public Vector transform(Vector v) {
		if(v.componentCount() != getDomainSpace().dimension())
			throw new ArithmeticException("This linear transformation can only transform a Vector that is in " + getDomainSpace().dimension() + "-space");
		
		return transformationMatrix.multiply(v.toBasis(domainBasis), targetBasis);
	}
	
	/**
	 * Generates a new LinearTransformation which is similar to this LinearTransformation but is defined in terms of a different basis.
	 * Only supported for LinearTransformations have the same domain basis and target basis.
	 * 
	 * @param newBasis the basis to change to
	 * @return a new LinearTransformation which is equivalent to this LinearTransformation but defined in terms of {@code newBasis}
	 * @throws IllegalStateException if the domainBasis and the targetBasis of this LinearTransformation are not the same
	 * @throws IllegalArgumentException if the current basis and {@code newBasis} do not define the same space
	 */
	public LinearTransformation toBasis(Basis newBasis) {
		if(!domainBasis.equals(targetBasis))
			throw new UnsupportedOperationException("Cannot change the basis of a Linear Tranformation whose domain basis and target basis are not the same.");
		if(domainBasis.inSpace() != newBasis.inSpace() || !newBasis.spansFullSpace())
			throw new IllegalArgumentException("The new basis must define the same subspace as the current basis.");
		
		return domainBasis.equals(newBasis) ? this : new LinearTransformation(newBasis.asMatrix().inverse().multiply(domainBasis.asMatrix()).multiply(transformationMatrix).multiply(domainBasis.asMatrix().inverse()).multiply(newBasis.asMatrix()), newBasis);
	}
	
	/**
	 * Generates a new LinearTransformation which is similar to this LinearTransformation but defined in terms of the standard basis of the domain space of this LinearTransformation.
	 * 
	 * @return a new LinearTransformation which is similar to this LinearTransformation but defined in terms of the standard basis of the domain space of this LinearTransformation
	 */
	public LinearTransformation toStandardBasis() {
		return toBasis(Basis.standardBasis(domainBasis.inSpace()));
	}
	
}
