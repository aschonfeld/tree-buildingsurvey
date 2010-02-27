package tbs.graphanalysis;
import java.awt.image.BufferedImage;


public class VertexInfo {

	private String name;
	private String type;
	private BufferedImage image;
	private VertexType vertexType;
	
	public VertexInfo(String name){
		this.name = name;
		type = "";
		image = null;
		vertexType = VertexType.EMPTY;
	}
	
	public VertexInfo(String name, String type, BufferedImage image){
		this(name);
		this.type = type;
		this.image = image;
		vertexType = VertexType.ORGANISM;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
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
