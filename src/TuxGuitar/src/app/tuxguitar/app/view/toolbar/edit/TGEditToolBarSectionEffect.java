package app.tuxguitar.app.view.toolbar.edit;

import app.tuxguitar.editor.action.effect.TGChangeVibratoNoteAction;
import app.tuxguitar.app.action.impl.effects.TGOpenBendDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenTremoloBarDialogAction;
import app.tuxguitar.editor.action.effect.TGChangeSlideNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeDeadNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHammerNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeGhostNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeAccentuatedNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHeavyAccentuatedNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeLetRingAction;
import app.tuxguitar.app.action.impl.effects.TGOpenHarmonicDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenGraceDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenTrillDialogAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.action.impl.effects.TGOpenTremoloPickingDialogAction;
import app.tuxguitar.editor.action.effect.TGChangePalmMuteAction;
import app.tuxguitar.editor.action.effect.TGChangeStaccatoAction;
import app.tuxguitar.editor.action.effect.TGChangeTappingAction;
import app.tuxguitar.editor.action.effect.TGChangeSlappingAction;
import app.tuxguitar.editor.action.effect.TGChangePoppingAction;
import app.tuxguitar.editor.action.effect.TGChangeFadeInAction;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.toolbar.UIToolCheckableItem;
import app.tuxguitar.util.TGNoteRange;

public class TGEditToolBarSectionEffect extends TGEditToolBarSection {

	private static final String SECTION_TITLE = "effects";

	private UIToolCheckableItem vibrato;
	private UIToolCheckableItem bend;
	private UIToolCheckableItem tremoloBar;
	private UIToolCheckableItem slide;
	private UIToolCheckableItem deadNote;
	private UIToolCheckableItem hammer;
	private UIToolCheckableItem ghostNote;
	private UIToolCheckableItem accentuatedNote;
	private UIToolCheckableItem heavyAccentuatedNote;
	private UIToolCheckableItem letRing;
	private UIToolCheckableItem harmonicNote;
	private UIToolCheckableItem graceNote;
	private UIToolCheckableItem trill;
	private UIToolCheckableItem tremoloPicking;
	private UIToolCheckableItem palmMute;
	private UIToolCheckableItem staccato;
	private UIToolCheckableItem tapping;
	private UIToolCheckableItem slapping;
	private UIToolCheckableItem popping;
	private UIToolCheckableItem fadeIn;

	public TGEditToolBarSectionEffect(TGEditToolBar toolBar) {
		super(toolBar, SECTION_TITLE);
	}

	public void createSectionToolBars() {
		UIToolBar toolBar = this.createToolBar();

		//--VIBRATO--
		this.vibrato = toolBar.createCheckItem();
		this.vibrato.addSelectionListener(this.createActionProcessor(TGChangeVibratoNoteAction.NAME));

		//--BEND--
		this.bend = toolBar.createCheckItem();
		this.bend.addSelectionListener(this.createActionProcessor(TGOpenBendDialogAction.NAME));

		//--TREMOLO BAR--
		this.tremoloBar = toolBar.createCheckItem();
		this.tremoloBar.addSelectionListener(this.createActionProcessor(TGOpenTremoloBarDialogAction.NAME));

		//--SLIDE--
		this.slide = toolBar.createCheckItem();
		this.slide.addSelectionListener(this.createActionProcessor(TGChangeSlideNoteAction.NAME));

		//--DEAD NOTE--
		this.deadNote = toolBar.createCheckItem();
		this.deadNote.addSelectionListener(this.createActionProcessor(TGChangeDeadNoteAction.NAME));

		toolBar = this.createToolBar();

		//--HAMMER--
		this.hammer = toolBar.createCheckItem();
		this.hammer.addSelectionListener(this.createActionProcessor(TGChangeHammerNoteAction.NAME));

		//--GHOST NOTE--
		this.ghostNote = toolBar.createCheckItem();
		this.ghostNote.addSelectionListener(this.createActionProcessor(TGChangeGhostNoteAction.NAME));

		//--ACCENTUATED NOTE--
		this.accentuatedNote = toolBar.createCheckItem();
		this.accentuatedNote.addSelectionListener(this.createActionProcessor(TGChangeAccentuatedNoteAction.NAME));

		//--HEAVY ACCENTUATED NOTE--
		this.heavyAccentuatedNote = toolBar.createCheckItem();
		this.heavyAccentuatedNote.addSelectionListener(this.createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME));

		//LET RING--
		this.letRing = toolBar.createCheckItem();
		this.letRing.addSelectionListener(this.createActionProcessor(TGChangeLetRingAction.NAME));

		toolBar = this.createToolBar();

		//--HARMONIC NOTE--
		this.harmonicNote = toolBar.createCheckItem();
		this.harmonicNote.addSelectionListener(this.createActionProcessor(TGOpenHarmonicDialogAction.NAME));

		//--GRACE NOTE--
		this.graceNote = toolBar.createCheckItem();
		this.graceNote.addSelectionListener(this.createActionProcessor(TGOpenGraceDialogAction.NAME));

		//--TRILL--
		this.trill = toolBar.createCheckItem();
		this.trill.addSelectionListener(this.createActionProcessor(TGOpenTrillDialogAction.NAME));

		//--TREMOLO PICKING--
		this.tremoloPicking = toolBar.createCheckItem();
		this.tremoloPicking.addSelectionListener(this.createActionProcessor(TGOpenTremoloPickingDialogAction.NAME));

		//--PALM MUTE--
		this.palmMute = toolBar.createCheckItem();
		this.palmMute.addSelectionListener(this.createActionProcessor(TGChangePalmMuteAction.NAME));

		toolBar = this.createToolBar();

		//--STACCATO--
		this.staccato = toolBar.createCheckItem();
		this.staccato.addSelectionListener(this.createActionProcessor(TGChangeStaccatoAction.NAME));

		//--TAPPING--
		this.tapping = toolBar.createCheckItem();
		this.tapping.addSelectionListener(this.createActionProcessor(TGChangeTappingAction.NAME));

		//--SLAPPING--
		this.slapping = toolBar.createCheckItem();
		this.slapping.addSelectionListener(this.createActionProcessor(TGChangeSlappingAction.NAME));

		//--POPPING--
		this.popping = toolBar.createCheckItem();
		this.popping.addSelectionListener(this.createActionProcessor(TGChangePoppingAction.NAME));

		//--FADE IN--
		this.fadeIn = toolBar.createCheckItem();
		this.fadeIn.addSelectionListener(this.createActionProcessor(TGChangeFadeInAction.NAME));
	}

	public void loadSectionProperties() {
		this.vibrato.setToolTipText(this.getText("effects.vibrato"));
		this.bend.setToolTipText(this.getText("effects.bend"));
		this.tremoloBar.setToolTipText(this.getText("effects.tremolo-bar"));
		this.slide.setToolTipText(this.getText("effects.slide"));
		this.deadNote.setToolTipText(this.getText("effects.deadnote"));
		this.hammer.setToolTipText(this.getText("effects.hammer"));
		this.ghostNote.setToolTipText(this.getText("effects.ghostnote"));
		this.accentuatedNote.setToolTipText(this.getText("effects.accentuatednote"));
		this.heavyAccentuatedNote.setToolTipText(this.getText("effects.heavyaccentuatednote"));
		this.letRing.setToolTipText(this.getText("effects.let-ring"));
		this.harmonicNote.setToolTipText(this.getText("effects.harmonic"));
		this.graceNote.setToolTipText(this.getText("effects.grace"));
		this.trill.setToolTipText(this.getText("effects.trill"));
		this.tremoloPicking.setToolTipText(this.getText("effects.tremolo-picking"));
		this.palmMute.setToolTipText(this.getText("effects.palm-mute"));
		this.staccato.setToolTipText(this.getText("effects.staccato"));
		this.tapping.setToolTipText(this.getText("effects.tapping"));
		this.slapping.setToolTipText(this.getText("effects.slapping"));
		this.popping.setToolTipText(this.getText("effects.popping"));
		this.fadeIn.setToolTipText(this.getText("effects.fade-in"));
	}

	public void loadSectionIcons() {
		this.vibrato.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_VIBRATO));
		this.bend.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_BEND));
		this.tremoloBar.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_TREMOLO_BAR));
		this.slide.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_SLIDE));
		this.deadNote.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_DEAD));
		this.hammer.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_HAMMER));
		this.ghostNote.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_GHOST));
		this.accentuatedNote.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_ACCENTUATED));
		this.heavyAccentuatedNote.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_HEAVY_ACCENTUATED));
		this.letRing.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_LET_RING));
		this.harmonicNote.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_HARMONIC));
		this.graceNote.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_GRACE));
		this.trill.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_TRILL));
		this.tremoloPicking.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_TREMOLO_PICKING));
		this.palmMute.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_PALM_MUTE));
		this.staccato.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_STACCATO));
		this.tapping.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_TAPPING));
		this.slapping.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_SLAPPING));
		this.popping.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_POPPING));
		this.fadeIn.setImage(this.getIconManager().getImageByName(TGIconManager.EFFECT_FADE_IN));
	}

	public void updateSectionItems() {
		boolean running = MidiPlayer.getInstance(this.getToolBar().getContext()).isRunning();
		TGNoteRange noteRange = this.getTablature().getCurrentNoteRange();
		boolean isPercussion = this.getTablature().getCaret().getTrack().isPercussion();

		this.vibrato.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.vibrato.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isVibrato()));

		this.bend.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.bend.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isBend()));

		this.tremoloBar.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.tremoloBar.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isTremoloBar()));

		this.slide.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.slide.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isSlide()));

		this.deadNote.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.deadNote.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isDeadNote()));

		this.hammer.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.hammer.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isHammer()));

		this.ghostNote.setEnabled(!running && !noteRange.isEmpty());
		this.ghostNote.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isGhostNote()));

		this.accentuatedNote.setEnabled(!running && !noteRange.isEmpty());
		this.accentuatedNote.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isAccentuatedNote()));

		this.heavyAccentuatedNote.setEnabled(!running && !noteRange.isEmpty());
		this.heavyAccentuatedNote.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isHeavyAccentuatedNote()));

		this.letRing.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.letRing.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isLetRing()));

		this.harmonicNote.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.harmonicNote.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isHarmonic()));

		this.graceNote.setEnabled(!running && !noteRange.isEmpty());
		this.graceNote.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isGrace()));

		this.trill.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.trill.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isTrill()));

		this.tremoloPicking.setEnabled(!running && !noteRange.isEmpty());
		this.tremoloPicking.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isTremoloPicking()));

		this.palmMute.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.palmMute.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isPalmMute()));

		this.staccato.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.staccato.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isStaccato()));

		this.tapping.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.tapping.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isTapping()));

		this.slapping.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.slapping.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isSlapping()));

		this.popping.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.popping.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isPopping()));

		this.fadeIn.setEnabled(!running && !noteRange.isEmpty() && !isPercussion);
		this.fadeIn.setChecked(!noteRange.isEmpty() && noteRange.getNotes().stream().allMatch(n -> n.getEffect().isFadeIn()));
	}
}
