package jere99.javaLinearAlgebra.linearTransformations;

import jere99.javaLinearAlgebra.foundation.Matrix;
import jere99.javaLinearAlgebra.foundation.Vector;

/**
 * Defines a linear transformation.
 * A transformation, T, is linear from m-space to n-space if and only if:
 * <ul>
 * <li>it transforms a Vector in m-space into a Vector in n-space by multiplying it by an n x m Matrix</li>
 * <li>T(v + w) = T(v) + T(w) for all vectors v and w in m-space</li>
 * <li>T(kv) = kT(v) for all vectors v in m-space and all scalars k</li>
 * </ul>
 * 
 * @author JeremiahDeGreeff
 * @see jere99.javaLinearAlgebra.foundation.Matrix
 */
public class LinearTransformation {
	
	/**
	 * The transformation Matrix, A, of this linear transformation.
	 * A linear transformation is defined as the result of multiplying this Matrix by an input vector.
	 */
	private final Matrix transformationMatrix;
	
	/**
	 * Initializes a LinearTransformation with a specified transformation matrix.
	 * 
	 * @param transformationMatrix the transformation matrix for the new LinearTransformation
	 */
	public LinearTransformation(Matrix transformationMatrix) {
		this.transformationMatrix = transformationMatrix;
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
	
}
