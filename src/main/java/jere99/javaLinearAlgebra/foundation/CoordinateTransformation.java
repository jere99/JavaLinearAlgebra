package jere99.javaLinearAlgebra.foundation;

/**
 * Defines a linear transformation which transforms Vectors between Bases.
 * 
 * @author JeremiahDeGreeff
 * @see Matrix
 * @see Vector
 */
public class CoordinateTransformation extends LinearTransformation {
	
	
	/**
	 * Initializes a coordinate transformation with specified input and output Bases.
	 * 
	 * @param from the input Basis
	 * @param to the output Basis
	 */
	private CoordinateTransformation(Basis from, Basis to) {
		//TODO write CoordinateTransformation class & rewrite all toBasis() methods
		
		super(null, from, to);
	}
	
}
