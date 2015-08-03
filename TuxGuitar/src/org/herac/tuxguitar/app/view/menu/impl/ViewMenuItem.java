/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.menu.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
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
import org.herac.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleMatrixEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGTogglePianoEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleToolbarsAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import org.herac.tuxguitar.app.view.dialog.transport.TGTransportDialog;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.app.view.toolbar.TGToolBar;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLayoutHorizontal;
import org.herac.tuxguitar.graphics.control.TGLayoutVertical;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewMenuItem extends TGMenuItem{
	
	private Menu menu;
	private Menu chordMenu;
	private MenuItem layoutMenuItem;
	private MenuItem showToolbars;
	private MenuItem showInstruments;
	private MenuItem showTransport;
	private MenuItem showFretBoard;
	private MenuItem showPiano;
	private MenuItem showMatrix;
	private MenuItem pageLayout;
	private MenuItem linearLayout;
	private MenuItem multitrack;
	private MenuItem scoreEnabled;
	private MenuItem tablatureEnabled;
	private MenuItem compact;
	
	private MenuItem chordMenuItem;
	private MenuItem chordName;
	private MenuItem chordDiagram;
	
	public ViewMenuItem(Shell shell,Menu parent, int style) {
		this.layoutMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems(){
		//--TOOLBARS--
		this.showToolbars = new MenuItem(this.menu, SWT.CHECK);
		this.showToolbars.addSelectionListener(this.createActionProcessor(TGToggleToolbarsAction.NAME));
		
		//--INSTRUMENTS--
		this.showInstruments = new MenuItem(this.menu, SWT.CHECK);
		this.showInstruments.addSelectionListener(this.createActionProcessor(TGToggleChannelsDialogAction.NAME));
		
		//--TRANSPORT--
		this.showTransport = new MenuItem(this.menu, SWT.CHECK);
		this.showTransport.addSelectionListener(this.createActionProcessor(TGToggleTransportDialogAction.NAME));
		
		//--FRETBOARD--
		this.showFretBoard = new MenuItem(this.menu, SWT.CHECK);
		this.showFretBoard.addSelectionListener(this.createActionProcessor(TGToggleFretBoardEditorAction.NAME));
		
		//--PIANO--
		this.showPiano = new MenuItem(this.menu, SWT.CHECK);
		this.showPiano.addSelectionListener(this.createActionProcessor(TGTogglePianoEditorAction.NAME));
		
		//--MATRIX--
		this.showMatrix = new MenuItem(this.menu, SWT.CHECK);
		this.showMatrix.addSelectionListener(this.createActionProcessor(TGToggleMatrixEditorAction.NAME));
		
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--PAGE LAYOUT--
		this.pageLayout = new MenuItem(this.menu, SWT.RADIO);
		this.pageLayout.addSelectionListener(this.createActionProcessor(TGSetPageLayoutAction.NAME));
		
		//--LINEAR LAYOUT--
		this.linearLayout = new MenuItem(this.menu, SWT.RADIO);
		this.linearLayout.addSelectionListener(this.createActionProcessor(TGSetLinearLayoutAction.NAME));
		
		//--MULTITRACK--
		this.multitrack = new MenuItem(this.menu, SWT.CHECK);
		this.multitrack.addSelectionListener(this.createActionProcessor(TGSetMultitrackViewAction.NAME));
		
		//--SCORE
		this.scoreEnabled = new MenuItem(this.menu, SWT.CHECK);
		this.scoreEnabled.addSelectionListener(this.createActionProcessor(TGSetScoreEnabledAction.NAME));
		
		//--SCORE
		this.tablatureEnabled = new MenuItem(this.menu, SWT.CHECK);
		this.tablatureEnabled.addSelectionListener(this.createActionProcessor(TGSetTablatureEnabledAction.NAME));
		
		//--COMPACT
		this.compact = new MenuItem(this.menu, SWT.CHECK);
		this.compact.addSelectionListener(this.createActionProcessor(TGSetCompactViewAction.NAME));
		
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--CHORD STYLE
		this.chordMenuItem = new MenuItem(this.menu,SWT.CASCADE);
		this.chordMenu = new Menu(this.menu.getShell(), SWT.DROP_DOWN);
		
		this.chordName = new MenuItem(this.chordMenu, SWT.CHECK);
		this.chordName.addSelectionListener(this.createActionProcessor(TGSetChordNameEnabledAction.NAME));
		
		this.chordDiagram = new MenuItem(this.chordMenu, SWT.CHECK);
		this.chordDiagram.addSelectionListener(this.createActionProcessor(TGSetChordDiagramEnabledAction.NAME));
		
		this.chordMenuItem.setMenu(this.chordMenu);
		this.layoutMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		TGLayout layout = TuxGuitar.getInstance().getTablatureEditor().getTablature().getViewLayout();
		int style = layout.getStyle();
		this.showToolbars.setSelection(TGToolBar.getInstance(this.findContext()).isVisible());
		this.showInstruments.setSelection(!TuxGuitar.getInstance().getChannelManager().isDisposed());
		this.showTransport.setSelection(!TGTransportDialog.getInstance(this.findContext()).isDisposed());
		this.showFretBoard.setSelection(TuxGuitar.getInstance().getFretBoardEditor().isVisible());
		this.showPiano.setSelection(!TuxGuitar.getInstance().getPianoEditor().isDisposed());
		this.showMatrix.setSelection(!TuxGuitar.getInstance().getMatrixEditor().isDisposed());
		this.pageLayout.setSelection(layout instanceof TGLayoutVertical);
		this.linearLayout.setSelection(layout instanceof TGLayoutHorizontal);
		this.multitrack.setSelection( (style & TGLayout.DISPLAY_MULTITRACK) != 0 );
		this.scoreEnabled.setSelection( (style & TGLayout.DISPLAY_SCORE) != 0 );
		this.tablatureEnabled.setSelection( (style & TGLayout.DISPLAY_TABLATURE) != 0 );
		this.compact.setSelection( (style & TGLayout.DISPLAY_COMPACT) != 0 );
		this.compact.setEnabled((style & TGLayout.DISPLAY_MULTITRACK) == 0 || layout.getSong().countTracks() == 1);
		this.chordName.setSelection( (style & TGLayout.DISPLAY_CHORD_NAME) != 0 );
		this.chordDiagram.setSelection( (style & TGLayout.DISPLAY_CHORD_DIAGRAM) != 0 );
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.layoutMenuItem, "view", null);
		setMenuItemTextAndAccelerator(this.showToolbars, "view.show-toolbars", TGToggleToolbarsAction.NAME);
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
