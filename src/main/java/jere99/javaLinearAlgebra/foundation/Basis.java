package jere99.javaLinearAlgebra.foundation;

/**
 * Defines a basis of a subspace of <html>&#x211D<sup><em>n</em></sup></hmtl>.
 * 
 * @author JeremiahDeGreeff
 * @see VectorSpace
 */
public class Basis {
	
	//================================================================================
	// Instance Variables
	//================================================================================
	
	/**
	 * The Vectors which form this basis.
	 * All of the basis Vectors must be in <html>&#x211D<sup><em>n</em></sup></hmtl>.
	 */
	private Vector[] contents;
	
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
		
		contents = basisVectors;
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
		if(this.contents.length != basis.contents.length)
			return false;
		for(int i = 0; i < contents.length; i++)
			if(this.contents[i] != basis.contents[i])
				return false;
		return true;
	}
	
	/**
	 * Generates a string representation of this Basis.
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(contents.length * contents[0].componentCount() * 6); // allocates just under 4 characters per value
		result.append('{');
		for(int i = 0; i < contents.length; i++)
			result.append(contents[i].toString() + (i == contents.length - 1 ? "}" : ", "));
		System.out.println(result.length() + "/" + result.capacity());
		return result.toString();
	}
	
	//================================================================================
	// Accessor Methods
	//================================================================================
	
	/**
	 * Retrieves the number of Vectors in this Basis.
	 * 
	 * @return the number of Vectors in this Basis
	 */
	public int length() {
		return contents.length;
	}
	
	/**
	 * Determines the dimension of the space in which a subspace formed by this basis would exist.
	 * This is equivalent to the number of components in each Vector of this Basis.
	 * 
	 * @return the dimension of the space in which a subspace formed by this basis would exist
	 */
	public int inSpace() {
		return contents.length == 0 ? 0 : contents[0].componentCount();
	}
	
	/**
	 * Determines if a Vector is within the span of the Vectors in this Basis.
	 * 
	 * @param v the Vector to test
	 * @return true if {@code v} is within the span of the Vectors in this Basis, false otherwise
	 */
	public boolean contains(Vector v) {
		return v.isLinearCombination(contents);
	}
	
	/**
	 * Determines if another Basis describes the same subspace as this Basis does.
	 * 
	 * @param other the Basis to test
	 * @return true if the two Bases describe the same subspace, false otherwise
	 */
	public boolean isEquivalent(Basis other) {
		return new Matrix(this.contents).rref().equals(new Matrix(other.contents).rref());
	}
	
}
