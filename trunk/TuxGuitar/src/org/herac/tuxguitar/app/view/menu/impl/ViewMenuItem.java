package org.herac.tuxguitar.app.view.menu.impl;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.layout.TGSetChordDiagramEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetChordNameEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetCompactViewAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLinearLayoutAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetMultitrackViewAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetPageLayoutAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetScoreEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetTablatureEnabledAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleEditToolbarAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleMainToolbarAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleMatrixEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGTogglePianoEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import org.herac.tuxguitar.app.view.dialog.transport.TGTransportDialog;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.app.view.toolbar.edit.TGEditToolBar;
import org.herac.tuxguitar.app.view.toolbar.main.TGMainToolBar;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLayoutHorizontal;
import org.herac.tuxguitar.graphics.control.TGLayoutVertical;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class ViewMenuItem extends TGMenuItem {
	
	private UIMenuSubMenuItem layoutMenuItem;
	private UIMenuCheckableItem showMainToolbar;
	private UIMenuCheckableItem showEditToolbar;
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
		
		//--SCORE
		this.scoreEnabled = this.layoutMenuItem.getMenu().createCheckItem();
		this.scoreEnabled.addSelectionListener(this.createActionProcessor(TGSetScoreEnabledAction.NAME));
		
		//--SCORE
		this.tablatureEnabled = this.layoutMenuItem.getMenu().createCheckItem();
		this.tablatureEnabled.addSelectionListener(this.createActionProcessor(TGSetTablatureEnabledAction.NAME));
		
		//--COMPACT
		this.compact = this.layoutMenuItem.getMenu().createCheckItem();
		this.compact.addSelectionListener(this.createActionProcessor(TGSetCompactViewAction.NAME));
		
		this.layoutMenuItem.getMenu().createSeparator();
		
		//--CHORD STYLE
		this.chordMenuItem = this.layoutMenuItem.getMenu().createSubMenuItem();
		
		this.chordName = this.chordMenuItem.getMenu().createCheckItem();
		this.chordName.addSelectionListener(this.createActionProcessor(TGSetChordNameEnabledAction.NAME));
		
		this.chordDiagram = this.chordMenuItem.getMenu().createCheckItem();
		this.chordDiagram.addSelectionListener(this.createActionProcessor(TGSetChordDiagramEnabledAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		TGLayout layout = TuxGuitar.getInstance().getTablatureEditor().getTablature().getViewLayout();
		int style = layout.getStyle();
		this.showMainToolbar.setChecked(TGMainToolBar.getInstance(this.findContext()).isVisible());
		this.showEditToolbar.setChecked(TGEditToolBar.getInstance(this.findContext()).isVisible());
		this.showInstruments.setChecked(!TuxGuitar.getInstance().getChannelManager().isDisposed());
		this.showTransport.setChecked(!TGTransportDialog.getInstance(this.findContext()).isDisposed());
		this.showFretBoard.setChecked(TuxGuitar.getInstance().getFretBoardEditor().isVisible());
		this.showPiano.setChecked(!TuxGuitar.getInstance().getPianoEditor().isDisposed());
		this.showMatrix.setChecked(!TuxGuitar.getInstance().getMatrixEditor().isDisposed());
		this.pageLayout.setChecked(layout instanceof TGLayoutVertical);
		this.linearLayout.setChecked(layout instanceof TGLayoutHorizontal);
		this.multitrack.setChecked( (style & TGLayout.DISPLAY_MULTITRACK) != 0 );
		this.scoreEnabled.setChecked( (style & TGLayout.DISPLAY_SCORE) != 0 );
		this.tablatureEnabled.setChecked( (style & TGLayout.DISPLAY_TABLATURE) != 0 );
		this.compact.setChecked( (style & TGLayout.DISPLAY_COMPACT) != 0 );
		this.compact.setEnabled((style & TGLayout.DISPLAY_MULTITRACK) == 0 || layout.getSong().countTracks() == 1);
		this.chordName.setChecked( (style & TGLayout.DISPLAY_CHORD_NAME) != 0 );
		this.chordDiagram.setChecked( (style & TGLayout.DISPLAY_CHORD_DIAGRAM) != 0 );
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.layoutMenuItem, "view", null);
		setMenuItemTextAndAccelerator(this.showMainToolbar, "view.show-main-toolbar", TGToggleMainToolbarAction.NAME);
		setMenuItemTextAndAccelerator(this.showEditToolbar, "view.show-edit-toolbar", TGToggleMainToolbarAction.NAME);
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
	}
	
	public void loadIcons(){
		//Nothing to do
	}
}
