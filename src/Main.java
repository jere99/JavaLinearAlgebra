
public class Main {

	public static void main(String[] args) {
		Matrix m = new Matrix(new double[][] {{1,2,3},{-1,0,-1},{3,2,1}});
		System.out.println(m);
		System.out.println(m.rref());
		System.out.println(m.rank() + "\n");
		System.out.println(m.add(m));
		System.out.println(m.scalarMultiply(10));
		System.out.println(m.matrixMultiply(m));
	}

}
