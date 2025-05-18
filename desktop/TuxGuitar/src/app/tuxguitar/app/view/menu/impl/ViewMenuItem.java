package app.tuxguitar.app.view.menu.impl;


import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.layout.TGSetChordDiagramEnabledAction;
import app.tuxguitar.app.action.impl.layout.TGSetChordNameEnabledAction;
import app.tuxguitar.app.action.impl.layout.TGSetCompactViewAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleDecrementAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleIncrementAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleResetAction;
import app.tuxguitar.app.action.impl.layout.TGSetLinearLayoutAction;
import app.tuxguitar.app.action.impl.layout.TGSetMultitrackViewAction;
import app.tuxguitar.app.action.impl.layout.TGSetPageLayoutAction;
import app.tuxguitar.app.action.impl.layout.TGSetScoreEnabledAction;
import app.tuxguitar.app.action.impl.layout.TGSetTablatureEnabledAction;
import app.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import app.tuxguitar.app.action.impl.view.TGToggleEditToolbarAction;
import app.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import app.tuxguitar.app.action.impl.view.TGToggleMainToolbarAction;
import app.tuxguitar.app.action.impl.view.TGToggleMatrixEditorAction;
import app.tuxguitar.app.action.impl.view.TGTogglePianoEditorAction;
import app.tuxguitar.app.action.impl.view.TGToggleTableViewerAction;
import app.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.component.table.TGTableViewer;
import app.tuxguitar.app.view.dialog.transport.TGTransportDialog;
import app.tuxguitar.app.view.menu.TGMenuItem;
import app.tuxguitar.app.view.toolbar.edit.TGEditToolBar;
import app.tuxguitar.app.view.toolbar.main.TGMainToolBar;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.graphics.control.TGLayoutHorizontal;
import app.tuxguitar.graphics.control.TGLayoutVertical;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.menu.UIMenuCheckableItem;
import app.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class ViewMenuItem extends TGMenuItem {

	private UIMenuSubMenuItem layoutMenuItem;
	private UIMenuCheckableItem showMainToolbar;
	private UIMenuCheckableItem showEditToolbar;
	private UIMenuCheckableItem showTableViewer;
	private UIMenuCheckableItem showInstruments;
	private UIMenuCheckableItem showTransport;
	private UIMenuCheckableItem showFretBoard;
	private UIMenuCheckableItem showPiano;
	private UIMenuCheckableItem showMatrix;
	private UIMenuCheckableItem pageLayout;
	private UIMenuCheckableItem linearLayout;
	private UIMenuCheckableItem multitrack;
	private UIMenuCheckableItem scoreEnabled;
	private UIMenuCheckableItem tablatureEnabled;
	private UIMenuCheckableItem compact;
	private UIMenuActionItem zoomIn;
	private UIMenuActionItem zoomOut;
	private UIMenuActionItem zoomReset;

	private UIMenuSubMenuItem chordMenuItem;
	private UIMenuCheckableItem chordName;
	private UIMenuCheckableItem chordDiagram;

	public ViewMenuItem(UIMenu parent) {
		this.layoutMenuItem = parent.createSubMenuItem();
	}

	public void showItems(){
		//--TOOLBARS--
		this.showMainToolbar = this.layoutMenuItem.getMenu().createCheckItem();
		this.showMainToolbar.addSelectionListener(this.createActionProcessor(TGToggleMainToolbarAction.NAME));

		//--EDIT TOOLBAR--
		this.showEditToolbar = this.layoutMenuItem.getMenu().createCheckItem();
		this.showEditToolbar.addSelectionListener(this.createActionProcessor(TGToggleEditToolbarAction.NAME));

		//--TRACKS TABLE--
		this.showTableViewer = this.layoutMenuItem.getMenu().createCheckItem();
		this.showTableViewer.addSelectionListener(this.createActionProcessor(TGToggleTableViewerAction.NAME));
		this.layoutMenuItem.getMenu().createSeparator();

		//--INSTRUMENTS--
		this.showInstruments = this.layoutMenuItem.getMenu().createCheckItem();
		this.showInstruments.addSelectionListener(this.createActionProcessor(TGToggleChannelsDialogAction.NAME));

		//--TRANSPORT--
		this.showTransport = this.layoutMenuItem.getMenu().createCheckItem();
		this.showTransport.addSelectionListener(this.createActionProcessor(TGToggleTransportDialogAction.NAME));

		//--FRETBOARD--
		this.showFretBoard = this.layoutMenuItem.getMenu().createCheckItem();
		this.showFretBoard.addSelectionListener(this.createActionProcessor(TGToggleFretBoardEditorAction.NAME));

		//--PIANO--
		this.showPiano = this.layoutMenuItem.getMenu().createCheckItem();
		this.showPiano.addSelectionListener(this.createActionProcessor(TGTogglePianoEditorAction.NAME));

		//--MATRIX--
		this.showMatrix = this.layoutMenuItem.getMenu().createCheckItem();
		this.showMatrix.addSelectionListener(this.createActionProcessor(TGToggleMatrixEditorAction.NAME));

		this.layoutMenuItem.getMenu().createSeparator();

		//--PAGE LAYOUT--
		this.pageLayout = this.layoutMenuItem.getMenu().createRadioItem();
		this.pageLayout.addSelectionListener(this.createActionProcessor(TGSetPageLayoutAction.NAME));

		//--LINEAR LAYOUT--
		this.linearLayout = this.layoutMenuItem.getMenu().createRadioItem();
		this.linearLayout.addSelectionListener(this.createActionProcessor(TGSetLinearLayoutAction.NAME));

		//--MULTITRACK--
		this.multitrack = this.layoutMenuItem.getMenu().createCheckItem();
		this.multitrack.addSelectionListener(this.createActionProcessor(TGSetMultitrackViewAction.NAME));

		//--SCORE--
		this.scoreEnabled = this.layoutMenuItem.getMenu().createCheckItem();
		this.scoreEnabled.addSelectionListener(this.createActionProcessor(TGSetScoreEnabledAction.NAME));

		//--TABLATURE--
		this.tablatureEnabled = this.layoutMenuItem.getMenu().createCheckItem();
		this.tablatureEnabled.addSelectionListener(this.createActionProcessor(TGSetTablatureEnabledAction.NAME));

		//--COMPACT--
		this.compact = this.layoutMenuItem.getMenu().createCheckItem();
		this.compact.addSelectionListener(this.createActionProcessor(TGSetCompactViewAction.NAME));

		this.layoutMenuItem.getMenu().createSeparator();

		//--CHORD STYLE--
		this.chordMenuItem = this.layoutMenuItem.getMenu().createSubMenuItem();

		this.chordName = this.chordMenuItem.getMenu().createCheckItem();
		this.chordName.addSelectionListener(this.createActionProcessor(TGSetChordNameEnabledAction.NAME));

		this.chordDiagram = this.chordMenuItem.getMenu().createCheckItem();
		this.chordDiagram.addSelectionListener(this.createActionProcessor(TGSetChordDiagramEnabledAction.NAME));

		this.layoutMenuItem.getMenu().createSeparator();

		//--ZOOM IN--
		this.zoomIn = this.layoutMenuItem.getMenu().createActionItem();
		this.zoomIn.addSelectionListener(this.createActionProcessor(TGSetLayoutScaleIncrementAction.NAME));

		//--ZOOM OUT--
		this.zoomOut = this.layoutMenuItem.getMenu().createActionItem();
		this.zoomOut.addSelectionListener(this.createActionProcessor(TGSetLayoutScaleDecrementAction.NAME));

		//--ZOOM RESET--
		this.zoomReset = this.layoutMenuItem.getMenu().createActionItem();
		this.zoomReset.addSelectionListener(this.createActionProcessor(TGSetLayoutScaleResetAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void update() {
		Tablature tablature = TablatureEditor.getInstance(this.findContext()).getTablature();
		int style = tablature.getViewLayout().getStyle();
		this.showMainToolbar.setChecked(TGMainToolBar.getInstance(this.findContext()).isVisible());
		this.showEditToolbar.setChecked(TGEditToolBar.getInstance(this.findContext()).isVisible());
		this.showTableViewer.setChecked(TGTableViewer.getInstance(this.findContext()).isVisible());
		this.showInstruments.setChecked(!TuxGuitar.getInstance().getChannelManager().isDisposed());
		this.showTransport.setChecked(!TGTransportDialog.getInstance(this.findContext()).isDisposed());
		this.showFretBoard.setChecked(TuxGuitar.getInstance().getFretBoardEditor().isVisible());
		this.showPiano.setChecked(!TuxGuitar.getInstance().getPianoEditor().isDisposed());
		this.showMatrix.setChecked(!TuxGuitar.getInstance().getMatrixEditor().isDisposed());
		this.pageLayout.setChecked(tablature.getViewLayout() instanceof TGLayoutVertical);
		this.linearLayout.setChecked(tablature.getViewLayout() instanceof TGLayoutHorizontal);
		this.multitrack.setChecked( (style & TGLayout.DISPLAY_MULTITRACK) != 0 );
		this.scoreEnabled.setChecked( (style & TGLayout.DISPLAY_SCORE) != 0 );
		this.tablatureEnabled.setChecked( (style & TGLayout.DISPLAY_TABLATURE) != 0 );
		this.compact.setChecked( (style & TGLayout.DISPLAY_COMPACT) != 0 );
		this.compact.setEnabled((style & TGLayout.DISPLAY_MULTITRACK) == 0 || tablature.getViewLayout().getSong().countTracks() == 1);
		this.chordName.setChecked( (style & TGLayout.DISPLAY_CHORD_NAME) != 0 );
		this.chordDiagram.setChecked( (style & TGLayout.DISPLAY_CHORD_DIAGRAM) != 0 );
		this.zoomReset.setEnabled(!Tablature.DEFAULT_SCALE.equals(tablature.getScale()));
	}

	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.layoutMenuItem, "view", null);
		setMenuItemTextAndAccelerator(this.showMainToolbar, "view.show-main-toolbar", TGToggleMainToolbarAction.NAME);
		setMenuItemTextAndAccelerator(this.showEditToolbar, "view.show-edit-toolbar", TGToggleMainToolbarAction.NAME);
		setMenuItemTextAndAccelerator(this.showTableViewer, "view.show-table-viewer", TGToggleTableViewerAction.NAME);
		setMenuItemTextAndAccelerator(this.showInstruments, "view.show-instruments", TGToggleChannelsDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.showTransport, "view.show-transport", TGToggleTransportDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.showFretBoard, "view.show-fretboard", TGToggleFretBoardEditorAction.NAME);
		setMenuItemTextAndAccelerator(this.showPiano, "view.show-piano", TGTogglePianoEditorAction.NAME);
		setMenuItemTextAndAccelerator(this.showMatrix, "view.show-matrix", TGToggleMatrixEditorAction.NAME);
		setMenuItemTextAndAccelerator(this.pageLayout, "view.layout.page", TGSetPageLayoutAction.NAME);
		setMenuItemTextAndAccelerator(this.linearLayout, "view.layout.linear", TGSetLinearLayoutAction.NAME);
		setMenuItemTextAndAccelerator(this.multitrack, "view.layout.multitrack", TGSetMultitrackViewAction.NAME);
		setMenuItemTextAndAccelerator(this.scoreEnabled, "view.layout.score-enabled", TGSetScoreEnabledAction.NAME);
		setMenuItemTextAndAccelerator(this.tablatureEnabled, "view.layout.tablature-enabled", TGSetTablatureEnabledAction.NAME);
		setMenuItemTextAndAccelerator(this.compact, "view.layout.compact", TGSetCompactViewAction.NAME);
		setMenuItemTextAndAccelerator(this.chordMenuItem, "view.layout.chord-style", null);
		setMenuItemTextAndAccelerator(this.chordName, "view.layout.chord-name", TGSetChordNameEnabledAction.NAME);
		setMenuItemTextAndAccelerator(this.chordDiagram, "view.layout.chord-diagram", TGSetChordDiagramEnabledAction.NAME);
		setMenuItemTextAndAccelerator(this.zoomIn, "view.zoom.in", TGSetLayoutScaleIncrementAction.NAME);
		setMenuItemTextAndAccelerator(this.zoomOut, "view.zoom.out", TGSetLayoutScaleDecrementAction.NAME);
		setMenuItemTextAndAccelerator(this.zoomReset, "view.zoom.reset", TGSetLayoutScaleResetAction.NAME);
	}

	public void loadIcons(){
		this.showMainToolbar.setImage(TuxGuitar.getInstance().getIconManager().getToolbarMain());
		this.showEditToolbar.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TOOLBAR_EDIT));
		this.showTableViewer.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TABLE_VIEWER));
		this.showInstruments.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.INSTRUMENTS));
		this.showTransport.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRANSPORT));
		this.showFretBoard.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.FRETBOARD));
		this.showPiano.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.PIANO));
		this.showMatrix.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MATRIX));
		this.pageLayout.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.LAYOUT_PAGE));
		this.linearLayout.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.LAYOUT_LINEAR));
		this.multitrack.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.LAYOUT_MULTITRACK));
		this.scoreEnabled.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.LAYOUT_SCORE));
		this.tablatureEnabled.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.LAYOUT_TABLATURE));
		this.compact.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.LAYOUT_COMPACT));
		this.zoomIn.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ZOOM_IN));
		this.zoomOut.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ZOOM_OUT));
		this.zoomReset.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ZOOM_RESET));
	}
}
