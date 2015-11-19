package org.herac.tuxguitar.app.action.installer;

import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.TGActionAdapterManager;
import org.herac.tuxguitar.app.action.impl.layout.SetChordDiagramEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.SetChordNameEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.SetScoreEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.SetTablatureEnabledAction;
import org.herac.tuxguitar.app.action.impl.measure.GoNextMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.GoPreviousMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.SelectMeasureAction;
import org.herac.tuxguitar.app.action.impl.settings.SettingsAction;
import org.herac.tuxguitar.app.action.impl.track.SelectTrackAction;
import org.herac.tuxguitar.app.action.impl.transport.TransportMixerAction;
import org.herac.tuxguitar.app.action.impl.transport.TransportPlayAction;
import org.herac.tuxguitar.app.action.impl.transport.TransportSetupAction;
import org.herac.tuxguitar.app.action.impl.transport.TransportStopAction;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGActionInstaller {
	
	private TGActionAdapterManager manager;
	private TGActionConfigMap configMap;
	
	public TGActionInstaller(TGActionAdapterManager manager) {
		this.manager = manager;
		this.configMap = new TGActionConfigMap();
	}
	
	public void installDefaultActions(){
		TGContext context = this.manager.getContext();
		
		//layout actions
		installAction(new SetScoreEnabledAction(context));
		installAction(new SetTablatureEnabledAction(context));
		installAction(new SetChordNameEnabledAction(context));
		installAction(new SetChordDiagramEnabledAction(context));
		
		//track actions	
		installAction(new SelectTrackAction(context));
		
		//measure actions
		installAction(new SelectMeasureAction(context));
		installAction(new GoNextMeasureAction(context));
		installAction(new GoPreviousMeasureAction(context));
		
		//player actions
		installAction(new TransportPlayAction(context));
		installAction(new TransportStopAction(context));
		installAction(new TransportMixerAction(context));
		installAction(new TransportSetupAction(context));
		
		//Settings
		installAction(new SettingsAction(context));
	}
	
	public void installAction(TGActionBase action) {
		String actionId = action.getName();
		
		TGActionManager.getInstance(this.manager.getContext()).mapAction(actionId, action);
		TGActionConfig config = this.configMap.get(actionId);
		if( config != null ) {
			if( config.isShortcutAvailable() ) {
				this.manager.getKeyBindingActionIds().add(actionId);
			}
			if( config.isDisableOnPlaying() ) {
				this.manager.getDisableOnPlayInterceptor().addActionId(actionId);
			}
			if( config.isSyncThread() ) {
				this.manager.getSyncThreadInterceptor().addActionId(actionId);
			}
			if( config.isLockableAction() ) {
				this.manager.getLockableActionListener().addActionId(actionId);
			}
			
			this.manager.getUpdatableActionListener().getControllers().set(actionId, config.getUpdateController());
		}
	}
}
