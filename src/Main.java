import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		Matrix m = new Matrix(new double[][] {{2,8,4,2},{2,5,1,5},{4,10,-1,1}});
		System.out.println(m);
		System.out.println(m.rref());
		System.out.println(m.rank() + "\n");
		System.out.println(Arrays.toString(m.findSolution()));
	}

}
