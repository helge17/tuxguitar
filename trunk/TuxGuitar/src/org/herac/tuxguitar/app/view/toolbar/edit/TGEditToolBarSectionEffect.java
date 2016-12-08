package org.herac.tuxguitar.app.view.toolbar.edit;

import org.herac.tuxguitar.app.action.impl.effects.TGOpenBendDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenGraceDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenHarmonicDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTremoloBarDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTremoloPickingDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTrillDialogAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeAccentuatedNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeDeadNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeFadeInAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeGhostNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeHammerNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeHeavyAccentuatedNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangePalmMuteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangePoppingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeSlappingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeSlideNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeStaccatoAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeTappingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeVibratoNoteAction;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;

public class TGEditToolBarSectionEffect extends TGEditToolBarSection {
	
	private static final String SECTION_TITLE = "effects";
	
	private UIToolCheckableItem deadNote;
	private UIToolCheckableItem ghostNote;
	private UIToolCheckableItem accentuatedNote;
	private UIToolCheckableItem heavyAccentuatedNote;
	private UIToolCheckableItem harmonicNote;
	private UIToolCheckableItem graceNote;
	private UIToolCheckableItem vibrato;
	private UIToolCheckableItem bend;
	private UIToolCheckableItem tremoloBar;
	private UIToolCheckableItem slide;
	private UIToolCheckableItem hammer;
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
		
		//--DEAD NOTE--
		this.deadNote = toolBar.createCheckItem();
		this.deadNote.addSelectionListener(this.createActionProcessor(TGChangeDeadNoteAction.NAME));
		
		//--GHOST NOTE--
		this.ghostNote = toolBar.createCheckItem();
		this.ghostNote.addSelectionListener(this.createActionProcessor(TGChangeGhostNoteAction.NAME));
		
		//--ACCENTUATED NOTE--
		this.accentuatedNote = toolBar.createCheckItem();
		this.accentuatedNote.addSelectionListener(this.createActionProcessor(TGChangeAccentuatedNoteAction.NAME));
		
		//--HEAVY ACCENTUATED NOTE--
		this.heavyAccentuatedNote = toolBar.createCheckItem();
		this.heavyAccentuatedNote.addSelectionListener(this.createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME));
		
		//--HARMONIC NOTE--
		this.harmonicNote = toolBar.createCheckItem();
		this.harmonicNote.addSelectionListener(this.createActionProcessor(TGOpenHarmonicDialogAction.NAME));
		
		toolBar = this.createToolBar();
		
		//--GRACE NOTE--
		this.graceNote = toolBar.createCheckItem();
		this.graceNote.addSelectionListener(this.createActionProcessor(TGOpenGraceDialogAction.NAME));
		
		//--VIBRATO--
		this.vibrato = toolBar.createCheckItem();
		this.vibrato.addSelectionListener(this.createActionProcessor(TGChangeVibratoNoteAction.NAME));
		
		//--BEND--
		this.bend = toolBar.createCheckItem();
		this.bend.addSelectionListener(this.createActionProcessor(TGOpenBendDialogAction.NAME));
		
		//--BEND--
		this.tremoloBar = toolBar.createCheckItem();
		this.tremoloBar.addSelectionListener(this.createActionProcessor(TGOpenTremoloBarDialogAction.NAME));
		
		//--SLIDE--
		this.slide = toolBar.createCheckItem();
		this.slide.addSelectionListener(this.createActionProcessor(TGChangeSlideNoteAction.NAME));
		
		toolBar = this.createToolBar();
		
		//--HAMMER--
		this.hammer = toolBar.createCheckItem();
		this.hammer.addSelectionListener(this.createActionProcessor(TGChangeHammerNoteAction.NAME));
		
		//--TRILL--
		this.trill = toolBar.createCheckItem();
		this.trill.addSelectionListener(this.createActionProcessor(TGOpenTrillDialogAction.NAME));
		
		//--TREMOLO PICKING--
		this.tremoloPicking = toolBar.createCheckItem();
		this.tremoloPicking.addSelectionListener(this.createActionProcessor(TGOpenTremoloPickingDialogAction.NAME));
		
		//--PALM MUTE--
		this.palmMute = toolBar.createCheckItem();
		this.palmMute.addSelectionListener(this.createActionProcessor(TGChangePalmMuteAction.NAME));
		
		//--STACCATO
		this.staccato = toolBar.createCheckItem();
		this.staccato.addSelectionListener(this.createActionProcessor(TGChangeStaccatoAction.NAME));
		
		toolBar = this.createToolBar();
		
		//--TAPPING
		this.tapping = toolBar.createCheckItem();
		this.tapping.addSelectionListener(this.createActionProcessor(TGChangeTappingAction.NAME));
		
		//--SLAPPING
		this.slapping = toolBar.createCheckItem();
		this.slapping.addSelectionListener(this.createActionProcessor(TGChangeSlappingAction.NAME));
		
		//--POPPING
		this.popping = toolBar.createCheckItem();
		this.popping.addSelectionListener(this.createActionProcessor(TGChangePoppingAction.NAME));
		
		//--FADE IN
		this.fadeIn = toolBar.createCheckItem();
		this.fadeIn.addSelectionListener(this.createActionProcessor(TGChangeFadeInAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		this.deadNote.setToolTipText(this.getText("effects.deadnote"));
		this.ghostNote.setToolTipText(this.getText("effects.ghostnote"));
		this.accentuatedNote.setToolTipText(this.getText("effects.accentuatednote"));
		this.heavyAccentuatedNote.setToolTipText(this.getText("effects.heavyaccentuatednote"));
		this.harmonicNote.setToolTipText(this.getText("effects.harmonic"));
		this.graceNote.setToolTipText(this.getText("effects.grace"));
		this.vibrato.setToolTipText(this.getText("effects.vibrato"));
		this.bend.setToolTipText(this.getText("effects.bend"));
		this.tremoloBar.setToolTipText(this.getText("effects.tremolo-bar"));
		this.slide.setToolTipText(this.getText("effects.slide"));
		this.hammer.setToolTipText(this.getText("effects.hammer"));
		this.trill.setToolTipText(this.getText("effects.trill"));
		this.tremoloPicking.setToolTipText(this.getText("effects.tremolo-picking"));
		this.palmMute.setToolTipText(this.getText("effects.palm-mute"));
		this.staccato.setToolTipText(this.getText("effects.staccato"));
		this.tapping.setToolTipText(this.getText("effects.tapping"));
		this.slapping.setToolTipText(this.getText("effects.slapping"));
		this.popping.setToolTipText(this.getText("effects.popping"));
		this.fadeIn.setToolTipText(this.getText("effects.fade-in"));
	}
	
	public void loadIcons(){
		this.deadNote.setImage(this.getIconManager().getEffectDead());
		this.ghostNote.setImage(this.getIconManager().getEffectGhost());
		this.accentuatedNote.setImage(this.getIconManager().getEffectAccentuated());
		this.heavyAccentuatedNote.setImage(this.getIconManager().getEffectHeavyAccentuated());
		this.harmonicNote.setImage(this.getIconManager().getEffectHarmonic());
		this.graceNote.setImage(this.getIconManager().getEffectGrace());
		this.vibrato.setImage(this.getIconManager().getEffectVibrato());
		this.bend.setImage(this.getIconManager().getEffectBend());
		this.tremoloBar.setImage(this.getIconManager().getEffectTremoloBar());
		this.slide.setImage(this.getIconManager().getEffectSlide());
		this.hammer.setImage(this.getIconManager().getEffectHammer());
		this.trill.setImage(this.getIconManager().getEffectTrill());
		this.tremoloPicking.setImage(this.getIconManager().getEffectTremoloPicking());
		this.palmMute.setImage(this.getIconManager().getEffectPalmMute());
		this.staccato.setImage(this.getIconManager().getEffectStaccato());
		this.tapping.setImage(this.getIconManager().getEffectTapping());
		this.slapping.setImage(this.getIconManager().getEffectSlapping());
		this.popping.setImage(this.getIconManager().getEffectPopping());
		this.fadeIn.setImage(this.getIconManager().getEffectFadeIn());
	}
	
	public void updateItems(){
		boolean running = MidiPlayer.getInstance(this.getToolBar().getContext()).isRunning();
		TGNote note = this.getTablature().getCaret().getSelectedNote();
		
		this.deadNote.setEnabled(!running && note != null);
		this.deadNote.setChecked(note != null && note.getEffect().isDeadNote());
		
		this.ghostNote.setEnabled(!running && note != null);
		this.ghostNote.setChecked(note != null && note.getEffect().isGhostNote());
		
		this.accentuatedNote.setEnabled(!running && note != null);
		this.accentuatedNote.setChecked(note != null && note.getEffect().isAccentuatedNote());
		
		this.heavyAccentuatedNote.setEnabled(!running && note != null);
		this.heavyAccentuatedNote.setChecked(note != null && note.getEffect().isHeavyAccentuatedNote());
		
		this.harmonicNote.setEnabled(!running && note != null);
		this.harmonicNote.setChecked(note != null && note.getEffect().isHarmonic());
		
		this.graceNote.setEnabled(!running && note != null);
		this.graceNote.setChecked(note != null && note.getEffect().isGrace());
		
		this.vibrato.setEnabled(!running && note != null);
		this.vibrato.setChecked(note != null && note.getEffect().isVibrato());
		
		this.bend.setEnabled(!running && note != null);
		this.bend.setChecked(note != null && note.getEffect().isBend());
		
		this.tremoloBar.setEnabled(!running && note != null);
		this.tremoloBar.setChecked(note != null && note.getEffect().isTremoloBar());
		
		this.slide.setEnabled(!running && note != null);
		this.slide.setChecked(note != null && note.getEffect().isSlide());
		
		this.hammer.setEnabled(!running && note != null);
		this.hammer.setChecked(note != null && note.getEffect().isHammer());
		
		this.trill.setEnabled(!running && note != null);
		this.trill.setChecked(note != null && note.getEffect().isTrill());
		
		this.tremoloPicking.setEnabled(!running && note != null);
		this.tremoloPicking.setChecked(note != null && note.getEffect().isTremoloPicking());
		
		this.palmMute.setEnabled(!running && note != null);
		this.palmMute.setChecked(note != null && note.getEffect().isPalmMute());
		
		this.staccato.setEnabled(!running && note != null);
		this.staccato.setChecked(note != null && note.getEffect().isStaccato());
		
		this.tapping.setEnabled(!running && note != null);
		this.tapping.setChecked(note != null && note.getEffect().isTapping());
		
		this.slapping.setEnabled(!running && note != null);
		this.slapping.setChecked(note != null && note.getEffect().isSlapping());
		
		this.popping.setEnabled(!running && note != null);
		this.popping.setChecked(note != null && note.getEffect().isPopping());
		
		this.fadeIn.setEnabled(!running && note != null);
		this.fadeIn.setChecked(note != null && note.getEffect().isFadeIn());
	}
}
