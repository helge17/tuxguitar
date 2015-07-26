package org.herac.tuxguitar.app.tools.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TGCustomToolManager {
	
	private static TGCustomToolManager instance;
	
	private List<TGCustomTool> tools;
	
	public TGCustomToolManager(){
		this.tools = new ArrayList<TGCustomTool>();
	}
	
	public static TGCustomToolManager instance(){
		if(instance == null){
			instance = new TGCustomToolManager();
		}
		return instance;
	}
	
	public void addCustomTool(TGCustomTool tool){
		this.tools.add(tool);
	}
	
	public void removeCustomTool(TGCustomTool tool){
		this.tools.remove(tool);
	}
	
	public Iterator<TGCustomTool> getCustomTools(){
		return this.tools.iterator();
	}
	
}
