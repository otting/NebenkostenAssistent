
public class TestClass {

    public static void main(String[] args) {
	String a = "A5-$A$1+$B$2/$C$6*A4";
	String from = "A";
	String to = "B";
	Long start = System.currentTimeMillis();
	String regex = from + "[0-9]+";
	int check = 2;
	for (int i = 0; i <= a.length() - check; i++) {
	    String sub = a.substring(i, i + check);
	    System.out.println(sub);
	    if (sub.matches(regex)) {
		a = a.replace(sub, sub.replace(from, to));
	    }
	}

	System.out.println(a);
	System.out.println(System.currentTimeMillis() - start);
    }

}
