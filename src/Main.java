import foundation.Matrix;
import foundation.Vector;

public class Main {

	public static void main(String[] args) {
		Matrix m = new Matrix(new double[][] {{2,8,4},{2,5,1},{4,10,-1}});
		Vector v = new Vector(new double[] {3,7,1});
		System.out.println(m);
		System.out.println(v);
		System.out.println(m.matrixMultiply(v));
		System.out.println(v.getLength());
	}

}
