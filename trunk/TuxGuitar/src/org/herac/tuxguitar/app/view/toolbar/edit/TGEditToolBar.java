package org.herac.tuxguitar.app.view.toolbar.edit;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tabfolder.TGTabFolder;
import org.herac.tuxguitar.app.view.toolbar.model.TGToolBarModel;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIFocusEvent;
import org.herac.tuxguitar.ui.event.UIFocusGainedListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UIScrollBarPanelLayout;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGEditToolBar extends TGToolBarModel implements UIFocusGainedListener {
	
	private static final int SCROLL_INCREMENT = 10;
	
	private UIScrollBarPanel control;
	private UIPanel sectionContainer;
	
	private TGEditToolBar(TGContext context) {
		super(context);
	}
	
	public void createToolBar(UIContainer parent, boolean visible) {
		UIFactory uiFactory = TGApplication.getInstance(this.getContext()).getFactory();
		
		this.control = uiFactory.createScrollBarPanel(parent, true, false, true);
		this.control.setVisible(visible);
		this.control.setLayout(new UIScrollBarPanelLayout(false, true, false, false, false, false));
		this.control.addFocusGainedListener(this);
		this.control.getVScroll().setIncrement(SCROLL_INCREMENT);
		this.control.getVScroll().addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGEditToolBar.this.getControl().layout();
			}
		});
		
		this.sectionContainer = uiFactory.createPanel(this.control, false);
		this.sectionContainer.setLayout(new UITableLayout());
		this.sectionContainer.addFocusGainedListener(this);
		this.createSections();
	}

	public void createSection(TGEditToolBarSection section) {
		UIControl control = section.createSection(this.sectionContainer);
		
		UITableLayout uiLayout = (UITableLayout) this.sectionContainer.getLayout();
		uiLayout.set(control, (this.getSections().size() + 1), 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
		
		this.addSection(section);
	}
	
	public void createSections() {
		this.clearSections();
		this.createSection(new TGEditToolBarSectionEdit(this));
		this.createSection(new TGEditToolBarSectionComposition(this));
		this.createSection(new TGEditToolBarSectionDuration(this));
		this.createSection(new TGEditToolBarSectionDynamic(this));
		this.createSection(new TGEditToolBarSectionEffect(this));
		this.createSection(new TGEditToolBarSectionBeat(this));
	}
	
	public void onFocusGained(UIFocusEvent event) {
		TGTabFolder.getInstance(this.getContext()).updateFocus();
	}
	
	public UIScrollBarPanel getControl() {
		return control;
	}
	
	public static TGEditToolBar getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGEditToolBar.class.getName(), new TGSingletonFactory<TGEditToolBar>() {
			public TGEditToolBar createInstance(TGContext context) {
				return new TGEditToolBar(context);
			}
		});
	}
}
