package org.herac.tuxguitar.app.view.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
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
	
	public void createSection(final TGToolBar toolBar) {
		this.undo = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.undo.addSelectionListener(toolBar.createActionProcessor(TGUndoAction.NAME));
		
		this.redo = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.redo.addSelectionListener(toolBar.createActionProcessor(TGRedoAction.NAME));
		
		this.menuItem = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				createMenu(toolBar, (ToolItem) event.widget);
			}
		});
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void updateItems(TGToolBar toolBar){
		boolean running = MidiPlayer.getInstance(toolBar.getContext()).isRunning();
		TGUndoableManager undoableManager = TGUndoableManager.getInstance(toolBar.getContext());
		
		this.undo.setEnabled(!running && undoableManager.canUndo());
		this.redo.setEnabled(!running && undoableManager.canRedo());
	}
	
	public void loadProperties(TGToolBar toolBar){
		this.undo.setToolTipText(toolBar.getText("edit.undo"));
		this.redo.setToolTipText(toolBar.getText("edit.redo"));
		this.menuItem.setToolTipText(toolBar.getText("edit"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.undo.setImage(toolBar.getIconManager().getEditUndo());
		this.redo.setImage(toolBar.getIconManager().getEditRedo());
		this.menuItem.setImage(toolBar.getIconManager().getEditModeEdition());
	}
	
	public void createMenu(TGToolBar toolBar, ToolItem item) {
		TablatureEditor editor = TablatureEditor.getInstance(toolBar.getContext());
		EditorKit editorKit = editor.getTablature().getEditorKit();
		
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		
		Menu menu = new Menu(item.getParent().getShell());
		
		MenuItem voice1 = new MenuItem(menu, SWT.PUSH);
		voice1.addSelectionListener(toolBar.createActionProcessor(TGSetVoice1Action.NAME));
		voice1.setText(toolBar.getText("edit.voice-1", (editor.getTablature().getCaret().getVoice() == 0)));
		voice1.setImage(toolBar.getIconManager().getEditVoice1());
		voice1.setEnabled(!running);
		
		MenuItem voice2 = new MenuItem(menu, SWT.PUSH);
		voice2.addSelectionListener(toolBar.createActionProcessor(TGSetVoice2Action.NAME));
		voice2.setText(toolBar.getText("edit.voice-2", (editor.getTablature().getCaret().getVoice() == 1)));
		voice2.setImage(toolBar.getIconManager().getEditVoice2());
		voice2.setEnabled(!running);
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem modeSelection = new MenuItem(menu, SWT.PUSH);
		modeSelection.addSelectionListener(toolBar.createActionProcessor(TGSetMouseModeSelectionAction.NAME));
		modeSelection.setText(toolBar.getText("edit.mouse-mode-selection", (editorKit.getMouseMode() == EditorKit.MOUSE_MODE_SELECTION)));
		modeSelection.setImage(toolBar.getIconManager().getEditModeSelection());
		modeSelection.setEnabled(!running);
		
		MenuItem modeEdition = new MenuItem(menu, SWT.PUSH);
		modeEdition.addSelectionListener(toolBar.createActionProcessor(TGSetMouseModeEditionAction.NAME));
		modeEdition.setText(toolBar.getText("edit.mouse-mode-edition", (editorKit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION)));
		modeEdition.setImage(toolBar.getIconManager().getEditModeEdition());
		modeEdition.setEnabled(!running);
		
		MenuItem notNaturalKey = new MenuItem(menu, SWT.PUSH);
		notNaturalKey.addSelectionListener(toolBar.createActionProcessor(TGSetNaturalKeyAction.NAME));
		notNaturalKey.setText(toolBar.getText("edit.not-natural-key", (!editorKit.isNatural())));
		notNaturalKey.setImage(toolBar.getIconManager().getEditModeEditionNotNatural());
		notNaturalKey.setEnabled(!running && editorKit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION);
		
		Rectangle rect = item.getBounds();
		Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
		
		menu.setLocation(pt.x, pt.y + rect.height);
		menu.setVisible(true);
	}
}
