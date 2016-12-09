package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.toolbar.model.TGToolBarModel;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGMainToolBar extends TGToolBarModel {
	
	private UIToolBar control;
	
	private TGMainToolBar(TGContext context) {
		super(context);
	}
	
	public void createToolBar(UIContainer parent, boolean visible){
		UIFactory uiFactory = TGApplication.getInstance(this.getContext()).getFactory();
		this.control = uiFactory.createHorizontalToolBar(parent);
		this.control.setVisible(visible);
		this.createSections();
	}
	
	public void createSection(TGMainToolBarSection section) {
		section.createSection();
		
		this.addSection(section);
	}
	
	public void createSections() {
		this.clearSections();
		this.createSection(new TGMainToolBarSectionFile(this));
		this.createSection(new TGMainToolBarSectionDivider(this));
		this.createSection(new TGMainToolBarSectionEdit(this));
		this.createSection(new TGMainToolBarSectionDivider(this));
		this.createSection(new TGMainToolBarSectionComposition(this));
		this.createSection(new TGMainToolBarSectionDivider(this));
		this.createSection(new TGMainToolBarSectionTrack(this));
		this.createSection(new TGMainToolBarSectionDivider(this));
		this.createSection(new TGMainToolBarSectionLayout(this));
		this.createSection(new TGMainToolBarSectionView(this));
		this.createSection(new TGMainToolBarSectionMarker(this));
		this.createSection(new TGMainToolBarSectionDivider(this));
		this.createSection(new TGMainToolBarSectionTransport(this));
	}
	
	public UIToolBar getControl() {
		return control;
	}
	
	public static TGMainToolBar getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMainToolBar.class.getName(), new TGSingletonFactory<TGMainToolBar>() {
			public TGMainToolBar createInstance(TGContext context) {
				return new TGMainToolBar(context);
			}
		});
	}
}
