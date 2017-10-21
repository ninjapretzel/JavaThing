
import java.util.HashMap;
import java.util.Map;



public class Node {
	
	public Map<String, String> data;
	public Map<String, Node> children;
	int dataListSize = 0;
	int childrenListSize = 0;
	
	public Node() {
		data = new HashMap<>();
		children = new HashMap<>();
		dataListSize = childrenListSize = 0;
	}
	
	public void mapChild(String name, Node child){
		if (child != null) {
			children.put(name, child);
		}
	}
	
	public void listChild(Node child) {
		if (child != null) {
			children.put(""+childrenListSize++, child);
		}
	}
			
	
	public void mapData(String name, Token val) {
		if (val != null) {
			data.put(name, val.content);
		}
	}
	public void mapData(String name, String val) {
		if (val != null) {
			data.put(name, val);
		}
	}
	public void listData(Token val) {
		if (val != null) {
			listData(val.content);
		}
	}
	
	public void listData(String val) {
		if (val != null) {
			data.put(""+dataListSize++, val);
		}
	}
	
}
