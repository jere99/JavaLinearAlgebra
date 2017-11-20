package jere99.javaLinearAlgebra;

import jere99.javaLinearAlgebra.foundation.*;
import jere99.javaLinearAlgebra.linearTransformations.*;

public class Main {

	public static void main(String[] args) {
		Matrix m = new Matrix(new double[][] {{-2, 5, 2.135}, {124445, 0, -1.4}, {-135.1, -1, 4514.235123}});
		System.out.println(m);
		System.out.println(m.toString(5));
	}

}
