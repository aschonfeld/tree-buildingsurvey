package admin;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VertexInfo {

	private String name;
	private Map<Integer, String> types;
	private BufferedImage image;
	private VertexType vertexType;
	
	public VertexInfo(String name){
		this.name = name;
		this.types = new HashMap<Integer, String>();
		image = null;
		vertexType = VertexType.EMPTY;
	}
	
	public VertexInfo(String name, List<String> types, BufferedImage image){
		this(name);
		int index=1;
		for(String type : types){
			this.types.put(index, type);
			index++;
		}
		this.image = image;
		vertexType = VertexType.ORGANISM;
	}

	public String getName() {
		return name;
	}

	public Map<Integer, String> getTypes() {
		return types;
	}

	public BufferedImage getImage() {
		return image;
	}
	
	public VertexType getVertexType() {
		return vertexType;
	}
	
	// would like to generalize these sometime, but use biology terms for now
	public enum VertexType {
		ORGANISM,
		EMPTY;
	}
}
