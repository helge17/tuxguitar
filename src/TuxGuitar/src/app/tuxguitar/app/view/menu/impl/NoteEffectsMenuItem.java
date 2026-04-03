package app.tuxguitar.app.view.menu.impl;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.effects.TGOpenBendDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenGraceDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenHarmonicDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenTremoloBarDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenTremoloPickingDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenTrillDialogAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.menu.TGMenuItem;
import app.tuxguitar.editor.action.effect.TGChangeAccentuatedNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeDeadNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeFadeInAction;
import app.tuxguitar.editor.action.effect.TGChangeGhostNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHammerNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHeavyAccentuatedNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeLetRingAction;
import app.tuxguitar.editor.action.effect.TGChangePalmMuteAction;
import app.tuxguitar.editor.action.effect.TGChangePoppingAction;
import app.tuxguitar.editor.action.effect.TGChangeSlappingAction;
import app.tuxguitar.editor.action.effect.TGChangeSlideNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeStaccatoAction;
import app.tuxguitar.editor.action.effect.TGChangeTappingAction;
import app.tuxguitar.editor.action.effect.TGChangeVibratoNoteAction;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuCheckableItem;
import app.tuxguitar.ui.menu.UIMenuSubMenuItem;
import app.tuxguitar.util.TGNoteRange;

public class NoteEffectsMenuItem extends TGMenuItem {

	private UIMenuSubMenuItem noteEffectsMenuItem;
	private UIMenuCheckableItem vibrato;
	private UIMenuCheckableItem bend;
	private UIMenuCheckableItem tremoloBar;
	private UIMenuCheckableItem deadNote;
	private UIMenuCheckableItem slide;
	private UIMenuCheckableItem hammer;
	private UIMenuCheckableItem ghostNote;
	private UIMenuCheckableItem accentuatedNote;
	private UIMenuCheckableItem heavyAccentuatedNote;
	private UIMenuCheckableItem letRing;
	private UIMenuCheckableItem harmonicNote;
	private UIMenuCheckableItem graceNote;
	private UIMenuCheckableItem trill;
	private UIMenuCheckableItem tremoloPicking;
	private UIMenuCheckableItem palmMute;
	private UIMenuCheckableItem staccato;
	private UIMenuCheckableItem tapping;
	private UIMenuCheckableItem slapping;
	private UIMenuCheckableItem popping;
	private UIMenuCheckableItem fadeIn;

	public NoteEffectsMenuItem(UIMenuSubMenuItem noteEffectsMenuItem) {
		this.noteEffectsMenuItem = noteEffectsMenuItem;
	}

	public NoteEffectsMenuItem(UIMenu parent) {
		this(parent.createSubMenuItem());
	}

	public void showItems(){
		//--VIBRATO--
		this.vibrato = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.vibrato.addSelectionListener(this.createActionProcessor(TGChangeVibratoNoteAction.NAME));

		//--BEND--
		this.bend = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.bend.addSelectionListener(this.createActionProcessor(TGOpenBendDialogAction.NAME));

		//--BEND--
		this.tremoloBar = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.tremoloBar.addSelectionListener(this.createActionProcessor(TGOpenTremoloBarDialogAction.NAME));

		//--SLIDE--
		this.slide = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.slide.addSelectionListener(this.createActionProcessor(TGChangeSlideNoteAction.NAME));

		//--SLIDE--
		this.deadNote = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.deadNote.addSelectionListener(this.createActionProcessor(TGChangeDeadNoteAction.NAME));

		//--HAMMER--
		this.hammer = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.hammer.addSelectionListener(this.createActionProcessor(TGChangeHammerNoteAction.NAME));

		//--SEPARATOR--
		this.noteEffectsMenuItem.getMenu().createSeparator();

		//--GHOST NOTE--
		this.ghostNote = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.ghostNote.addSelectionListener(this.createActionProcessor(TGChangeGhostNoteAction.NAME));

		//--ACCENTUATED NOTE--
		this.accentuatedNote = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.accentuatedNote.addSelectionListener(this.createActionProcessor(TGChangeAccentuatedNoteAction.NAME));

		//--HEAVY ACCENTUATED NOTE--
		this.heavyAccentuatedNote = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.heavyAccentuatedNote.addSelectionListener(this.createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME));

		//--LET RING--
		this.letRing = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.letRing.addSelectionListener(this.createActionProcessor(TGChangeLetRingAction.NAME));

		//--HARMONIC NOTE--
		this.harmonicNote = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.harmonicNote.addSelectionListener(this.createActionProcessor(TGOpenHarmonicDialogAction.NAME));

		//--GRACE NOTE--
		this.graceNote = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.graceNote.addSelectionListener(this.createActionProcessor(TGOpenGraceDialogAction.NAME));

		//--SEPARATOR--
		this.noteEffectsMenuItem.getMenu().createSeparator();

		//--TRILL--
		this.trill = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.trill.addSelectionListener(this.createActionProcessor(TGOpenTrillDialogAction.NAME));

		//--TREMOLO PICKING--
		this.tremoloPicking = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.tremoloPicking.addSelectionListener(this.createActionProcessor(TGOpenTremoloPickingDialogAction.NAME));

		//--PALM MUTE--
		this.palmMute = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.palmMute.addSelectionListener(this.createActionProcessor(TGChangePalmMuteAction.NAME));

		//--STACCATO--
		this.staccato = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.staccato.addSelectionListener(this.createActionProcessor(TGChangeStaccatoAction.NAME));

		//--SEPARATOR--
		this.noteEffectsMenuItem.getMenu().createSeparator();

		//--TAPPING--
		this.tapping = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.tapping.addSelectionListener(this.createActionProcessor(TGChangeTappingAction.NAME));

		//--SLAPPING--
		this.slapping = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.slapping.addSelectionListener(this.createActionProcessor(TGChangeSlappingAction.NAME));

		//--POPPING--
		this.popping = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.popping.addSelectionListener(this.createActionProcessor(TGChangePoppingAction.NAME));

		//--SEPARATOR--
		this.noteEffectsMenuItem.getMenu().createSeparator();

		//--FADE IN--
		this.fadeIn = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.fadeIn.addSelectionListener(this.createActionProcessor(TGChangeFadeInAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void setEnabled(boolean enabled) {
		this.noteEffectsMenuItem.setEnabled(enabled);
	}

	public void update(){
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		TGNoteRange noteRange = TablatureEditor.getInstance(this.findContext()).getTablature().getCurrentNoteRange();
		boolean isPercussion = TablatureEditor.getInstance(this.findContext()).getTablature().getCaret().getTrack().isPercussion();

		this.vibrato.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isVibrato()));
		this.vibrato.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.bend.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isBend()));
		this.bend.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.tremoloBar.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isTremoloBar()));
		this.tremoloBar.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.deadNote.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isDeadNote()));
		this.deadNote.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.slide.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isSlide()));
		this.slide.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.hammer.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isHammer()));
		this.hammer.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.ghostNote.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isGhostNote()));
		this.ghostNote.setEnabled(!running && !noteRange.isEmpty());
		this.accentuatedNote.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isAccentuatedNote()));
		this.accentuatedNote.setEnabled(!running && !noteRange.isEmpty());
		this.heavyAccentuatedNote.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isHeavyAccentuatedNote()));
		this.heavyAccentuatedNote.setEnabled(!running && !noteRange.isEmpty());
		this.letRing.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isLetRing()));
		this.letRing.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.harmonicNote.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isHarmonic()));
		this.harmonicNote.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.graceNote.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isGrace()));
		this.graceNote.setEnabled(!running && !noteRange.isEmpty());
		this.trill.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isTrill()));
		this.trill.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.tremoloPicking.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isTremoloPicking()));
		this.tremoloPicking.setEnabled(!running && !noteRange.isEmpty());
		this.palmMute.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isPalmMute()));
		this.palmMute.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.staccato.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isStaccato()));
		this.staccato.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.tapping.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isTapping()));
		this.tapping.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.slapping.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isSlapping()));
		this.slapping.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.popping.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isPopping()));
		this.popping.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.fadeIn.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isFadeIn()));
		this.fadeIn.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
	}

	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.noteEffectsMenuItem, "effects", null);
		setMenuItemTextAndAccelerator(this.vibrato, "effects.vibrato", TGChangeVibratoNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.bend, "effects.bend", TGOpenBendDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.tremoloBar, "effects.tremolo-bar", TGOpenTremoloBarDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.deadNote, "effects.deadnote", TGChangeDeadNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.slide, "effects.slide", TGChangeSlideNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.hammer, "effects.hammer", TGChangeHammerNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.ghostNote, "effects.ghostnote", TGChangeGhostNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.accentuatedNote, "effects.accentuatednote", TGChangeAccentuatedNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.heavyAccentuatedNote, "effects.heavyaccentuatednote", TGChangeHeavyAccentuatedNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.letRing, "effects.let-ring", TGChangeLetRingAction.NAME);
		setMenuItemTextAndAccelerator(this.harmonicNote, "effects.harmonic", TGOpenHarmonicDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.graceNote, "effects.grace", TGOpenGraceDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.trill, "effects.trill", TGOpenTrillDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.tremoloPicking, "effects.tremolo-picking", TGOpenTremoloPickingDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.palmMute, "effects.palm-mute", TGChangePalmMuteAction.NAME);
		setMenuItemTextAndAccelerator(this.staccato, "effects.staccato", TGChangeStaccatoAction.NAME);
		setMenuItemTextAndAccelerator(this.tapping, "effects.tapping", TGChangeTappingAction.NAME);
		setMenuItemTextAndAccelerator(this.slapping, "effects.slapping", TGChangeSlappingAction.NAME);
		setMenuItemTextAndAccelerator(this.popping, "effects.popping", TGChangePoppingAction.NAME);
		setMenuItemTextAndAccelerator(this.fadeIn, "effects.fade-in", TGChangeFadeInAction.NAME);
	}

	public void loadIcons(){
		this.vibrato.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_VIBRATO));
		this.bend.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_BEND));
		this.tremoloBar.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_TREMOLO_BAR));
		this.deadNote.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_DEAD));
		this.slide.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_SLIDE));
		this.hammer.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_HAMMER));
		this.ghostNote.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_GHOST));
		this.accentuatedNote.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_ACCENTUATED));
		this.heavyAccentuatedNote.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_HEAVY_ACCENTUATED));
		this.letRing.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_LET_RING));
		this.harmonicNote.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_HARMONIC));
		this.graceNote.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_GRACE));
		this.trill.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_TRILL));
		this.tremoloPicking.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_TREMOLO_PICKING));
		this.palmMute.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_PALM_MUTE));
		this.staccato.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_STACCATO));
		this.tapping.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_TAPPING));
		this.slapping.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_SLAPPING));
		this.popping.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_POPPING));
		this.fadeIn.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.EFFECT_FADE_IN));
	}
}
