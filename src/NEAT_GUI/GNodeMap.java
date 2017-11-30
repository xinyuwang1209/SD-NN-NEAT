package NEAT_GUI;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/*
 * Todo:
 * -provide implementation of entrySet
 * -override put method
 * -override iterator by implementing remove method
 * -provide no argument and argument (?) 
 * */
public class GNodeMap extends AbstractMap{
	
	Map<Integer, GNode> gnodeMap;
	
	public GNodeMap(){
		gnodeMap = new HashMap<Integer, GNode>();
	}
	
	public GNodeMap(Map<Integer, GNode> gnm){
		gnodeMap = gnm;
	}
	
//	@Override
//	public GNode put(Integer entryKey, GNode entyValue){
//		gnodeMap.put(entryKey, entyValue);
//		return null;
//	}
//	
	@Override
	/* Completed for the sake of fulfilling contract with AbstractMap
	 * Intended purpose is to provide read-only set view of map; immutable set*/
	public Set<Map.Entry<Integer, GNode>> entrySet() {
		Set<Map.Entry<Integer, GNode>> mapEntries = new HashSet<Map.Entry<Integer, GNode>>();
		Iterator mapKeysItr = keySet().iterator();
		
		while(mapKeysItr.hasNext()){
			Integer entryKey = (Integer) mapKeysItr.next();
			GNode entryValue = (GNode) get(entryKey);
			mapEntries.add(new SimpleImmutableEntry<Integer, GNode>(entryKey, entryValue));
		}
		
		return mapEntries;
	}
	
}
