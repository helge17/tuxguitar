/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.layout.SetChordDiagramEnabledAction;
import org.herac.tuxguitar.gui.actions.layout.SetChordNameEnabledAction;
import org.herac.tuxguitar.gui.actions.layout.SetCompactViewAction;
import org.herac.tuxguitar.gui.actions.layout.SetLinearLayoutAction;
import org.herac.tuxguitar.gui.actions.layout.SetMultitrackViewAction;
import org.herac.tuxguitar.gui.actions.layout.SetPageLayoutAction;
import org.herac.tuxguitar.gui.actions.layout.SetScoreEnabledAction;
import org.herac.tuxguitar.gui.actions.layout.SetTablatureEnabledAction;
import org.herac.tuxguitar.gui.actions.view.ShowFretBoardAction;
import org.herac.tuxguitar.gui.actions.view.ShowMatrixAction;
import org.herac.tuxguitar.gui.actions.view.ShowMixerAction;
import org.herac.tuxguitar.gui.actions.view.ShowPianoAction;
import org.herac.tuxguitar.gui.actions.view.ShowTransportAction;
import org.herac.tuxguitar.gui.editors.tab.layout.LinearViewLayout;
import org.herac.tuxguitar.gui.editors.tab.layout.PageViewLayout;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.gui.items.MenuItems;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewMenuItem implements MenuItems{
	
	private Menu menu;
	private Menu chordMenu;
	private MenuItem layoutMenuItem;
	private MenuItem showMixer;
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
		//--MIXER--
		this.showMixer = new MenuItem(this.menu, SWT.CHECK);
		this.showMixer.addSelectionListener(TuxGuitar.instance().getAction(ShowMixerAction.NAME));
		
		//--TRANSPORT--
		this.showTransport = new MenuItem(this.menu, SWT.CHECK);
		this.showTransport.addSelectionListener(TuxGuitar.instance().getAction(ShowTransportAction.NAME));
		
		//--FRETBOARD--
		this.showFretBoard = new MenuItem(this.menu, SWT.CHECK);
		this.showFretBoard.addSelectionListener(TuxGuitar.instance().getAction(ShowFretBoardAction.NAME));
		
		//--PIANO--
		this.showPiano = new MenuItem(this.menu, SWT.CHECK);
		this.showPiano.addSelectionListener(TuxGuitar.instance().getAction(ShowPianoAction.NAME));
		
		//--MATRIX--
		this.showMatrix = new MenuItem(this.menu, SWT.CHECK);
		this.showMatrix.addSelectionListener(TuxGuitar.instance().getAction(ShowMatrixAction.NAME));
		
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--PAGE LAYOUT--
		this.pageLayout = new MenuItem(this.menu, SWT.RADIO);
		this.pageLayout.addSelectionListener(TuxGuitar.instance().getAction(SetPageLayoutAction.NAME));
		
		//--LINEAR LAYOUT--
		this.linearLayout = new MenuItem(this.menu, SWT.RADIO);
		this.linearLayout.addSelectionListener(TuxGuitar.instance().getAction(SetLinearLayoutAction.NAME));
		
		//--MULTITRACK--
		this.multitrack = new MenuItem(this.menu, SWT.CHECK);
		this.multitrack.addSelectionListener(TuxGuitar.instance().getAction(SetMultitrackViewAction.NAME));
		
		//--SCORE
		this.scoreEnabled = new MenuItem(this.menu, SWT.CHECK);
		this.scoreEnabled.addSelectionListener(TuxGuitar.instance().getAction(SetScoreEnabledAction.NAME));
		
		//--SCORE
		this.tablatureEnabled = new MenuItem(this.menu, SWT.CHECK);
		this.tablatureEnabled.addSelectionListener(TuxGuitar.instance().getAction(SetTablatureEnabledAction.NAME));
		
		//--COMPACT
		this.compact = new MenuItem(this.menu, SWT.CHECK);
		this.compact.addSelectionListener(TuxGuitar.instance().getAction(SetCompactViewAction.NAME));
		
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--CHORD STYLE
		this.chordMenuItem = new MenuItem(this.menu,SWT.CASCADE);
		this.chordMenu = new Menu(this.menu.getShell(), SWT.DROP_DOWN);
		
		this.chordName = new MenuItem(this.chordMenu, SWT.CHECK);
		this.chordName.addSelectionListener(TuxGuitar.instance().getAction(SetChordNameEnabledAction.NAME));
		
		this.chordDiagram = new MenuItem(this.chordMenu, SWT.CHECK);
		this.chordDiagram.addSelectionListener(TuxGuitar.instance().getAction(SetChordDiagramEnabledAction.NAME));
		
		this.chordMenuItem.setMenu(this.chordMenu);
		this.layoutMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		ViewLayout layout = TuxGuitar.instance().getTablatureEditor().getTablature().getViewLayout();
		int style = layout.getStyle();
		this.showMixer.setSelection(!TuxGuitar.instance().getMixer().isDisposed());
		this.showTransport.setSelection(!TuxGuitar.instance().getTransport().isDisposed());
		this.showFretBoard.setSelection(TuxGuitar.instance().getFretBoardEditor().isVisible());
		this.showPiano.setSelection(!TuxGuitar.instance().getPianoEditor().isDisposed());
		this.showMatrix.setSelection(!TuxGuitar.instance().getMatrixEditor().isDisposed());
		this.pageLayout.setSelection(layout instanceof PageViewLayout);
		this.linearLayout.setSelection(layout instanceof LinearViewLayout);
		this.multitrack.setSelection( (style & ViewLayout.DISPLAY_MULTITRACK) != 0 );
		this.scoreEnabled.setSelection( (style & ViewLayout.DISPLAY_SCORE) != 0 );
		this.tablatureEnabled.setSelection( (style & ViewLayout.DISPLAY_TABLATURE) != 0 );
		this.compact.setSelection( (style & ViewLayout.DISPLAY_COMPACT) != 0 );
		this.compact.setEnabled((style & ViewLayout.DISPLAY_MULTITRACK) == 0 || layout.getSongManager().getSong().countTracks() == 1);
		this.chordName.setSelection( (style & ViewLayout.DISPLAY_CHORD_NAME) != 0 );
		this.chordDiagram.setSelection( (style & ViewLayout.DISPLAY_CHORD_DIAGRAM) != 0 );
	}
	
	public void loadProperties(){
		this.layoutMenuItem.setText(TuxGuitar.getProperty("view"));
		this.showMixer.setText(TuxGuitar.getProperty("view.show-mixer"));
		this.showTransport.setText(TuxGuitar.getProperty("view.show-transport"));
		this.showFretBoard.setText(TuxGuitar.getProperty("view.show-fretboard"));
		this.showPiano.setText(TuxGuitar.getProperty("view.show-piano"));
		this.showMatrix.setText(TuxGuitar.getProperty("view.show-matrix"));
		this.pageLayout.setText(TuxGuitar.getProperty("view.layout.page"));
		this.linearLayout.setText(TuxGuitar.getProperty("view.layout.linear"));
		this.multitrack.setText(TuxGuitar.getProperty("view.layout.multitrack"));
		this.scoreEnabled.setText(TuxGuitar.getProperty("view.layout.score-enabled"));
		this.tablatureEnabled.setText(TuxGuitar.getProperty("view.layout.tablature-enabled"));
		this.compact.setText(TuxGuitar.getProperty("view.layout.compact"));
		this.chordMenuItem.setText(TuxGuitar.getProperty("view.layout.chord-style"));
		this.chordName.setText(TuxGuitar.getProperty("view.layout.chord-name"));
		this.chordDiagram.setText(TuxGuitar.getProperty("view.layout.chord-diagram"));
	}
	
	public void loadIcons(){
		//Nothing to do
	}
}
