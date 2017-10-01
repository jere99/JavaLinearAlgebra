import foundation.*;

public class Main {

	public static void main(String[] args) {
		Vector v = new Vector(new double[] {0,0,0});
		System.out.println(v);
		
		System.out.println(v.isLinearCombination(new Vector[] {
				new Vector(new double[] {1,4,7}),
				new Vector(new double[] {2,5,8}),
				new Vector(new double[] {3,6,9})
		}));
	}

}
