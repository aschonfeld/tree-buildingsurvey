//TBS version 0.2: ModelElement
//Abstract Class at the top of Model Element Hierarchy

public abstract class ModelElement
{
	private String name;
	

	public ModelElement(String n)
	{
		name = n;
	}

	public String getName()
	{
		return name;
	}

	public String toString()
	{
		return name;
	}

	public abstract boolean isOrganismNode();
}
