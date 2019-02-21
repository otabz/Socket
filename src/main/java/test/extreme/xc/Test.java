package test.extreme.xc;

public class Test {
	
	public static void main(String[] args) {
		int[] a = {6,0,7,4};
		int sum;
		for(int i=0; i<a.length-1; i++) {
			for(int j = i+1; j<a.length-(i+1); j++) {
				sum  = a[i] + a[j];
				found(sum, a);
			}
		}
	}
	
	private static void found(int sum, int[] a) {
		for(int i=0; i<a.length; i++) {
			if(a[i] == sum)
			System.out.printf("found at [%d]", i);
		}
	}

}
