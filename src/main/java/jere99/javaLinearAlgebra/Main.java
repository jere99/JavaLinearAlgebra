package jere99.javaLinearAlgebra;

import jere99.javaLinearAlgebra.foundation.*;
import jere99.javaLinearAlgebra.linearTransformations.*;

public class Main {

	public static void main(String[] args) {
		LinearTransformation T = new Reflection(new Vector(new double[] {1, 1}));
		System.out.println(T.getTransformationMatrix());
		System.out.println(T.transform(new Vector(new double[] {5, -3})));
	}

}
