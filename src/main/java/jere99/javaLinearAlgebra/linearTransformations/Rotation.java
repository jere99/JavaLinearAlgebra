package jere99.javaLinearAlgebra.linearTransformations;

import jere99.javaLinearAlgebra.foundation.LinearTransformation;
import jere99.javaLinearAlgebra.foundation.Matrix;

/**
 * Defines a linear transformation which rotates a vector in 2-space.
 * 
 * @author JeremiahDeGreeff
 */
public class Rotation extends LinearTransformation {
	
	/**
	 * Initializes a counterclockwise Rotation by a particular angle within 2-space.
	 * 
	 * @param theta the angle to rotate by (in radians)
	 */
	public Rotation(double theta) {
		super(new Matrix(new double[][] {{Math.cos(theta), -Math.sin(theta)}, {Math.sin(theta), Math.cos(theta)}}));
	}
	
}
