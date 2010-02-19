package admin;

import java.util.Comparator;

//compare two nodes by their number of fromNodes

public class VertexParentsComparator implements Comparator
{

    public int compare(Object o1, Object o2)
    {
        Vertex n1 = (Vertex) o1;
        Vertex n2 = (Vertex) o2;
        return (n1.getFromVertices().size() - n2.getFromVertices().size());
    }
    
}

