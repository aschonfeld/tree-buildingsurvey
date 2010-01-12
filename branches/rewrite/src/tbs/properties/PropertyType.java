package tbs.properties;

public enum PropertyType {

	HELP("help.properties", true),
	INSTRUCTIONS("instructions.properties", true),
	ORGANISMS("organisms.properties", false),
	QUESTIONS("questions.properties", true),
	STATUS("status.properties", true);
	
	private String filename;
	
	private Boolean loadedToModel;
	
	private PropertyType(String filename, Boolean loadedToModel){
		this.filename = filename;
		this.loadedToModel = loadedToModel;
	}
	
	public String getFilename(){
		return filename;	
	}
	
	public Boolean isLoadedToModel(){
		return loadedToModel;
	}
}
