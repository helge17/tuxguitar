package app.tuxguitar.app.view.toolbar.edit;

import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionProcessorListener;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.toolbar.model.TGToolBarSection;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.widget.UIContainer;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UILegendPanel;

public abstract class TGEditToolBarSection implements TGToolBarSection {

	private String sectionTitle;
	private TGEditToolBar toolBar;
	private UILegendPanel toolBarContainer;

	public TGEditToolBarSection(TGEditToolBar toolBar, String sectionTitle) {
		this.toolBar = toolBar;
		this.sectionTitle = sectionTitle;
	}

	public abstract void loadSectionIcons();

	public abstract void loadSectionProperties();

	public abstract void updateSectionItems();

	public abstract void createSectionToolBars();

	public UIControl createSection(UIContainer container) {
		UIFactory uiFactory = TGApplication.getInstance(this.toolBar.getContext()).getFactory();

		UITableLayout layout = new UITableLayout();
		this.toolBarContainer = uiFactory.createLegendPanel(container);
		this.toolBarContainer.setLayout(layout);

		this.createSectionToolBars();
		this.loadIcons();
		this.loadProperties();

		List<UIControl> toolBars = this.toolBarContainer.getChildren();
		for(int i = 0; i < toolBars.size(); i ++) {
			layout.set(toolBars.get(i), (i + 1), 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false, 1, 1, null, null, 0f);
		}
		return this.toolBarContainer;
	}

	public UIToolBar createToolBar() {
		UIFactory uiFactory = TGApplication.getInstance(this.getToolBar().getContext()).getFactory();
		UIToolBar toolBar = uiFactory.createHorizontalToolBar(this.toolBarContainer);

		return toolBar;
	}

	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(this.toolBar.getContext(), actionId);
	}

	public void loadIcons() {
		this.loadSectionIcons();
	}

	public void loadProperties() {
		this.toolBarContainer.setText(this.getText(this.sectionTitle));
		this.loadSectionProperties();
	}

	public void updateItems() {
		this.updateSectionItems();
	}

	public String getText(String key) {
		return TuxGuitar.getProperty(key);
	}

	public TGIconManager getIconManager() {
		return TuxGuitar.getInstance().getIconManager();
	}

	public Tablature getTablature() {
		return TablatureEditor.getInstance(this.toolBar.getContext()).getTablature();
	}

	public TGSong getSong() {
		return TGDocumentManager.getInstance(this.toolBar.getContext()).getSong();
	}

	public TGEditToolBar getToolBar() {
		return toolBar;
	}
}
