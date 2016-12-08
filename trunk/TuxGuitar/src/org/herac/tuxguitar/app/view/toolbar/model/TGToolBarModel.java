package org.herac.tuxguitar.app.view.toolbar.model;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGToolBarModel implements TGEventListener {
	
	private TGContext context;
	private List<TGToolBarSection> sections;
	
	private TGSyncProcessLocked loadIconsProcess;
	private TGSyncProcessLocked loadPropertiesProcess;
	private TGSyncProcessLocked updateItemsProcess;
	
	public TGToolBarModel(TGContext context) {
		this.context = context;
		this.sections = new ArrayList<TGToolBarSection>();
		this.createSyncProcesses();
		this.appendListeners();
	}
	
	public void appendListeners() {
		TuxGuitar.getInstance().getIconManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}
	
	public void createSyncProcesses() {		
		this.updateItemsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				updateItems();
			}
		});
		
		this.loadIconsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				loadIcons();
			}
		});
		
		this.loadPropertiesProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				loadProperties();
			}
		});
	}
	
	public void updateItems() {
		if(!this.isDisposed()) {
			for(TGToolBarSection section : this.sections) {
				section.updateItems();
			}
		}
	}

	public void loadIcons() {
		if(!this.isDisposed()) {
			for(TGToolBarSection section : this.sections) {
				section.loadIcons();
			}
		}
	}
	
	public void loadProperties() {
		if(!this.isDisposed()) {
			for(TGToolBarSection section : this.sections) {
				section.loadProperties();
			}
		}
	}
	
	public void updateVisibility(boolean visible) {
		if(!this.isDisposed() && (this.isVisible() != visible)) {
			this.getControl().setVisible(visible);
			
			TGWindow.getInstance(this.context).getWindow().layout();
		}
	}
	
	public void toogleVisibility() {
		this.updateVisibility(!this.isVisible());
	}
	
	public boolean isVisible() {
		return (!this.isDisposed() && this.getControl().isVisible());
	}
	
	public boolean isDisposed() {
		return (this.getControl() == null || this.getControl().isDisposed());
	}
	
	public void clearSections() {
		this.sections.clear();
	}
	
	public void addSection(TGToolBarSection section) {
		this.sections.add(section);
	}
	
	public List<TGToolBarSection> getSections() {
		return this.sections;
	}
	
	public TGContext getContext() {
		return this.context;
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.updateItemsProcess.process();
		}
	}
	
	public void processEvent(final TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
		else if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIconsProcess.process();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadPropertiesProcess.process();
		}
	}
	
	public abstract UIControl getControl();
}
