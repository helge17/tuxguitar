package org.herac.tuxguitar.gui.tools.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TGCustomToolManager {
	
	private static TGCustomToolManager instance;
	
	private List tools;
	
	public TGCustomToolManager(){
		this.tools = new ArrayList();
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
	
	public Iterator getCustomTools(){
		return this.tools.iterator();
	}
	
}
