/*
 * Created on 18-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.gui.actions.layout.SetChordDiagramEnabledAction;
import org.herac.tuxguitar.gui.actions.layout.SetChordNameEnabledAction;
import org.herac.tuxguitar.gui.actions.layout.SetScoreEnabledAction;
import org.herac.tuxguitar.gui.actions.layout.SetTablatureEnabledAction;
import org.herac.tuxguitar.gui.actions.measure.GoNextMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.GoPreviousMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.SelectMeasureAction;
import org.herac.tuxguitar.gui.actions.settings.SettingsAction;
import org.herac.tuxguitar.gui.actions.track.SelectTrackAction;
import org.herac.tuxguitar.gui.actions.transport.TransportMixerAction;
import org.herac.tuxguitar.gui.actions.transport.TransportPlayAction;
import org.herac.tuxguitar.gui.actions.transport.TransportSetupAction;
import org.herac.tuxguitar.gui.actions.transport.TransportStopAction;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ActionManager {
	
	private Map actions;
	
	public ActionManager(){
		this.actions = new HashMap();
		this.init();
	}
	
	public void init(){
		//layout actions
		addAction(new SetScoreEnabledAction());
		addAction(new SetTablatureEnabledAction());
		addAction(new SetChordNameEnabledAction());
		addAction(new SetChordDiagramEnabledAction());
		
		//track actions	
		addAction(new SelectTrackAction());
		
		//measure actions
		addAction(new SelectMeasureAction());
		addAction(new GoNextMeasureAction());
		addAction(new GoPreviousMeasureAction());
		
		//player actions
		addAction(new TransportPlayAction());
		addAction(new TransportStopAction());
		addAction(new TransportMixerAction());
		addAction(new TransportSetupAction());
		
		//Settings
		addAction(new SettingsAction());
	}
	
	public void addAction(Action action){
		this.actions.put(action.getName(),action);
	}
	
	public void removeAction(String name){
		this.actions.remove(name);
	}
	
	public Action getAction(String name){
		return (Action)this.actions.get(name);
	}
	
	public List getAvailableKeyBindingActions(){
		List availableKeyBindingActions = new ArrayList();
		Iterator it = this.actions.keySet().iterator();
		while(it.hasNext()){
			String actionName = (String)it.next();
			if(getAction(actionName).isKeyBindingAvailable()){
				availableKeyBindingActions.add(actionName);
			}
		}
		return availableKeyBindingActions;
	}
}
