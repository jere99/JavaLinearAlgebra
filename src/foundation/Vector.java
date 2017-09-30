package foundation;

/**
 * Defines a vector and provides vector operations.
 * 
 * @author JeremiahDeGreeff
 */
public class Vector {
	
	//================================================================================
	// Instance Variables
	//================================================================================
	
	/**
	 * The main contents of this vector.
	 * The number of components indicates the space in which this vector exists.
	 */
	private double[] components;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * Initializes an Vector in {@code n} space without any initial components.
	 * 
	 * @param n the space in which the vector will exist
	 */
	public Vector(int n) {
		components = new double[n];
	}
	
	/**
	 * Initializes an Vector with initial components.
	 * 
	 * @param initialComponents the initial components for the Vector
	 */
	public Vector(double[] initialComponents) {
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
			result.append((Math.rint(components[i]) == components[i] ? String.format("%d", (int)components[i]) : String.format("%.3f", components[i])) + (i == components.length - 1 ? ">" : ", "));
		return result.toString();
	}
	
	//================================================================================
	// Mutator Methods
	//================================================================================
	
	/**
	 * Sets a components in this Vector.
	 * 
	 * @param i the index of the row to set
	 * @param newComponent the new value to set
	 * @return the old component at index {@code i}
	 * @throws IllegalArgumentException if {@code i} is negative or exceeds the valid indices of components in this Vector
	 */
	public double setComponent(int i, double newComponent) {
		if(i < 0 || i >= components.length)
			throw new IllegalArgumentException("The paramter i was not in the valid range [0, " + (components.length - 1) + "].");
		double old = components[i];
		components[i] = newComponent;
		return old;
	}
	
	/**
	 * Sets all the components of this Vector.
	 * 
	 * @param newComponents the contents to set
	 * @return the old components
	 */
	public double[] setContents(double[] newComponents) {
		double[] old = components;
		components = newComponents;
		return old;
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
	 * Determines whether or not this Vector is parallel to another Vector.
	 * 
	 * @param v the Vector to compare this Vector to
	 * @return true if they are parallel, false otherwise
	 */
	public boolean isParallel(Vector v) {
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
	 * Determines whether or not this Vector is orthogonal to another Vector.
	 * 
	 * @param v the Vector to compare this Vector to
	 * @return true if they are orthogonal, false otherwise
	 */
	public boolean isOrthogonal(Vector v) {
		return this.dotProduct(v) == 0;
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
	 * @throws ArithmeticException if the Vectors have different numbers of components
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
