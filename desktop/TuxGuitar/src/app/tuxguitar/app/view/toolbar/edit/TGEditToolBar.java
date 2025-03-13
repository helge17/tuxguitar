package app.tuxguitar.app.view.toolbar.edit;

import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.component.tabfolder.TGTabFolder;
import app.tuxguitar.app.view.toolbar.model.TGToolBarModel;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIFocusEvent;
import app.tuxguitar.ui.event.UIFocusGainedListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UIScrollBarPanelLayout;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIContainer;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIScrollBarPanel;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

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
