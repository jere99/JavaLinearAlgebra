package jere99.javaLinearAlgebra;

import java.util.Arrays;

import jere99.javaLinearAlgebra.foundation.*;
import jere99.javaLinearAlgebra.linearTransformations.*;

@SuppressWarnings("unused")
public class Main {

	public static void main(String[] args) {
		Vector[] vectors = new Vector[] {new Vector(new double[] {1, 1, 1}), new Vector(new double[] {1, 2, 3}), new Vector(new double[] {3, 4, 5})};
		System.out.println(Arrays.toString(vectors));
		System.out.println(Arrays.toString(Vector.removeRedundant(vectors)));
	}

}
