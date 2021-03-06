package jere99.javaLinearAlgebra.foundation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines a vector and provides vector operations.
 * 
 * @author JeremiahDeGreeff
 */
public class Vector implements Cloneable {
	
	//================================================================================
	// Static Variables
	//================================================================================
	
	/**
	 * Cached references to zero vectors keyed by dimension.
	 */
	private static Map<Integer, Vector> zeroVectors = new HashMap<Integer, Vector>();
	/**
	 * Cached references to standard vectors keyed by dimension and index.
	 */
	private static Map<Integer, Map<Integer, Vector>> standardVectors = new HashMap<Integer, Map<Integer, Vector>>();
	
	//================================================================================
	// Static Methods
	//================================================================================
	
	/**
	 * Returns a reference to the zero vector in <html>&#x211D<sup><em>n</em></sup></hmtl>.
	 * 
	 * @param n the dimension of the space in which the desired zero vector exists
	 * @return the specified zero vector
	 * @throws IllegalArgumentException if {@code n} is negative
	 */
	public static Vector getZeroVector(int n) {
		if(n < 0)
			throw new IllegalArgumentException("The parameter n must not be negative.");
		if(!zeroVectors.containsKey(n))
			zeroVectors.put(n, new Vector(new double[n]));
		return zeroVectors.get(n);
	}
	
	/**
	 * Returns a reference to a particular Vector from the standard basis of <html>&#x211D<sup><em>n</em></sup></hmtl>.
	 * 
	 * @param n the dimension of the space in which the desired Vector exists
	 * @param i the index of the standard vector in <html>&#x211D<sup><em>n</em></sup></hmtl> [1, n] (not zero indexed
	 * @return the specified standard vector
	 * @throws IllegalArgumentException if any of the following is true:
	 * <ul>
	 * <li>{@code n} is negative</li>
	 * <li>{@code i} is not positive</li>
	 * <li>{@code i} exceeds {@code n}</li>
	 * </ul>
	 */
	public static Vector getStandardVector(int n, int i) {
		if(n < 0)
			throw new IllegalArgumentException("The parameter n must not be negative.");
		if(i < 1)
			throw new IllegalArgumentException("The parameter i must be positive.");
		if(i > n)
			throw new IllegalArgumentException("The parameter i cannot exceed the parameter n.");
		if(!standardVectors.containsKey(n))
			standardVectors.put(n, new HashMap<Integer, Vector>());
		if(!standardVectors.get(n).containsKey(i)) {
			double[] components = new double[n];
			components[i - 1] = 1;
			standardVectors.get(n).put(i, new Vector(components, null));
		}
		return standardVectors.get(n).get(i);
	}
	
	/**
	 * Determines if an array of Vectors are linearly independent,
	 * that is that none of the Vectors can be expressed as a linear combination of the other Vectors.
	 * Takes into account the potential for differences between the bases which determine the coordinates of the Vectors.
	 * 
	 * @param vectors the Vectors to test
	 * @return true if the vectors are linearly independent, false otherwise
	 * @throws IllegalArgumentException if any of the following is true:
	 * <ul>
	 * <li>{@code vectors} does not contain any elements</li>
	 * <li>the Vectors in {@code vectors} are not all in the same space</li>
	 * </ul>
	 */
	public static boolean areLinearlyIndependent(Vector[] vectors) {
		if(vectors.length == 0)
			throw new IllegalArgumentException("The array of vectors must include at least one element.");
		for(Vector v : vectors)
			if(v.componentCount() != vectors[0].componentCount())
				throw new IllegalArgumentException("All the vectors must be in the same space.");
		
		Vector[] converted = new Vector[vectors.length];
		for(int i = 0; i < converted.length; i++)
			converted[i] = vectors[i].toStandardBasis();
		return new Matrix(converted).nullity() == 0;
	}
	
	/**
	 * Creates an array of Vectors which consists of those which are passed with any redundant Vectors removed.
	 * In other words it creates an array of Vectors which form the basis of the subspace spanned by all of the passed Vectors.
	 * Takes into account the potential for differences between the bases which determine the coordinates of the Vectors.
	 * 
	 * @param vectors the Vectors to test - must all be in the same space
	 * @return a new array consisting of the Vectors in {@code vectors} which are not redundant
	 * @throws IllegalArgumentException if the Vectors in {@code vectors} are not all in the same space
	 */
	public static Vector[] removeRedundant(Vector[] vectors) {
		if(vectors.length != 0) {
			for(Vector v : vectors)
				if(v.componentCount() != vectors[0].componentCount())
					throw new IllegalArgumentException("All the vectors must be in the same space.");
		}
		
		List<Vector> result = new ArrayList<Vector>();
		int i = 0;
		while(i < vectors.length) {
			if(!vectors[i].isZeroVector() && !vectors[i].isLinearCombinationOf(result.toArray(new Vector[result.size()])))
				result.add(vectors[i]);
			i++;
		}
		return result.toArray(new Vector[result.size()]);
	}
	
	//================================================================================
	// Instance Variables
	//================================================================================
	
	/**
	 * The main contents of this Vector.
	 * The length of the array indicates the space in which this vector exists.
	 */
	private final double[] components;
	
	/**
	 * The basis used to determine the coordinates of this Vector.
	 */
	private final Basis basis;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * Initializes a Vector with specified components defined in terms of the standard Basis of the space in which the Vector will exist.
	 * 
	 * @param components the components for the Vector
	 * @throws IllegalArgumentException if {@code components} has length 0
	 */
	public Vector(double[] components) {
		this(components, Basis.standardBasis(components.length));
	}
	
	/**
	 * Initializes a Vector with specified components in terms of a specified Basis.
	 * 
	 * @param components the components for the Vector
	 * @param basis the Basis used to determine the coordinates of this Vector - a value of {@code null} indicates that the Vector is used to define a standard Basis
	 * @throws IllegalArgumentException if any of the following is the case:
	 * <ul>
	 * <li>{@code components} has length 0</li>
	 * <li>the length of {@code components} is not the same as the dimension of the space in which {@code basis} exists</li>
	 * </ul>
	 */
	public Vector(double[] components, Basis basis) {
		if(components.length == 0)
			throw new IllegalArgumentException("The parameter components must have length of at least one.");
		if(basis != null && components.length != basis.inSpace())
			throw new IllegalArgumentException("The length of the parameter components must match the dimension of the space in which the parameter basis exists.");
		
		this.components = Arrays.copyOf(components, components.length);
		this.basis = basis;
	}
	
	//================================================================================
	// Overrides
	//================================================================================
	
	/**
	 * Creates and returns a Vector with identical contents.
	 */
	@Override
	public Vector clone() {
		return new Vector(Arrays.copyOf(components, components.length), basis);
	}
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * <p>
	 * The other object is considered "equal" if either:
	 * <ul>
	 * <li>The other object references this instance. In other words {@code this == obj} has the value {@code true}.</li>
	 * <li>The other object is a {@code Vector} and represents the same Vector defined in terms of any basis.</li>
	 * </ul>
	 * </p>
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(!(obj instanceof Vector))
			return false;
		Vector vector = ((Vector) obj);
		if(this.components.length != vector.components.length)
			return false;
		Vector converted = vector.toBasis(this.basis);
		for(int i = 0; i < components.length; i++)
			if(this.components[i] != converted.components[i])
				return false;
		return true;
	}
	
	/**
	 * Generates a string representation of this Vector.
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(components.length * 6); // allocates just under 4 characters per value
		result.append('<');
		for(int i = 0; i < components.length; i++)
			result.append((Math.rint(components[i]) == components[i] ? String.format("%d", (int)components[i]) : String.format("%.3f", components[i])) + (i == components.length - 1 ? ">" : ", "));
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
	 * @throws VectorIndexOutOfBoundsException if {@code i} is negative or exceeds the valid indices of components in this Vector
	 */
	public double getComponent(int i) {
		if(i < 0 || i >= components.length)
			throw new VectorIndexOutOfBoundsException(i);
		
		return components[i];
	}
	
	/**
	 * Retrieves the basis used to determine the coordinates of this Vector.
	 * 
	 * @return the basis used to determine the coordinates of this Vector.
	 */
	public Basis getBasis() {
		return basis;
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
	 * Calculates the length (norm) of this Vector (in the standard basis of whatever space this Vector exists in).
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
	 * Takes into account the potential for differences between the bases which determine the coordinates of the two Vectors.
	 * 
	 * @param v the Vector to compare this Vector to
	 * @return true if they are scalar multiples of each other, false otherwise
	 */
	public boolean isScalarMultipleOf(Vector v) {
		if(this.components.length != v.components.length)
			return false;
		if(this.isZeroVector())
			return v.isZeroVector();
		
		Vector converted = v.toBasis(this.basis);
		double multiplier = 0;
		for(int i = 0; i < components.length; i++)
			if(this.components[i] != 0) {
				multiplier = converted.components[i] / this.components[i];
				break;
			}
		for(int i = 0; i < components.length; i++)
			if(this.components[i] * multiplier != converted.components[i])
				return false;
		return true;
	}
	
	/**
	 * Determines if this Vector is orthogonal to another Vector.
	 * Takes into account the potential for differences between the bases which determine the coordinates of the two Vectors.
	 * 
	 * @param v the Vector to compare this Vector to
	 * @return true if they are orthogonal, false otherwise
	 */
	public boolean isOrthogonalTo(Vector v) {
		return this.dotProduct(v) == 0;
	}
	
	/**
	 * Determines if this Vector can be expressed as a linear combination of an array of Vectors,
	 * that is if this Vector could be expressed as the sum of scalar multiples of each of the provided Vectors.
	 * Takes into account the potential for differences between the bases which determine the coordinates of the Vectors.
	 * 
	 * @param vectors the Vectors to test
	 * @return true if this Vector can be expressed as a linear combination of {@code vectors}, false otherwise
	 */
	public boolean isLinearCombinationOf(Vector[] vectors) {
		if(vectors.length == 0)
			return false;
		Vector[] converted = new Vector[vectors.length];
		for(int i = 0; i < converted.length; i++)
			converted[i] = vectors[i].toBasis(this.basis);
		return new Matrix(converted).isConsistent(this);
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
	 * The result will be defined in terms of the same basis as this Vector in defined.
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
	 * The result will be defined in terms of the same basis as this Vector in defined.
	 * 
	 * @param v the Vector to add
	 * @return the sum of the two Vectors
	 * @throws ArithmeticException if the Vectors are in different spaces
	 */
	public Vector add(Vector v) {
		if(this.components.length != v.components.length)
			throw new ArithmeticException("Cannot add or subtract Vectors that are not in the same space.");
		
		Vector sum = this.clone(), converted = v.toBasis(this.basis);
		for(int i = 0; i < sum.components.length; i++)
			sum.components[i] += converted.components[i];
		return sum;
	}
	
	/**
	 * Calculates the difference of this Vector and another Vector.
	 * The two Vectors must be in the same space.
	 * The result will be defined in terms of the same basis as this Vector is defined.
	 * 
	 * @param v the Vector to subtract
	 * @return the difference of the two Vectors
	 * @throws ArithmeticException if the Vectors are in different spaces
	 */
	public Vector subtract(Vector v) {
		return this.add(v.scalarMultiply(-1));
	}
	
	/**
	 * Calculates the dot product this Vector and another Vector (in the standard basis of whatever space this Vector exists in).
	 * The two Vectors must be in the same space.
	 * 
	 * @param v the Vector to perform the dot product with
	 * @return the dot product of the two Vectors
	 * @throws ArithmeticException if the Vectors are in different spaces
	 */
	public double dotProduct(Vector v) {
		if(this.components.length != v.components.length)
			throw new ArithmeticException("Cannot perform the dot product on Vectors that are not in the same space.");
		
		Vector standard1 = this.toStandardBasis(), stadard2 = v.toStandardBasis();
		double sum = 0;
		for(int i = 0; i < components.length; i++)
			sum += standard1.components[i] * stadard2.components[i];
		return sum;
	}
	
	/**
	 * Generates a new Vector which is equivalent to this Vector but is defined in terms of a different basis.
	 * 
	 * @param newBasis the basis to change to
	 * @return a new Vector which is equivalent to this Vector but defined in terms of {@code newBasis}
	 * @throws IllegalArgumentException if the current basis and {@code newBasis} do not exist within the same space
	 */
	public Vector toBasis(Basis newBasis) {
		if(newBasis == null)
			return toStandardBasis();
		if(components.length != newBasis.inSpace())
			throw new IllegalArgumentException("The new basis must exist within the same space as the current basis.");
		
		Basis basis = this.basis == null ? Basis.standardBasis(componentCount()) : this.basis;
		return /*basis.equals(newBasis) ? this :*/ new Vector((newBasis.asMatrix().inverse().multiply(basis.asMatrix())).multiply(this).components, newBasis);
	}
	
	/**
	 * Generates a new Vector which is equivalent to this Vector but defined in terms of the standard basis of the space in which this Vector exists.
	 * 
	 * @return a new Vector which is equivalent to this Vector but defined in terms of the standard basis of the space in which this Vector exists.
	 */
	public Vector toStandardBasis() {
		return this.basis == null ? this : toBasis(Basis.standardBasis(componentCount()));
	}
	
}
