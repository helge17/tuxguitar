package org.herac.tuxguitar.app.view.toolbar;

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

public class TGToolBarSectionEffect implements TGToolBarSection {
	
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
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = toolBar.getControl().createMenuItem();
		
		//--DEAD NOTE--
		this.deadNote = this.menuItem.getMenu().createActionItem();
		this.deadNote.addSelectionListener(toolBar.createActionProcessor(TGChangeDeadNoteAction.NAME));
		
		//--GHOST NOTE--
		this.ghostNote = this.menuItem.getMenu().createActionItem();
		this.ghostNote.addSelectionListener(toolBar.createActionProcessor(TGChangeGhostNoteAction.NAME));
		
		//--ACCENTUATED NOTE--
		this.accentuatedNote = this.menuItem.getMenu().createActionItem();
		this.accentuatedNote.addSelectionListener(toolBar.createActionProcessor(TGChangeAccentuatedNoteAction.NAME));
		
		//--HEAVY ACCENTUATED NOTE--
		this.heavyAccentuatedNote = this.menuItem.getMenu().createActionItem();
		this.heavyAccentuatedNote.addSelectionListener(toolBar.createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME));
		
		//--HARMONIC NOTE--
		this.harmonicNote = this.menuItem.getMenu().createActionItem();
		this.harmonicNote.addSelectionListener(toolBar.createActionProcessor(TGOpenHarmonicDialogAction.NAME));
		
		//--GRACE NOTE--
		this.graceNote = this.menuItem.getMenu().createActionItem();
		this.graceNote.addSelectionListener(toolBar.createActionProcessor(TGOpenGraceDialogAction.NAME));
		
		//--SEPARATOR--
		this.menuItem.getMenu().createSeparator();
		
		//--VIBRATO--
		this.vibrato = this.menuItem.getMenu().createActionItem();
		this.vibrato.addSelectionListener(toolBar.createActionProcessor(TGChangeVibratoNoteAction.NAME));
		
		//--BEND--
		this.bend = this.menuItem.getMenu().createActionItem();
		this.bend.addSelectionListener(toolBar.createActionProcessor(TGOpenBendDialogAction.NAME));
		
		//--BEND--
		this.tremoloBar = this.menuItem.getMenu().createActionItem();
		this.tremoloBar.addSelectionListener(toolBar.createActionProcessor(TGOpenTremoloBarDialogAction.NAME));
		
		//--SLIDE--
		this.slide = this.menuItem.getMenu().createActionItem();
		this.slide.addSelectionListener(toolBar.createActionProcessor(TGChangeSlideNoteAction.NAME));
		
		//--HAMMER--
		this.hammer = this.menuItem.getMenu().createActionItem();
		this.hammer.addSelectionListener(toolBar.createActionProcessor(TGChangeHammerNoteAction.NAME));
		
		//--SEPARATOR--
		this.menuItem.getMenu().createSeparator();
		
		//--TRILL--
		this.trill = this.menuItem.getMenu().createActionItem();
		this.trill.addSelectionListener(toolBar.createActionProcessor(TGOpenTrillDialogAction.NAME));
		
		//--TREMOLO PICKING--
		this.tremoloPicking = this.menuItem.getMenu().createActionItem();
		this.tremoloPicking.addSelectionListener(toolBar.createActionProcessor(TGOpenTremoloPickingDialogAction.NAME));
		
		//--PALM MUTE--
		this.palmMute = this.menuItem.getMenu().createActionItem();
		this.palmMute.addSelectionListener(toolBar.createActionProcessor(TGChangePalmMuteAction.NAME));
		
		//--STACCATO
		this.staccato = this.menuItem.getMenu().createActionItem();
		this.staccato.addSelectionListener(toolBar.createActionProcessor(TGChangeStaccatoAction.NAME));
		
		//--SEPARATOR--
		this.menuItem.getMenu().createSeparator();
		
		//--TAPPING
		this.tapping = this.menuItem.getMenu().createActionItem();
		this.tapping.addSelectionListener(toolBar.createActionProcessor(TGChangeTappingAction.NAME));
		
		//--SLAPPING
		this.slapping = this.menuItem.getMenu().createActionItem();
		this.slapping.addSelectionListener(toolBar.createActionProcessor(TGChangeSlappingAction.NAME));
		
		//--POPPING
		this.popping = this.menuItem.getMenu().createActionItem();
		this.popping.addSelectionListener(toolBar.createActionProcessor(TGChangePoppingAction.NAME));
		
		//--SEPARATOR--
		this.menuItem.getMenu().createSeparator();
		
		//--FADE IN
		this.fadeIn = this.menuItem.getMenu().createActionItem();
		this.fadeIn.addSelectionListener(toolBar.createActionProcessor(TGChangeFadeInAction.NAME));
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar){
		TGNote note = toolBar.getTablature().getCaret().getSelectedNote();
		this.menuItem.setToolTipText(toolBar.getText("effects"));
		this.deadNote.setText(toolBar.getText("effects.deadnote", (note != null && note.getEffect().isDeadNote())));
		this.ghostNote.setText(toolBar.getText("effects.ghostnote", (note != null && note.getEffect().isGhostNote())));
		this.accentuatedNote.setText(toolBar.getText("effects.accentuatednote", (note != null && note.getEffect().isAccentuatedNote())));
		this.heavyAccentuatedNote.setText(toolBar.getText("effects.heavyaccentuatednote", (note != null && note.getEffect().isHeavyAccentuatedNote())));
		this.harmonicNote.setText(toolBar.getText("effects.harmonic", (note != null && note.getEffect().isHarmonic())));
		this.graceNote.setText(toolBar.getText("effects.grace", (note != null && note.getEffect().isGrace())));
		this.vibrato.setText(toolBar.getText("effects.vibrato", (note != null && note.getEffect().isVibrato())));
		this.bend.setText(toolBar.getText("effects.bend", (note != null && note.getEffect().isBend())));
		this.tremoloBar.setText(toolBar.getText("effects.tremolo-bar", (note != null && note.getEffect().isTremoloBar())));
		this.slide.setText(toolBar.getText("effects.slide", (note != null && note.getEffect().isSlide())));
		this.hammer.setText(toolBar.getText("effects.hammer", (note != null && note.getEffect().isHammer())));
		this.trill.setText(toolBar.getText("effects.trill", (note != null && note.getEffect().isTrill())));
		this.tremoloPicking.setText(toolBar.getText("effects.tremolo-picking", (note != null && note.getEffect().isTremoloPicking())));
		this.palmMute.setText(toolBar.getText("effects.palm-mute", (note != null && note.getEffect().isPalmMute())));
		this.staccato.setText(toolBar.getText("effects.staccato", (note != null && note.getEffect().isStaccato())));
		this.tapping.setText(toolBar.getText("effects.tapping", (note != null && note.getEffect().isTapping())));
		this.slapping.setText(toolBar.getText("effects.slapping", (note != null && note.getEffect().isSlapping())));
		this.popping.setText(toolBar.getText("effects.popping", (note != null && note.getEffect().isPopping())));
		this.fadeIn.setText(toolBar.getText("effects.fade-in", (note != null && note.getEffect().isFadeIn())));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.menuItem.setImage(toolBar.getIconManager().getEffectVibrato());
		this.deadNote.setImage(toolBar.getIconManager().getEffectDead());
		this.ghostNote.setImage(toolBar.getIconManager().getEffectGhost());
		this.accentuatedNote.setImage(toolBar.getIconManager().getEffectAccentuated());
		this.heavyAccentuatedNote.setImage(toolBar.getIconManager().getEffectHeavyAccentuated());
		this.harmonicNote.setImage(toolBar.getIconManager().getEffectHarmonic());
		this.graceNote.setImage(toolBar.getIconManager().getEffectGrace());
		this.vibrato.setImage(toolBar.getIconManager().getEffectVibrato());
		this.bend.setImage(toolBar.getIconManager().getEffectBend());
		this.tremoloBar.setImage(toolBar.getIconManager().getEffectTremoloBar());
		this.slide.setImage(toolBar.getIconManager().getEffectSlide());
		this.hammer.setImage(toolBar.getIconManager().getEffectHammer());
		this.trill.setImage(toolBar.getIconManager().getEffectTrill());
		this.tremoloPicking.setImage(toolBar.getIconManager().getEffectTremoloPicking());
		this.palmMute.setImage(toolBar.getIconManager().getEffectPalmMute());
		this.staccato.setImage(toolBar.getIconManager().getEffectStaccato());
		this.tapping.setImage(toolBar.getIconManager().getEffectTapping());
		this.slapping.setImage(toolBar.getIconManager().getEffectSlapping());
		this.popping.setImage(toolBar.getIconManager().getEffectPopping());
		this.fadeIn.setImage(toolBar.getIconManager().getEffectFadeIn());
	}
	
	public void updateItems(TGToolBar toolBar){
		boolean running = MidiPlayer.getInstance(toolBar.getContext()).isRunning();
		TGNote note = toolBar.getTablature().getCaret().getSelectedNote();
		
		this.menuItem.setEnabled(!running && note != null);
		
		this.deadNote.setEnabled(!running && note != null);
		this.deadNote.setText(toolBar.getText("effects.deadnote", (note != null && note.getEffect().isDeadNote())));
		
		this.ghostNote.setEnabled(!running && note != null);
		this.ghostNote.setText(toolBar.getText("effects.ghostnote", (note != null && note.getEffect().isGhostNote())));
		
		this.accentuatedNote.setEnabled(!running && note != null);
		this.accentuatedNote.setText(toolBar.getText("effects.accentuatednote", (note != null && note.getEffect().isAccentuatedNote())));
		
		this.heavyAccentuatedNote.setEnabled(!running && note != null);
		this.heavyAccentuatedNote.setText(toolBar.getText("effects.heavyaccentuatednote", (note != null && note.getEffect().isHeavyAccentuatedNote())));
		
		this.harmonicNote.setEnabled(!running && note != null);
		this.harmonicNote.setText(toolBar.getText("effects.harmonic", (note != null && note.getEffect().isHarmonic())));
		
		this.graceNote.setEnabled(!running && note != null);
		this.graceNote.setText(toolBar.getText("effects.grace", (note != null && note.getEffect().isGrace())));
		
		this.vibrato.setEnabled(!running && note != null);
		this.vibrato.setText(toolBar.getText("effects.vibrato", (note != null && note.getEffect().isVibrato())));
		
		this.bend.setEnabled(!running && note != null);
		this.bend.setText(toolBar.getText("effects.bend", (note != null && note.getEffect().isBend())));
		
		this.tremoloBar.setEnabled(!running && note != null);
		this.tremoloBar.setText(toolBar.getText("effects.tremolo-bar", (note != null && note.getEffect().isTremoloBar())));
		
		this.slide.setEnabled(!running && note != null);
		this.slide.setText(toolBar.getText("effects.slide", (note != null && note.getEffect().isSlide())));
		
		this.hammer.setEnabled(!running && note != null);
		this.hammer.setText(toolBar.getText("effects.hammer", (note != null && note.getEffect().isHammer())));
		
		this.trill.setEnabled(!running && note != null);
		this.trill.setText(toolBar.getText("effects.trill", (note != null && note.getEffect().isTrill())));
		
		this.tremoloPicking.setEnabled(!running && note != null);
		this.tremoloPicking.setText(toolBar.getText("effects.tremolo-picking", (note != null && note.getEffect().isTremoloPicking())));
		
		this.palmMute.setEnabled(!running && note != null);
		this.palmMute.setText(toolBar.getText("effects.palm-mute", (note != null && note.getEffect().isPalmMute())));
		
		this.staccato.setEnabled(!running && note != null);
		this.staccato.setText(toolBar.getText("effects.staccato", (note != null && note.getEffect().isStaccato())));
		
		this.tapping.setEnabled(!running && note != null);
		this.tapping.setText(toolBar.getText("effects.tapping", (note != null && note.getEffect().isTapping())));
		
		this.slapping.setEnabled(!running && note != null);
		this.slapping.setText(toolBar.getText("effects.slapping", (note != null && note.getEffect().isSlapping())));
		
		this.popping.setEnabled(!running && note != null);
		this.popping.setText(toolBar.getText("effects.popping", (note != null && note.getEffect().isPopping())));
		
		this.fadeIn.setEnabled(!running && note != null);
		this.fadeIn.setText(toolBar.getText("effects.fade-in", (note != null && note.getEffect().isFadeIn())));
	}
}
