package org.herac.tuxguitar.app.view.toolbar.edit;

import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenChordDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenTextDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenStrokeUpDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenStrokeDownDialogAction;
import org.herac.tuxguitar.app.view.dialog.chord.TGCustomChordManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.note.TGChangePickStrokeUpAction;
import org.herac.tuxguitar.editor.action.note.TGChangePickStrokeDownAction;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGInsertChordAction;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGPickStroke;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuItem;
import org.herac.tuxguitar.ui.toolbar.UIToolActionMenuItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;

public class TGEditToolBarSectionBeat extends TGEditToolBarSection {
	
	private static final String SECTION_TITLE = "beat";
	
	private UIToolActionMenuItem chordMenu;
	private UIToolCheckableItem text;
	private UIToolCheckableItem strokeUp;
	private UIToolCheckableItem strokeDown;
	private UIToolCheckableItem pickStrokeUp;
	private UIToolCheckableItem pickStrokeDown;
	
	public TGEditToolBarSectionBeat(TGEditToolBar toolBar) {
		super(toolBar, SECTION_TITLE);
	}
	
	public void createSectionToolBars() {
		UIToolBar toolBar = this.createToolBar();
		
		this.chordMenu = toolBar.createActionMenuItem();
		this.chordMenu.addSelectionListener(this.createActionProcessor(TGOpenChordDialogAction.NAME));

		toolBar.createSeparator();

		this.text = toolBar.createCheckItem();
		this.text.addSelectionListener(this.createActionProcessor(TGOpenTextDialogAction.NAME));

		toolBar.createSeparator();

		this.strokeUp = toolBar.createCheckItem();
		this.strokeUp.addSelectionListener(this.createActionProcessor(TGOpenStrokeUpDialogAction.NAME));

		this.strokeDown = toolBar.createCheckItem();
		this.strokeDown.addSelectionListener(this.createActionProcessor(TGOpenStrokeDownDialogAction.NAME));

		toolBar = this.createToolBar();
		this.pickStrokeUp = toolBar.createCheckItem();
		this.pickStrokeUp.addSelectionListener(this.createActionProcessor(TGChangePickStrokeUpAction.NAME));

		this.pickStrokeDown = toolBar.createCheckItem();
		this.pickStrokeDown.addSelectionListener(this.createActionProcessor(TGChangePickStrokeDownAction.NAME));
	}
	
	public void loadSectionProperties() {
		this.chordMenu.setToolTipText(this.getText("insert.chord"));
		this.text.setToolTipText(this.getText("text.insert"));
		this.strokeUp.setToolTipText(this.getText("beat.stroke-up"));
		this.strokeDown.setToolTipText(this.getText("beat.stroke-down"));
		this.pickStrokeUp.setToolTipText(this.getText("beat.pick-stroke-up"));
		this.pickStrokeDown.setToolTipText(this.getText("beat.pick-stroke-down"));
	}
	
	public void loadSectionIcons() {
		this.chordMenu.setImage(this.getIconManager().getChord());
		this.text.setImage(this.getIconManager().getText());
		this.strokeUp.setImage(this.getIconManager().getStrokeUp());
		this.strokeDown.setImage(this.getIconManager().getStrokeDown());
		this.pickStrokeUp.setImage(this.getIconManager().getPickStrokeUp());
		this.pickStrokeDown.setImage(this.getIconManager().getPickStrokeDown());
	}
	
	public void updateSectionItems() {
		TGBeat beat = this.getTablature().getCaret().getSelectedBeat();
		boolean isPercussion = this.getTablature().getCaret().getTrack().isPercussion();

		boolean running = MidiPlayer.getInstance(this.getToolBar().getContext()).isRunning();
		
		this.chordMenu.setEnabled(!running && !isPercussion);
		this.text.setEnabled(!running);
		this.text.setChecked(beat.isTextBeat());
		this.strokeUp.setEnabled(!running && !beat.isRestBeat() && !isPercussion);
		this.strokeUp.setChecked( beat != null && beat.getStroke().getDirection() == TGStroke.STROKE_UP );
		this.strokeDown.setEnabled(!running && !beat.isRestBeat() && !isPercussion);
		this.strokeDown.setChecked( beat != null && beat.getStroke().getDirection() == TGStroke.STROKE_DOWN );
		this.pickStrokeUp.setEnabled(!running && !beat.isRestBeat());
		this.pickStrokeUp.setChecked( beat != null && beat.getPickStroke().getDirection() == TGPickStroke.PICK_STROKE_UP );
		this.pickStrokeDown.setEnabled(!running && !beat.isRestBeat());
		this.pickStrokeDown.setChecked( beat != null && beat.getPickStroke().getDirection() == TGPickStroke.PICK_STROKE_DOWN );
		this.updateMenuItems();
	}
	
	public void updateMenuItems() {
		TGCustomChordManager customChordManager = TuxGuitar.getInstance().getCustomChordManager();
		UIMenu uiMenu = this.chordMenu.getMenu(); 
		List<UIMenuItem> uiMenuItems = this.chordMenu.getMenu().getItems();
		for(int i = customChordManager.countChords(); i < uiMenuItems.size(); i++) {
			uiMenuItems.get(i).dispose();
		}
		
		for(int i = 0; i < customChordManager.countChords(); i++) {
			TGChord chord = TuxGuitar.getInstance().getCustomChordManager().getChord(i);
			
			UIMenuItem uiMenuItem = (uiMenu.getItemCount() > i ? uiMenu.getItem(i) : null);
			if( uiMenuItem == null ) {
				UIMenuActionItem uiMenuActionItem = uiMenu.createActionItem();
				uiMenuActionItem.addSelectionListener(new UISelectionListener() {
					public void onSelect(UISelectionEvent event) {
						processInsertChordAction((TGChord) event.getComponent().getData(TGChord.class.getName()));
					}
				});
				uiMenuItem = uiMenuActionItem;
			}
			uiMenuItem.setText(chord.getName());
			uiMenuItem.setData(TGChord.class.getName(), chord);
		}
	}
	
	public void processInsertChordAction(TGChord chord) {
		TGActionProcessor tgActionProcessor = this.createActionProcessor(TGInsertChordAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHORD, chord);
		tgActionProcessor.process();
	}
}
