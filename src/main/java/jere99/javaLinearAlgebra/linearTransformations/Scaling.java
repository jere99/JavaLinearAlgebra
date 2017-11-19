package jere99.javaLinearAlgebra.linearTransformations;

import jere99.javaLinearAlgebra.foundation.Matrix;

/**
 * Defines a linear transformation which scales a vector by a constant factor.
 * 
 * @author JeremiahDeGreeff
 */
public class Scaling extends LinearTransformation {
	
	/**
	 * Initializes a Scaling by a particular constant factor within a particular vector space.
	 * 
	 * @param n the dimension of both the domain and target spaces
	 * @param k the constant factor to multiply by
	 */
	public Scaling(int n, double k) {
		super(Matrix.getIdentity(n).multiply(k));
	}
	
}
