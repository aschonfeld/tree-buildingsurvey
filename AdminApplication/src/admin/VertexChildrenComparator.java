package admin;

import java.util.Comparator;

public class VertexChildrenComparator implements Comparator
{

    public int compare(Object o1, Object o2)
    {
        Vertex n1 = (Vertex) o1;
        Vertex n2 = (Vertex) o2;
        return (n1.getToVertices().size() - n2.getToVertices().size());
    }
    
}

