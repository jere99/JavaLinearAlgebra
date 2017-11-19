package jere99.javaLinearAlgebra.linearTransformations;

import jere99.javaLinearAlgebra.foundation.LinearTransformation;
import jere99.javaLinearAlgebra.foundation.Matrix;
import jere99.javaLinearAlgebra.foundation.Vector;

/**
 * Defines a linear transformation which reflects a vector about a line.
 * 
 * @author JeremiahDeGreeff
 */
public class Reflection extends LinearTransformation {
	
	/**
	 * Initializes a Reflection about a particular line.
	 * 
	 * @param w a Vector which spans the line being reflected about
	 */
	public Reflection(Vector w) {
		super(constructReflectionMatrix(w));
	}
	
	/**
	 * Constructs the Matrix which reflects a Vector about a particular line.
	 * 
	 * @param w a Vector which spans the line being project upon
	 * @return the Matrix which would represent this projection
	 */
	private static Matrix constructReflectionMatrix(Vector w) {
		double multiple = w.dotProduct(w);
		double[][] contents = new double[w.componentCount()][w.componentCount()];
		for(int i = 0; i < contents.length; i++)
			for(int j = 0; j < contents[0].length; j++)
				contents[i][j] = 2 * w.getComponent(i) * w.getComponent(j) / multiple - (i == j ? 1 : 0);
		return new Matrix(contents);
	}
	
}
