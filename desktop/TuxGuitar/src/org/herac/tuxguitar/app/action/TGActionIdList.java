package org.herac.tuxguitar.app.action;

import java.util.ArrayList;
import java.util.List;

public class TGActionIdList {
	
	private List<String> actionIds;
	
	public TGActionIdList() {
		this.actionIds = new ArrayList<String>();
	}
	
	public boolean hasActionId(String actionId){
		return this.actionIds.contains(actionId);
	}
	
	public void removeActionId(String actionId){
		if( this.hasActionId(actionId) ){
			this.actionIds.remove(actionId);
		}
	}
	
	public void addActionId(String actionId){
		if(!this.hasActionId(actionId) ){
			this.actionIds.add(actionId);
		}
	}
	
	public void addActionIds(String[] actionIds){
		for(int i = 0 ; i < actionIds.length ; i ++){
			this.addActionId(actionIds[i]);
		}
	}
	
	public List<String> getActionIds(){
		return new ArrayList<String>(this.actionIds);
	}
}
