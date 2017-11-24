package jere99.javaLinearAlgebra.foundation;

import java.util.TreeMap;

/**
 * Defines a (linear) subspace of <html>&#x211D<sup><em>n</em></sup></hmtl> where <em>n</em> can be any positive integer.
 * 
 * <p>A subspace <em>W</em> of <html>&#x211D<sup><em>n</em></sup></hmtl> must:
 * <ul>
 * <li>contain the zero vector of <html>&#x211D<sup><em>n</em></sup></hmtl></li>
 * <li>be closed under addition,
 * that is for all vectors <em>w</em><html><sub>1</sub></hmtl> and <em>w</em><html><sub>2</sub></hmtl> in <em>W</em>,
 * <em>w</em><html><sub>1</sub></hmtl> + <em>w</em><html><sub>2</sub></hmtl> must also be in <em>W</em></li>
 * <li>be closed under scalar multiplication,
 * that is for all vectors <em>w</em> in <em>W</em> and for all scalars <em>k</em>,
 * <em>kw</em> must also be in <em>W</em></li>
 * </ul>
 * </p>
 * 
 * @author JeremiahDeGreeff
 */
public class VectorSpace {
	
	//================================================================================
	// Static Variables
	//================================================================================
	
	private static TreeMap<Integer, VectorSpace> realSpaces = new TreeMap<Integer, VectorSpace>();
	
	//================================================================================
	// Static Methods
	//================================================================================
	
	/**
	 * Returns a reference to an instance of <html>&#x211D<sup><em>n</em></sup></hmtl> for a specified <em>n</em>.
	 * 
	 * @param n the dimension of the real space to return
	 * @return a reference to the instance of the specified <html>&#x211D<sup><em>n</em></sup></hmtl>
	 */
	public static VectorSpace getRealSpace(int n) {
		if(!realSpaces.containsKey(n)) {
			Vector[] basis = new Vector[n];
			for(int i = 0; i < n; i++)
				basis[i] = Vector.getStandardVector(n, i + 1);
			realSpaces.put(n, new VectorSpace(new Basis(basis)));
		}
		return realSpaces.get(n);
	}
	
	//================================================================================
	// Instance Variables
	//================================================================================
	
	/**
	 * A basis of this subspace.
	 */
	private Basis basis;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * Creates a subspace of <html>&#x211D<sup><em>n</em></sup></hmtl> from its basis.
	 * 
	 * @param basis a basis of this subspace
	 */
	public VectorSpace(Basis basis) {
		this.basis = basis;
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
	 * <li>The other object is a {@code VectorSpace} and has a basis which is identical or equivalent to that of this instance.</li>
	 * </ul>
	 * </p>
	 */
	@Override
	public boolean equals(Object obj) {
		return this == obj || obj instanceof VectorSpace && (this.basis == ((VectorSpace) obj).basis || this.basis.isEquivalent(((VectorSpace) obj).basis));
	}
	
	/**
	 * Generates a string representation of this VectorSpace.
	 */
	@Override
	public String toString() {
		return dimension() + " dimensional subspace of " + inSpace() + "-space with basis: " + basis.toString();
	}
	
	//================================================================================
	// Accessor Methods
	//================================================================================
	
	/**
	 * Determines the dimension of this subspace.
	 * This is equivalent to determining the number of Vectors in the basis of this subspace.
	 * 
	 * @return the dimension of this VectorSpace
	 */
	public int dimension() {
		return basis.length();
	}
	
	/**
	 * Determines the dimension of the real space in which this VectorSpace exists.
	 * 
	 * @return the dimension of the real space in which this VectorSpace exists
	 */
	public int inSpace() {
		return basis.inSpace();
	}
	
	/**
	 * Determines if a Vector is within this subspace.
	 * 
	 * @param v the Vector to test
	 * @return true if {@code v} is within this subspace, false otherwise
	 */
	public boolean contains(Vector v) {
		return basis.contains(v);
	}
}
