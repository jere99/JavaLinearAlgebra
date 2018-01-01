package jere99.javaLinearAlgebra.foundation;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines a basis of a subspace of <html>&#x211D<sup><em>n</em></sup></hmtl>.
 * 
 * @author JeremiahDeGreeff
 * @see VectorSpace
 */
public class Basis {
	
	//================================================================================
	// Static Variables
	//================================================================================
	
	/**
	 * Cached references to standard bases of <html>&#x211D<sup><em>n</em></sup></hmtl> keyed by <em>n</em> value.
	 */
	private static Map<Integer, Basis> standardBases = new HashMap<Integer, Basis>();

	//================================================================================
	// Static Methods
	//================================================================================
	
	/**
	 * Returns a reference to the standard basis of <html>&#x211D<sup><em>n</em></sup></hmtl> for a specified <em>n</em>.
	 * 
	 * @param n the dimension of the desired real space
	 * @return the specified standard basis of <html>&#x211D<sup><em>n</em></sup></hmtl>
	 */
	public static Basis standardBasis(int n) {
		if(!standardBases.containsKey(n)) {
			Vector[] basis = new Vector[n];
			for(int i = 0; i < n; i++)
				basis[i] = Vector.getStandardVector(n, i + 1);
			standardBases.put(n, new Basis(basis));
		}
		return standardBases.get(n);
	}
	
	//================================================================================
	// Instance Variables
	//================================================================================
	
	/**
	 * The Vectors which form this basis.
	 * All of the basis vectors must be in the same <html>&#x211D<sup><em>n</em></sup></hmtl>.
	 * All of the basis vectors must be defined in terms of the standard basis of <html>&#x211D<sup><em>n</em></sup></hmtl>.
	 */
	private final Vector[] contents;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * Creates a subspace of <html>&#x211D<sup><em>n</em></sup></hmtl> from Vectors which form its basis.
	 * All of the basis Vectors must be in the same space and be linearly independent.
	 * 
	 * @param basisVectors the Vectors which form this basis
	 * @throws IllegalArgumentException if any of the following is true:
	 * <ul>
	 * <li>the Vectors in {@code basisVectors} are not all in the same space</li>
	 * <li>the Vectors in {@code basisVectors} are not linearly independent and thus cannot represent a basis of a subspace</li>
	 * </ul>
	 */
	public Basis(Vector[] basisVectors) {
		if(basisVectors.length > 0) {
			int n = basisVectors[0].componentCount();
			for(Vector v : basisVectors)
				if(v.componentCount() != n)
					throw new IllegalArgumentException("All the vectors must be in the same space.");
		if(!Vector.areLinearlyIndependent(basisVectors))
			throw new IllegalArgumentException("All the vectors must be in linearly independent to form a basis.");
		}
		
		Vector[] converted = new Vector[basisVectors.length];
		for(int i = 0; i < converted.length; i++)
			converted[i] = basisVectors[i].toStandardBasis();
		contents = converted;
	}
	
	//================================================================================
	// Overrides
	//================================================================================
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * <p>
	 * The other object is considered "equal" if either:
	 * <ul>
	 * <li>The other object references this instance. In other words {@code this == obj} has the value {@code true}.</li>
	 * <li>The other object is a {@code Basis} and has contents identical to those of this instance.</li>
	 * </ul>
	 * </p>
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(!(obj instanceof Basis))
			return false;
		Basis basis = (Basis) obj;
		if(this.length() != basis.length())
			return false;
		for(int i = 0; i < length(); i++)
			if(!this.contents[i].equals(basis.contents[i]))
				return false;
		return true;
	}
	
	/**
	 * Generates a string representation of this Basis.
	 */
	@Override
	public String toString() {
		if(length() == 0)
			return "{0}";
		StringBuffer result = new StringBuffer(length() * contents[0].componentCount() * 6); // allocates just under 4 characters per value
		result.append('{');
		for(int i = 0; i < length(); i++)
			result.append(contents[i].toString() + (i == length() - 1 ? "}" : ", "));
		return result.toString();
	}
	
	//================================================================================
	// Accessor Methods
	//================================================================================
	
	/**
	 * Retrieves the number of Vectors in this Basis.
	 * This is equivalent to the dimension of the subspace formed by this Basis.
	 * 
	 * @return the number of Vectors in this Basis
	 */
	public int length() {
		return contents.length;
	}
	
	/**
	 * Determines the dimension of the space in which a subspace formed by this Basis would exist.
	 * This is equivalent to the number of components in each Vector of this Basis.
	 * 
	 * @return the dimension of the space in which a subspace formed by this basis would exist
	 */
	public int inSpace() {
		return length() == 0 ? 0 : contents[0].componentCount();
	}
	
	/**
	 * Determines if the subspace formed by this Basis would span the entirety of the space in which that subspace would exist.
	 * 
	 * @return true if subspace formed by this Basis would span the entirety of the space in which that subspace would exist, false otherwise
	 */
	public boolean spansFullSpace() {
		return length() == inSpace();
	}
	
	/**
	 * Determines if this Basis is a standard Basis,
	 * that is if all of its Vectors are standard Vectors.
	 * 
	 * @return true if this Basis is a standard Basis, false otherwise.
	 */
	public boolean isStandardBasis() {
		if(!spansFullSpace())
			return false;
		for(int i = 0; i < length(); i++)
			if(!contents[i].equals(Vector.getStandardVector(inSpace(), i)))
				return false;
		return true;
	}
	
	/**
	 * Generates the 'S' Matrix of this Basis,
	 * that is the Matrix whose columns are the Vectors of this basis.
	 * 
	 * @return the 'S' Matrix
	 */
	public Matrix asMatrix() {
		return new Matrix(this.contents);
	}
	
	/**
	 * Determines if a Vector is within the span of the Vectors in this Basis.
	 * 
	 * @param v the Vector to test
	 * @return true if {@code v} is within the span of the Vectors in this Basis, false otherwise
	 */
	public boolean contains(Vector v) {
		return v.isLinearCombinationOf(contents);
	}
	
	/**
	 * Determines if another Basis describes the same subspace as this Basis does.
	 * 
	 * @param other the Basis to test
	 * @return true if the two Bases describe the same subspace, false otherwise
	 */
	public boolean sameSubspace(Basis other) {
		return this.asMatrix().rref().equals(other.asMatrix().rref());
	}
	
}
