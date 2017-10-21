
import java.util.ArrayList;
import java.util.List;




public class blah {
	public static List<blah> allBlahs = new ArrayList<blah>();
	
	float x,y,z;
	public blah() {
		x = y = z = 0;
		allBlahs.add(this);
	}
	
	public void die() {
		allBlahs.remove(this);
	}
	
	public static void main(String[] args) {
		
		boolean val = false;
		
		if (val)
			System.out.println("I'm feeling blah");
		else	
			System.out.println("I'm feeling meh");
		
		int z = 20, w = 3;
		int a,b,c,d;
		String s = new blah().toString();
		new blah();
		;;;;;
		
		boolean check;
		if (check = z == 5);
		{
		
		}
	}
	
}