package org.herac.tuxguitar.app.view.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.editor.TGUpdateEvent;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGToolBar implements TGEventListener {
	
	private static final String SELECTED_CHAR = "\u2713";
	
	private TGContext context;
	private List<TGToolBarSection> sections;
	private ToolBar control;
	
	private TGToolBar(TGContext context) {
		this.context = context;
		this.appendListeners();
		this.createSections();
	}
	
	public void appendListeners() {
		TuxGuitar.getInstance().getIconManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}
	
	public void createSections() {
		this.sections = new ArrayList<TGToolBarSection>();
		this.sections.add(new TGToolBarSectionFile());
		this.sections.add(new TGToolBarSectionDivider());
		this.sections.add(new TGToolBarSectionEdit());
		this.sections.add(new TGToolBarSectionDivider());
		this.sections.add(new TGToolBarSectionTrack());
		this.sections.add(new TGToolBarSectionDivider());
		this.sections.add(new TGToolBarSectionComposition());
		this.sections.add(new TGToolBarSectionDivider());
		this.sections.add(new TGToolBarSectionDuration());
		this.sections.add(new TGToolBarSectionDynamic());
		this.sections.add(new TGToolBarSectionEffect());
		this.sections.add(new TGToolBarSectionBeat());
		this.sections.add(new TGToolBarSectionChord());
		this.sections.add(new TGToolBarSectionDivider());
		this.sections.add(new TGToolBarSectionLayout());
		this.sections.add(new TGToolBarSectionView());
		this.sections.add(new TGToolBarSectionMarker());
		this.sections.add(new TGToolBarSectionDivider());
		this.sections.add(new TGToolBarSectionTransport());
	}
	
	public void createToolBar(Composite parent){
		FormData formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		formData.top = new FormAttachment(0,0);
		
		this.control = new ToolBar(parent, SWT.HORIZONTAL | SWT.FLAT | SWT.WRAP);
		this.control.setLayoutData(formData);
		for(TGToolBarSection section : this.sections) {
			section.createSection(this);
		}
	}

	public void updateItems() {
		for(TGToolBarSection section : this.sections) {
			section.updateItems(this);
		}
	}

	public void loadIcons() {
		for(TGToolBarSection section : this.sections) {
			section.loadIcons(this);
		}
	}
	
	public void loadProperties() {
		for(TGToolBarSection section : this.sections) {
			section.loadProperties(this);
		}
	}
	
	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(getContext(), actionId);
	}
	
	public String getText(String key) {
		return TuxGuitar.getProperty(key);
	}
	
	public String getText(String key, boolean selected) {
		return this.toCheckString(getText(key), selected);
	}
	
	public String toCheckString(String text, boolean selected) {
		return ((selected ? SELECTED_CHAR : "") + text);
	}
	
	public TGIconManager getIconManager() {
		return TuxGuitar.getInstance().getIconManager();
	}
	
	public Tablature getTablature() {
		return TablatureEditor.getInstance(getContext()).getTablature();
	}
	
	public TGSong getSong() {
		return TGDocumentManager.getInstance(getContext()).getSong();
	}
	
	public void layout() {
		if(!this.isDisposed()) {
			this.getControl().getParent().layout(true, true);
		}
	}
	
	public void updateVisibility(boolean visible) {
		if(!this.isDisposed()) {
			FormData formData = (FormData) this.getControl().getLayoutData(); 
			formData.bottom = (visible ? null : new FormAttachment(0));
			
			this.getControl().setVisible(visible);
			this.layout();
		}
	}
	
	public void toogleVisibility() {
		this.updateVisibility(!this.isVisible());
	}
	
	public boolean isVisible() {
		return (!this.isDisposed() ? this.getControl().isVisible() : false);
	}
	
	public boolean isDisposed() {
		return (this.getControl() == null || this.getControl().isDisposed());
	}
	
	public TGContext getContext() {
		return context;
	}

	public ToolBar getControl() {
		return control;
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.updateItems();
		}
	}
	
	public void processEvent(final TGEvent event) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadIcons();
				}
				else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadProperties();
				}
				else if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					processUpdateEvent(event);
				}
			}
		});
	}
	
	public static TGToolBar getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGToolBar.class.getName(), new TGSingletonFactory<TGToolBar>() {
			public TGToolBar createInstance(TGContext context) {
				return new TGToolBar(context);
			}
		});
	}
}
