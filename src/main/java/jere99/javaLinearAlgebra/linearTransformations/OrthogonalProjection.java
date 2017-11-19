package jere99.javaLinearAlgebra.linearTransformations;

import jere99.javaLinearAlgebra.foundation.LinearTransformation;
import jere99.javaLinearAlgebra.foundation.Matrix;
import jere99.javaLinearAlgebra.foundation.Vector;

/**
 * Defines a linear transformation which projects a vector orthogonally onto a line.
 * 
 * @author JeremiahDeGreeff
 */
public class OrthogonalProjection extends LinearTransformation {
	
	/**
	 * Initializes an OrthonalProjection onto a particular line.
	 * 
	 * @param w a Vector which spans the line being project upon
	 */
	public OrthogonalProjection(Vector w) {
		super(constructProjectionMatrix(w));
	}
	
	/**
	 * Constructs the Matrix which projects a Vector orthogonally onto a particular line.
	 * 
	 * @param w a Vector which spans the line being project upon
	 * @return the Matrix which would represent this projection
	 */
	private static Matrix constructProjectionMatrix(Vector w) {
		double multiple = w.dotProduct(w);
		double[][] contents = new double[w.componentCount()][w.componentCount()];
		for(int i = 0; i < contents.length; i++)
			for(int j = 0; j < contents[0].length; j++)
				contents[i][j] = w.getComponent(i) * w.getComponent(j) / multiple;
		return new Matrix(contents);
	}
	
}
