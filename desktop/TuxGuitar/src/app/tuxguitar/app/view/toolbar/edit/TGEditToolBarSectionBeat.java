package app.tuxguitar.app.view.toolbar.edit;

import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.insert.TGOpenChordDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenTextDialogAction;
import app.tuxguitar.app.action.impl.note.TGOpenStrokeUpDialogAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.action.impl.note.TGOpenStrokeDownDialogAction;
import app.tuxguitar.app.view.dialog.chord.TGCustomChordManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.note.TGChangePickStrokeUpAction;
import app.tuxguitar.editor.action.note.TGChangePickStrokeDownAction;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.note.TGInsertChordAction;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGPickStroke;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.menu.UIMenuItem;
import app.tuxguitar.ui.toolbar.UIToolActionMenuItem;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.toolbar.UIToolCheckableItem;

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
		this.pickStrokeDown = toolBar.createCheckItem();
		this.pickStrokeDown.addSelectionListener(this.createActionProcessor(TGChangePickStrokeDownAction.NAME));

		this.pickStrokeUp = toolBar.createCheckItem();
		this.pickStrokeUp.addSelectionListener(this.createActionProcessor(TGChangePickStrokeUpAction.NAME));
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
		this.chordMenu.setImage(this.getIconManager().getImageByName(TGIconManager.CHORD));
		this.text.setImage(this.getIconManager().getImageByName(TGIconManager.TEXT));
		this.strokeUp.setImage(this.getIconManager().getImageByName(TGIconManager.STROKE_UP));
		this.strokeDown.setImage(this.getIconManager().getImageByName(TGIconManager.STROKE_DOWN));
		this.pickStrokeUp.setImage(this.getIconManager().getImageByName(TGIconManager.PICK_STROKE_UP));
		this.pickStrokeDown.setImage(this.getIconManager().getImageByName(TGIconManager.PICK_STROKE_DOWN));
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
