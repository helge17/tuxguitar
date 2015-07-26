package org.herac.tuxguitar.app.view.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import org.herac.tuxguitar.song.models.TGNote;

public class TGToolBarSectionBeat implements TGToolBarSection {
	
	private ToolItem tiedNote;
	
	public TGToolBarSectionBeat() {
		super();
	}
	
	public void createSection(final TGToolBar toolBar) {
		this.tiedNote = new ToolItem(toolBar.getControl(), SWT.CHECK);
		this.tiedNote.addSelectionListener(toolBar.createActionProcessor(TGChangeTiedNoteAction.NAME));
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar){
		this.tiedNote.setToolTipText(toolBar.getText("note.tiednote"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.tiedNote.setImage(toolBar.getIconManager().getNoteTied());
	}
	
	public void updateItems(TGToolBar toolBar){
		TGNote note = toolBar.getTablature().getCaret().getSelectedNote();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		
		this.tiedNote.setEnabled(!running);
		this.tiedNote.setSelection(note != null && note.isTiedNote());
	}
}
