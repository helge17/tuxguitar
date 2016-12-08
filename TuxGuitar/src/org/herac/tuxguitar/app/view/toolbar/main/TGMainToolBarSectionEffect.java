package org.herac.tuxguitar.app.view.toolbar.main;

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
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;

public class TGMainToolBarSectionEffect extends TGMainToolBarSection {
	
	private UIToolMenuItem menuItem;
	
	private UIMenuActionItem deadNote;
	private UIMenuActionItem ghostNote;
	private UIMenuActionItem accentuatedNote;
	private UIMenuActionItem heavyAccentuatedNote;
	private UIMenuActionItem harmonicNote;
	private UIMenuActionItem graceNote;
	private UIMenuActionItem vibrato;
	private UIMenuActionItem bend;
	private UIMenuActionItem tremoloBar;
	private UIMenuActionItem slide;
	private UIMenuActionItem hammer;
	private UIMenuActionItem trill;
	private UIMenuActionItem tremoloPicking;
	private UIMenuActionItem palmMute;
	private UIMenuActionItem staccato;
	private UIMenuActionItem tapping;
	private UIMenuActionItem slapping;
	private UIMenuActionItem popping;
	private UIMenuActionItem fadeIn;
	
	public TGMainToolBarSectionEffect(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.menuItem = this.getToolBar().getControl().createMenuItem();
		
		//--DEAD NOTE--
		this.deadNote = this.menuItem.getMenu().createActionItem();
		this.deadNote.addSelectionListener(this.createActionProcessor(TGChangeDeadNoteAction.NAME));
		
		//--GHOST NOTE--
		this.ghostNote = this.menuItem.getMenu().createActionItem();
		this.ghostNote.addSelectionListener(this.createActionProcessor(TGChangeGhostNoteAction.NAME));
		
		//--ACCENTUATED NOTE--
		this.accentuatedNote = this.menuItem.getMenu().createActionItem();
		this.accentuatedNote.addSelectionListener(this.createActionProcessor(TGChangeAccentuatedNoteAction.NAME));
		
		//--HEAVY ACCENTUATED NOTE--
		this.heavyAccentuatedNote = this.menuItem.getMenu().createActionItem();
		this.heavyAccentuatedNote.addSelectionListener(this.createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME));
		
		//--HARMONIC NOTE--
		this.harmonicNote = this.menuItem.getMenu().createActionItem();
		this.harmonicNote.addSelectionListener(this.createActionProcessor(TGOpenHarmonicDialogAction.NAME));
		
		//--GRACE NOTE--
		this.graceNote = this.menuItem.getMenu().createActionItem();
		this.graceNote.addSelectionListener(this.createActionProcessor(TGOpenGraceDialogAction.NAME));
		
		//--SEPARATOR--
		this.menuItem.getMenu().createSeparator();
		
		//--VIBRATO--
		this.vibrato = this.menuItem.getMenu().createActionItem();
		this.vibrato.addSelectionListener(this.createActionProcessor(TGChangeVibratoNoteAction.NAME));
		
		//--BEND--
		this.bend = this.menuItem.getMenu().createActionItem();
		this.bend.addSelectionListener(this.createActionProcessor(TGOpenBendDialogAction.NAME));
		
		//--BEND--
		this.tremoloBar = this.menuItem.getMenu().createActionItem();
		this.tremoloBar.addSelectionListener(this.createActionProcessor(TGOpenTremoloBarDialogAction.NAME));
		
		//--SLIDE--
		this.slide = this.menuItem.getMenu().createActionItem();
		this.slide.addSelectionListener(this.createActionProcessor(TGChangeSlideNoteAction.NAME));
		
		//--HAMMER--
		this.hammer = this.menuItem.getMenu().createActionItem();
		this.hammer.addSelectionListener(this.createActionProcessor(TGChangeHammerNoteAction.NAME));
		
		//--SEPARATOR--
		this.menuItem.getMenu().createSeparator();
		
		//--TRILL--
		this.trill = this.menuItem.getMenu().createActionItem();
		this.trill.addSelectionListener(this.createActionProcessor(TGOpenTrillDialogAction.NAME));
		
		//--TREMOLO PICKING--
		this.tremoloPicking = this.menuItem.getMenu().createActionItem();
		this.tremoloPicking.addSelectionListener(this.createActionProcessor(TGOpenTremoloPickingDialogAction.NAME));
		
		//--PALM MUTE--
		this.palmMute = this.menuItem.getMenu().createActionItem();
		this.palmMute.addSelectionListener(this.createActionProcessor(TGChangePalmMuteAction.NAME));
		
		//--STACCATO
		this.staccato = this.menuItem.getMenu().createActionItem();
		this.staccato.addSelectionListener(this.createActionProcessor(TGChangeStaccatoAction.NAME));
		
		//--SEPARATOR--
		this.menuItem.getMenu().createSeparator();
		
		//--TAPPING
		this.tapping = this.menuItem.getMenu().createActionItem();
		this.tapping.addSelectionListener(this.createActionProcessor(TGChangeTappingAction.NAME));
		
		//--SLAPPING
		this.slapping = this.menuItem.getMenu().createActionItem();
		this.slapping.addSelectionListener(this.createActionProcessor(TGChangeSlappingAction.NAME));
		
		//--POPPING
		this.popping = this.menuItem.getMenu().createActionItem();
		this.popping.addSelectionListener(this.createActionProcessor(TGChangePoppingAction.NAME));
		
		//--SEPARATOR--
		this.menuItem.getMenu().createSeparator();
		
		//--FADE IN
		this.fadeIn = this.menuItem.getMenu().createActionItem();
		this.fadeIn.addSelectionListener(this.createActionProcessor(TGChangeFadeInAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		TGNote note = this.getTablature().getCaret().getSelectedNote();
		this.menuItem.setToolTipText(this.getText("effects"));
		this.deadNote.setText(this.getText("effects.deadnote", (note != null && note.getEffect().isDeadNote())));
		this.ghostNote.setText(this.getText("effects.ghostnote", (note != null && note.getEffect().isGhostNote())));
		this.accentuatedNote.setText(this.getText("effects.accentuatednote", (note != null && note.getEffect().isAccentuatedNote())));
		this.heavyAccentuatedNote.setText(this.getText("effects.heavyaccentuatednote", (note != null && note.getEffect().isHeavyAccentuatedNote())));
		this.harmonicNote.setText(this.getText("effects.harmonic", (note != null && note.getEffect().isHarmonic())));
		this.graceNote.setText(this.getText("effects.grace", (note != null && note.getEffect().isGrace())));
		this.vibrato.setText(this.getText("effects.vibrato", (note != null && note.getEffect().isVibrato())));
		this.bend.setText(this.getText("effects.bend", (note != null && note.getEffect().isBend())));
		this.tremoloBar.setText(this.getText("effects.tremolo-bar", (note != null && note.getEffect().isTremoloBar())));
		this.slide.setText(this.getText("effects.slide", (note != null && note.getEffect().isSlide())));
		this.hammer.setText(this.getText("effects.hammer", (note != null && note.getEffect().isHammer())));
		this.trill.setText(this.getText("effects.trill", (note != null && note.getEffect().isTrill())));
		this.tremoloPicking.setText(this.getText("effects.tremolo-picking", (note != null && note.getEffect().isTremoloPicking())));
		this.palmMute.setText(this.getText("effects.palm-mute", (note != null && note.getEffect().isPalmMute())));
		this.staccato.setText(this.getText("effects.staccato", (note != null && note.getEffect().isStaccato())));
		this.tapping.setText(this.getText("effects.tapping", (note != null && note.getEffect().isTapping())));
		this.slapping.setText(this.getText("effects.slapping", (note != null && note.getEffect().isSlapping())));
		this.popping.setText(this.getText("effects.popping", (note != null && note.getEffect().isPopping())));
		this.fadeIn.setText(this.getText("effects.fade-in", (note != null && note.getEffect().isFadeIn())));
	}
	
	public void loadIcons(){
		this.menuItem.setImage(this.getIconManager().getEffectVibrato());
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
		
		this.menuItem.setEnabled(!running && note != null);
		
		this.deadNote.setEnabled(!running && note != null);
		this.deadNote.setText(this.getText("effects.deadnote", (note != null && note.getEffect().isDeadNote())));
		
		this.ghostNote.setEnabled(!running && note != null);
		this.ghostNote.setText(this.getText("effects.ghostnote", (note != null && note.getEffect().isGhostNote())));
		
		this.accentuatedNote.setEnabled(!running && note != null);
		this.accentuatedNote.setText(this.getText("effects.accentuatednote", (note != null && note.getEffect().isAccentuatedNote())));
		
		this.heavyAccentuatedNote.setEnabled(!running && note != null);
		this.heavyAccentuatedNote.setText(this.getText("effects.heavyaccentuatednote", (note != null && note.getEffect().isHeavyAccentuatedNote())));
		
		this.harmonicNote.setEnabled(!running && note != null);
		this.harmonicNote.setText(this.getText("effects.harmonic", (note != null && note.getEffect().isHarmonic())));
		
		this.graceNote.setEnabled(!running && note != null);
		this.graceNote.setText(this.getText("effects.grace", (note != null && note.getEffect().isGrace())));
		
		this.vibrato.setEnabled(!running && note != null);
		this.vibrato.setText(this.getText("effects.vibrato", (note != null && note.getEffect().isVibrato())));
		
		this.bend.setEnabled(!running && note != null);
		this.bend.setText(this.getText("effects.bend", (note != null && note.getEffect().isBend())));
		
		this.tremoloBar.setEnabled(!running && note != null);
		this.tremoloBar.setText(this.getText("effects.tremolo-bar", (note != null && note.getEffect().isTremoloBar())));
		
		this.slide.setEnabled(!running && note != null);
		this.slide.setText(this.getText("effects.slide", (note != null && note.getEffect().isSlide())));
		
		this.hammer.setEnabled(!running && note != null);
		this.hammer.setText(this.getText("effects.hammer", (note != null && note.getEffect().isHammer())));
		
		this.trill.setEnabled(!running && note != null);
		this.trill.setText(this.getText("effects.trill", (note != null && note.getEffect().isTrill())));
		
		this.tremoloPicking.setEnabled(!running && note != null);
		this.tremoloPicking.setText(this.getText("effects.tremolo-picking", (note != null && note.getEffect().isTremoloPicking())));
		
		this.palmMute.setEnabled(!running && note != null);
		this.palmMute.setText(this.getText("effects.palm-mute", (note != null && note.getEffect().isPalmMute())));
		
		this.staccato.setEnabled(!running && note != null);
		this.staccato.setText(this.getText("effects.staccato", (note != null && note.getEffect().isStaccato())));
		
		this.tapping.setEnabled(!running && note != null);
		this.tapping.setText(this.getText("effects.tapping", (note != null && note.getEffect().isTapping())));
		
		this.slapping.setEnabled(!running && note != null);
		this.slapping.setText(this.getText("effects.slapping", (note != null && note.getEffect().isSlapping())));
		
		this.popping.setEnabled(!running && note != null);
		this.popping.setText(this.getText("effects.popping", (note != null && note.getEffect().isPopping())));
		
		this.fadeIn.setEnabled(!running && note != null);
		this.fadeIn.setText(this.getText("effects.fade-in", (note != null && note.getEffect().isFadeIn())));
	}
}
