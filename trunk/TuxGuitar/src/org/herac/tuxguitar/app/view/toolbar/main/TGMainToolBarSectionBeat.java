package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;

public class TGMainToolBarSectionBeat extends TGMainToolBarSection {
	
	private UIToolCheckableItem tiedNote;
	
	public TGMainToolBarSectionBeat(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.tiedNote = this.getToolBar().getControl().createCheckItem();
		this.tiedNote.addSelectionListener(this.createActionProcessor(TGChangeTiedNoteAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		this.tiedNote.setToolTipText(this.getText("note.tiednote"));
	}
	
	public void loadIcons(){
		this.tiedNote.setImage(this.getIconManager().getNoteTied());
	}
	
	public void updateItems(){
		TGNote note = this.getTablature().getCaret().getSelectedNote();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		
		this.tiedNote.setEnabled(!running);
		this.tiedNote.setChecked(note != null && note.isTiedNote());
	}
}
