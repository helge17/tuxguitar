package org.herac.tuxguitar.app.view.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeEditionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeSelectionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetNaturalKeyAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice1Action;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice2Action;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.editor.action.edit.TGRedoAction;
import org.herac.tuxguitar.editor.action.edit.TGUndoAction;
import org.herac.tuxguitar.editor.undo.TGUndoableManager;
import org.herac.tuxguitar.player.base.MidiPlayer;

public class TGToolBarSectionEdit implements TGToolBarSection {
	
	private ToolItem undo;
	private ToolItem redo;
	private ToolItem menuItem;
	
	private Menu menu;
	private MenuItem voice1;
	private MenuItem voice2;
	private MenuItem modeSelection;
	private MenuItem modeEdition;
	private MenuItem notNaturalKey;
	
	public void createSection(final TGToolBar toolBar) {
		this.undo = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.undo.addSelectionListener(toolBar.createActionProcessor(TGUndoAction.NAME));
		
		this.redo = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.redo.addSelectionListener(toolBar.createActionProcessor(TGRedoAction.NAME));
		
		this.menuItem = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				displayMenu();
			}
		});
		
		this.menu = new Menu(this.menuItem.getParent().getShell());
		
		this.voice1 = new MenuItem(this.menu, SWT.PUSH);
		this.voice1.addSelectionListener(toolBar.createActionProcessor(TGSetVoice1Action.NAME));
		
		this.voice2 = new MenuItem(this.menu, SWT.PUSH);
		this.voice2.addSelectionListener(toolBar.createActionProcessor(TGSetVoice2Action.NAME));
		
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		this.modeSelection = new MenuItem(this.menu, SWT.PUSH);
		this.modeSelection.addSelectionListener(toolBar.createActionProcessor(TGSetMouseModeSelectionAction.NAME));
		
		this.modeEdition = new MenuItem(this.menu, SWT.PUSH);
		this.modeEdition.addSelectionListener(toolBar.createActionProcessor(TGSetMouseModeEditionAction.NAME));
		
		this.notNaturalKey = new MenuItem(this.menu, SWT.PUSH);
		this.notNaturalKey.addSelectionListener(toolBar.createActionProcessor(TGSetNaturalKeyAction.NAME));
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void updateItems(TGToolBar toolBar){
		TablatureEditor editor = TablatureEditor.getInstance(toolBar.getContext());
		EditorKit editorKit = editor.getTablature().getEditorKit();
		
		boolean running = MidiPlayer.getInstance(toolBar.getContext()).isRunning();
		TGUndoableManager undoableManager = TGUndoableManager.getInstance(toolBar.getContext());
		
		this.undo.setEnabled(!running && undoableManager.canUndo());
		this.redo.setEnabled(!running && undoableManager.canRedo());
		
		this.voice1.setText(toolBar.getText("edit.voice-1", (editor.getTablature().getCaret().getVoice() == 0)));
		this.voice1.setEnabled(!running);
		
		this.voice2.setText(toolBar.getText("edit.voice-2", (editor.getTablature().getCaret().getVoice() == 1)));
		this.voice2.setEnabled(!running);
		
		this.modeSelection.setText(toolBar.getText("edit.mouse-mode-selection", (editorKit.getMouseMode() == EditorKit.MOUSE_MODE_SELECTION)));
		this.modeSelection.setEnabled(!running);
		
		this.modeEdition.setText(toolBar.getText("edit.mouse-mode-edition", (editorKit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION)));
		this.modeEdition.setEnabled(!running);
		
		this.notNaturalKey.setText(toolBar.getText("edit.not-natural-key", (!editorKit.isNatural())));
		this.notNaturalKey.setEnabled(!running && editorKit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION);
	}
	
	public void loadProperties(TGToolBar toolBar){
		TablatureEditor editor = TablatureEditor.getInstance(toolBar.getContext());
		EditorKit editorKit = editor.getTablature().getEditorKit();
		
		this.undo.setToolTipText(toolBar.getText("edit.undo"));
		this.redo.setToolTipText(toolBar.getText("edit.redo"));
		this.menuItem.setToolTipText(toolBar.getText("edit"));
		
		this.voice1.setText(toolBar.getText("edit.voice-1", (editor.getTablature().getCaret().getVoice() == 0)));
		this.voice2.setText(toolBar.getText("edit.voice-2", (editor.getTablature().getCaret().getVoice() == 1)));
		this.modeSelection.setText(toolBar.getText("edit.mouse-mode-selection", (editorKit.getMouseMode() == EditorKit.MOUSE_MODE_SELECTION)));
		this.modeEdition.setText(toolBar.getText("edit.mouse-mode-edition", (editorKit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION)));
		this.notNaturalKey.setText(toolBar.getText("edit.not-natural-key", (!editorKit.isNatural())));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.undo.setImage(toolBar.getIconManager().getEditUndo());
		this.redo.setImage(toolBar.getIconManager().getEditRedo());
		this.menuItem.setImage(toolBar.getIconManager().getEditModeEdition());
		this.voice1.setImage(toolBar.getIconManager().getEditVoice1());
		this.voice2.setImage(toolBar.getIconManager().getEditVoice2());
		this.modeSelection.setImage(toolBar.getIconManager().getEditModeSelection());
		this.modeEdition.setImage(toolBar.getIconManager().getEditModeEdition());
		this.notNaturalKey.setImage(toolBar.getIconManager().getEditModeEditionNotNatural());
	}
	
	public void displayMenu() {
		Rectangle rect = this.menuItem.getBounds();
		Point pt = this.menuItem.getParent().toDisplay(new Point(rect.x, rect.y));
		
		this.menu.setLocation(pt.x, pt.y + rect.height);
		this.menu.setVisible(true);
	}
}
