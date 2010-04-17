package tbs.graphanalysis;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;


public class VertexInfo {

	private String name;
	private Map<Integer,String> types;
	private BufferedImage image;
	private VertexType vertexType;
	
	public VertexInfo(String name){
		this.name = name;
		types = new HashMap<Integer, String>();
		image = null;
		vertexType = VertexType.EMPTY;
	}
	
	public VertexInfo(String name, Map<Integer,String> types, BufferedImage image){
		this(name);
		this.types = types;
		this.image = image;
		vertexType = VertexType.ORGANISM;
	}

	public String getName() {
		return name;
	}

	public Map<Integer,String> getTypes() {
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
