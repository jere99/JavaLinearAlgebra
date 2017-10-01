import foundation.*;

public class Main {

	public static void main(String[] args) {
		Matrix a = new Matrix(new double[][]{{6,7},{8,9}});
		Matrix b = new Matrix(new double[][]{{1,2},{3,5}});
		
		System.out.println(a);
		System.out.println(b);
		
		System.out.println(a.multiply(b));
		System.out.println(b.multiply(a));
	}

}
