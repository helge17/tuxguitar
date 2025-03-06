package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.toolbar.model.TGToolBarModel;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGMainToolBar extends TGToolBarModel {

	private UIPanel panel;

	private TGMainToolBar(TGContext context) {
		super(context);
	}

	public void createToolBar(UIWindow parentWindow, boolean visible){
		this.clearSections();
		UIFactory uiFactory = TGApplication.getInstance(this.getContext()).getFactory();
		this.panel = uiFactory.createPanel(parentWindow, false);
		this.panel.setVisible(visible);
		UITableLayout transportLayout = new UITableLayout();
		this.panel.setLayout(transportLayout);
		transportLayout.set(UITableLayout.MARGIN, 0f);

		UIToolBar  leftToolBar = uiFactory.createHorizontalToolBar(this.panel);
		this.createSection(new TGMainToolBarSectionFile(getContext(), leftToolBar));
		this.createSection(new TGMainToolBarSectionDivider(getContext(), leftToolBar));
		this.createSection(new TGMainToolBarSectionEdit(getContext(), leftToolBar));
		this.createSection(new TGMainToolBarSectionDivider(getContext(), leftToolBar));
		this.createSection(new TGMainToolBarSectionView(getContext(), leftToolBar));
		this.createSection(new TGMainToolBarSectionLayout(getContext(), leftToolBar));
		this.createSection(new TGMainToolBarSectionDivider(getContext(), leftToolBar));
		this.createSection(new TGMainToolBarSectionComposition(getContext(), leftToolBar));
		this.createSection(new TGMainToolBarSectionDivider(getContext(), leftToolBar));
		this.createSection(new TGMainToolBarSectionTrack(getContext(), leftToolBar));
		this.createSection(new TGMainToolBarSectionDivider(getContext(), leftToolBar));
		this.createSection(new TGMainToolBarSectionMarker(getContext(), leftToolBar));
		transportLayout.set(leftToolBar, 1,1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, true);

		UIToolBar centerLeftToolBar = uiFactory.createHorizontalToolBar(this.panel);
		this.createSection(new TGMainToolBarSectionTransportControl(getContext(), centerLeftToolBar));
		this.createSection(new TGMainToolBarSectionDivider(getContext(), centerLeftToolBar));
		transportLayout.set(centerLeftToolBar, 1,2, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		TGMainToolBarTransport centerToolBar = new TGMainToolBarTransport(getContext(), this.panel, parentWindow);
		transportLayout.set(centerToolBar.getTimestamp(), 1,3, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);
		transportLayout.set(centerToolBar.getTimestamp(), UITableLayout.PACKED_WIDTH, TGMainToolBarTransport.TIMESTAMP_MIN_WIDTH);
		transportLayout.set(centerToolBar.getTimestamp(), UITableLayout.PACKED_HEIGHT, TGMainToolBarTransport.TIMESTAMP_MIN_HEIGHT);
		transportLayout.set(centerToolBar.getTempoImage(), 1,4, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);
		transportLayout.set(centerToolBar.getTempoLabel(), 1,5, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);

		UIToolBar centerRightToolBar = uiFactory.createHorizontalToolBar(this.panel);
		this.createSection(new TGMainToolBarSectionDivider(getContext(), centerRightToolBar));
		this.createSection(new TGMainToolBarSectionTransportMode(getContext(), centerRightToolBar));
		transportLayout.set(centerRightToolBar, 1,6, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, true, true);

	}

	public void createSection(TGMainToolBarSection section) {
		section.createSection();

		this.addSection(section);
	}

	public UIPanel getControl() {
		return this.panel;
	}

	public static TGMainToolBar getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMainToolBar.class.getName(), new TGSingletonFactory<TGMainToolBar>() {
			public TGMainToolBar createInstance(TGContext context) {
				return new TGMainToolBar(context);
			}
		});
	}
}
