/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action;

import java.util.ArrayList;
import java.util.List;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window 
 * Preferences - Java - Code Style - Code Templates
 */
public class TGActionIdList {
	
	private List actionIds;
	
	public TGActionIdList() {
		this.actionIds = new ArrayList();
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
	
	public List getActionIds(){
		return new ArrayList(this.actionIds);
	}
}
