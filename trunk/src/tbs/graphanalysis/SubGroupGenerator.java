package tbs.graphanalysis;

import java.util.*;
/* 
 * @author  Manoj Dhanji & Andrew Schonfeld
 */
public class SubGroupGenerator
{
	public static void populateArray(Integer[] o){
		for (int i = 0;i<o.length; i++){
			o[i] = new Integer(i);
		}
 
	}
	
	public static Set<Set<Integer>> getIndexSubGroups(int size){
		Set<Set<Integer>> subGroups = new HashSet<Set<Integer>>();
		try{
			Integer[] indexes = new Integer[size];
			populateArray(indexes);
			getSubsets(indexes, subGroups, 1);
			for(Integer index : indexes){
				Set<Integer> temp = new HashSet<Integer>();
				temp.add(index);
				subGroups.remove(temp);
			}
		}catch(NumberFormatException nfe){
			nfe.printStackTrace(System.err);
		}
		return subGroups;
		
	}
	
	public static void getSubsets(Integer[] indexes, Set<Set<Integer>> subGroups, int n){
		for (int i = 0;i<indexes.length ;i++ ){
			Set<Integer> s = new HashSet<Integer>();
			if(n==1){
				s.add(indexes[i]);
				subGroups.add(s);
			}else if(n>1){
				if(n>indexes.length)
					return;
				generateHigherOrderSubSets(subGroups, n, indexes);
			}
		}
		getSubsets(indexes, subGroups, ++n);
	}
	
	public static void generateHigherOrderSubSets(Set<Set<Integer>> subGroups, int n, Integer[] o){
		List<Set<Integer>> list1 = getList(subGroups, 1);
		List<Set<Integer>> list2 = getList(subGroups, n-1);
		for (int x = 0;x<list2.size() ;x++ ){
			for (int y= 0;y<list1.size() ;y++ ){
				Set<Integer> s = new HashSet<Integer>();
				if(!getListOfObjectsFromASetContainedInAList(list2, x).contains(getListOfObjectsFromASetContainedInAList(list1, y).get(0))){
					List<Integer> l1 = getListOfObjectsFromASetContainedInAList(list2, x);
					l1.add(getListOfObjectsFromASetContainedInAList(list1, y).get(0));
					for (int z=0;z<l1.size() ;z++ )
						s.add(l1.get(z));
					if(!subGroups.contains(s))
						subGroups.add(s);
				}
			}
		}
	}
	//From a list that that contains Sets
	//Copy the Set to an Object array
	//transform the array into a List
	public static List<Integer> getListOfObjectsFromASetContainedInAList(List<Set<Integer>> list, int x){
		Integer[] subGroup = list.get(x).toArray(new Integer[0]);
		List<Integer> l = new ArrayList<Integer>();
		for (int i = 0 ;i<subGroup.length ;i++ )
			l.add(subGroup[i]);
		return l;
	}
	//From a Collection
	//get a List of Sets of a certain size
	public static List<Set<Integer>> getList(Set<Set<Integer>> subGroups, int size){
		List<Set<Integer>> list = new ArrayList<Set<Integer>>();
		for (Iterator<Set<Integer>> iter = subGroups.iterator();iter.hasNext(); ){
			Set<Integer> s = iter.next();
			if(s.size()==size){
				list.add(s);
			}
		}
		return list;
	}
}