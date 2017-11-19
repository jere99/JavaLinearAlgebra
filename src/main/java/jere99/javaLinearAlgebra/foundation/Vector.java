package jere99.javaLinearAlgebra.foundation;

/**
 * Defines a vector and provides vector operations.
 * 
 * @author JeremiahDeGreeff
 */
public class Vector implements Cloneable {
	
	//================================================================================
	// Static Methods
	//================================================================================
	
	/**
	 * Generates a particular standard Vector in a m-space.
	 * 
	 * @param m the space in which the desired vector exists
	 * @param i the index of the standard vector in m-space
	 * @return the specified standard vector
	 */
	public static Vector getStandardVector(int m, int i) {
		double[] components = new double[m];
		components[i] = 1;
		return new Vector(components);
	}
	
	//================================================================================
	// Instance Variables
	//================================================================================
	
	/**
	 * The main contents of this vector.
	 * The number of components indicates the space in which this vector exists.
	 */
	private final double[] components;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * Initializes a Vector with initial components.
	 * 
	 * @param initialComponents the initial components for the Vector
	 * @throws IllegalArgumentException if {@code initialComponents} has length 0
	 */
	public Vector(double[] initialComponents) {
		if(initialComponents.length == 0)
			throw new IllegalArgumentException("The parameter initialComponents is invalid - it must have length of at least one.");
		
		components = initialComponents;
	}
	
	//================================================================================
	// Overrides
	//================================================================================
	
	/**
	 * Creates and returns a Vector with identical contents.
	 */
	@Override
	public Vector clone() {
		double[] copy = new double[components.length];
		for(int i = 0; i < components.length; i++)
			copy[i] = components[i];
		return new Vector(copy);
	}
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * <p>
	 * The other object is considered "equal" if either:
	 * <ul>
	 * <li>The other object references this instance. In other words {@code this == obj} has the value {@code true}.</li>
	 * <li>The other object is a {@code Vector} and has contents identical to those of this instance.</li>
	 * </ul>
	 * </p>
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(!(obj instanceof Vector))
			return false;
		Vector vector = (Vector) obj;
		if(this.components.length != vector.components.length)
			return false;
		for(int i = 0; i < components.length; i++)
			if(this.components[i] != vector.components[i])
				return false;
		return true;
	}
	
	/**
	 * Generates a string representation of the Vector.
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(components.length * 8); //allocates just under 6 characters per value
		result.append('<');
		for(int i = 0; i < components.length; i++)
			result.append((Math.rint(components[i]) == components[i] ? String.format("%d", (int)components[i]) : String.format("%.3f", components[i])) + (i == components.length - 1 ? ">\n" : ", "));
		return result.toString();
	}
	
	//================================================================================
	// Accessor Methods
	//================================================================================
	
	/**
	 * Retrieves a component of this Vector.
	 * 
	 * @param i the index of the component to retrieve
	 * @return the component at index {@code i}
	 * @throws IllegalArgumentException if {@code i} is negative or exceeds the valid indices of components in this Vector
	 */
	public double getComponent(int i) {
		return components[i];
	}
	
	/**
	 * Retrieves all the components of this Vector.
	 * 
	 * @return the components of this Vector
	 */
	public double[] getComponents() {
		return components;
	}
	
	/**
	 * Retrieves the number of components in this Vector.
	 * 
	 * @return the number of components in this Vector
	 */
	public int componentCount() {
		return components.length;
	}
	
	/**
	 * Calculates the length (norm) of this Vector
	 * 
	 * @return the length
	 */
	public double getLength() {
		return Math.sqrt(this.dotProduct(this));
	}
	
	/**
	 * Determines if this Vector is a zero vector, that is if all of its entries are zeros.
	 * 
	 * @return true if this Vector is a zero vector, false otherwise
	 */
	public boolean isZeroVector() {
		for(double component: components)
				if(component != 0)
					return false;
		return true;
	}
	
	/**
	 * Determines if this Vector is a scalar multiple of another Vector.
	 * This is equivalent to determining if it is either parallel or antiparallel to the other vector.
	 * 
	 * @param v the Vector to compare this Vector to
	 * @return true if they are scalar multiples of each other, false otherwise
	 */
	public boolean isScalarMultiple(Vector v) {
		if(this.components.length != v.components.length)
			return false;
		if(this.isZeroVector())
			return v.isZeroVector();
		
		double multiplier = 0;
		for(int i = 0; i < components.length; i++)
			if(this.components[i] != 0) {
				multiplier = v.components[i] / this.components[i];
				break;
			}
		for(int i = 0; i < components.length; i++)
			if(this.components[i] * multiplier != v.components[i])
				return false;
		return true;
	}
	
	/**
	 * Determines if this Vector is orthogonal to another Vector.
	 * 
	 * @param v the Vector to compare this Vector to
	 * @return true if they are orthogonal, false otherwise
	 */
	public boolean isOrthogonal(Vector v) {
		return this.dotProduct(v) == 0;
	}
	
	/**
	 * Determines if this Vector is a linear combination of an array of Vectors,
	 * that is if this Vector could be represented as the sum of scalar multiples of each of the provided Vectors.
	 * 
	 * @param vectors the Vectors to test
	 * @return true if this Vector is a linear combination of {@code vectors}, false otherwise
	 */
	public boolean isLinearCombination(Vector[] vectors) {
		return new Matrix(vectors).isConsistent(this);
	}
	
	/**
	 * Determines if this Vector is a distribution vector,
	 * that is if all of its components are positive or zero and add up to 1.
	 * 
	 * @return true if this Vector is a distribution vector, false otherwise
	 */
	public boolean isDistributionVector() {
		return isDistributionVector(false);
	}
	
	/**
	 * Determines if this Vector is a positive distribution vector,
	 * that is if all of its components are positive and add up to 1.
	 * 
	 * @return true if this Vector is a distribution vector, false otherwise
	 */
	public boolean isPositiveDistributionVector() {
		return isDistributionVector(true);
	}
	
	/**
	 * Determines if this Vector is a distribution vector,
	 * that is if all of its components are positive or zero and add up to 1.
	 * Can also determine if this Vector is a positive distribution vector,
	 * that is if all of its components are positive and add up to 1.
	 * 
	 * @param positive if true will test for a positive distribution vector, if false will test for any distribution vector
	 * @return true if this Vector is a distribution vector, false otherwise
	 */
	public boolean isDistributionVector(boolean positive) {
		double sum = 0;
		for(double component : components) {
			if(component < 0 || component == 0 && positive)
				return false;
			sum += component;
		}
		return sum == 1;
	}
	
	//================================================================================
	// Vector Operations
	//================================================================================
	
	/**
	 * Calculates the product of this Vector and a scalar quantity.
	 * 
	 * @param scalar the scalar to multiply by
	 * @return the product of the Vector and the scalar
	 */
	public Vector scalarMultiply(double scalar) {
		Vector product = this.clone();
		for(int i = 0; i < product.components.length; i++)
			product.components[i] *= scalar;
		return product;
	}
	
	/**
	 * Calculates the sum of this Vector and another Vector.
	 * The two Vectors must be in the same space.
	 * 
	 * @param v the Vector to add
	 * @return the sum of the two Vectors
	 * @throws ArithmeticException if the Vectors are in different spaces
	 */
	public Vector add(Vector v) {
		if(this.components.length != v.components.length)
			throw new ArithmeticException("Cannot add or subtract Vectors that are not in the same space.");
		
		Vector sum = this.clone();
		for(int i = 0; i < sum.components.length; i++)
			sum.components[i] += v.components[i];
		return sum;
	}
	
	/**
	 * Calculates the difference of this Vector and another Vector.
	 * The two Vectors must be in the same space.
	 * 
	 * @param v the Vector to subtract
	 * @return the difference of the two Vectors
	 * @throws ArithmeticException if the Vectors are in different spaces
	 */
	public Vector subtract(Vector v) {
		return this.add(v.scalarMultiply(-1));
	}
	
	/**
	 * Calculates the dot product this Vector and another Vector.
	 * The two Vectors must be in the same space.
	 * 
	 * @param v the Vector to perform the dot product with
	 * @return the dot product of the two Vectors
	 * @throws ArithmeticException if the Vectors are in different spaces
	 */
	public double dotProduct(Vector v) {
		if(this.components.length != v.components.length)
			throw new ArithmeticException("Cannot perform the dot product on Vectors that are not in the same space.");
		
		double sum = 0;
		for(int i = 0; i < components.length; i++)
			sum += this.components[i] * v.components[i];
		return sum;
	}
	
}
