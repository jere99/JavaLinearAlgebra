package jere99.javaLinearAlgebra.linearTransformations;

import jere99.javaLinearAlgebra.foundation.LinearTransformation;
import jere99.javaLinearAlgebra.foundation.Matrix;

/**
 * Defines a linear transformation which shears in 2-space in either the horizontal or the vertical direction.
 * 
 * @author JeremiahDeGreeff
 */
public class Shear extends LinearTransformation {
	
	/**
	 * Initializes a Shear by a scalar in either the horizontal or the vertical direction within 2-space.
	 * 
	 * @param horizontal true if this shear should be horizontal, false if it should be vertical
	 * @param k the scalar to shear by
	 */
	public Shear(boolean horizontal, double k) {
		super(new Matrix(new double[][] {{1, horizontal ? k : 0}, {horizontal ? 0 : k, 1}}));
	}
	
}
