package jere99.javaLinearAlgebra;

import jere99.javaLinearAlgebra.foundation.*;
import jere99.javaLinearAlgebra.linearTransformations.*;

public class Main {

	public static void main(String[] args) {
		Matrix m = new Matrix(new double[][] {{1, 2, 2, -5, 6}, {-1, -2, -1, 1, -1}, {4, 8, 5, -8, 9}, {3, 6, 1, 5, -7}});
		System.out.println(m.findKernel());
	}

}
