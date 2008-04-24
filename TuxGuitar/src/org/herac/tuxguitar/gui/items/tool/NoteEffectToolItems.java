/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.effects.ChangeAccentuatedNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeBendNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeDeadNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeFadeInAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeGhostNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeGraceNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeHammerNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeHarmonicNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeHeavyAccentuatedNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangePalmMuteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangePoppingAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeSlappingAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeSlideNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeStaccatoAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeTappingAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeTremoloBarAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeTremoloPickingAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeTrillNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeVibratoNoteAction;
import org.herac.tuxguitar.gui.items.ToolItems;
import org.herac.tuxguitar.song.models.TGNote;
/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NoteEffectToolItems  extends ToolItems{
	public static final String NAME = "effect.items";
	
	private ToolItem deadNote;
	private ToolItem ghostNote;
	private ToolItem accentuatedNote;
	private ToolItem heavyAccentuatedNote;
	private ToolItem harmonicNote;
	private ToolItem graceNote;
	
	private ToolItem vibrato;
	private ToolItem bend;
	private ToolItem tremoloBar;
	private ToolItem slide;
	private ToolItem hammer;
	
	private ToolItem trill;
	private ToolItem tremoloPicking;
	private ToolItem palmMute;
	
	private ToolItem staccato;
	private ToolItem tapping;
	private ToolItem slapping;
	private ToolItem popping;
	
	private ToolItem fadeIn;
	
	public NoteEffectToolItems(){
		super(NAME);
	}
	
	public void showItems(ToolBar toolBar){
		//--DEAD NOTE--
		this.deadNote = new ToolItem(toolBar, SWT.CHECK);
		this.deadNote.addSelectionListener(TuxGuitar.instance().getAction(ChangeDeadNoteAction.NAME));
		
		//--GHOST NOTE--
		this.ghostNote = new ToolItem(toolBar, SWT.CHECK);
		this.ghostNote.addSelectionListener(TuxGuitar.instance().getAction(ChangeGhostNoteAction.NAME));
		
		//--ACCENTUATED NOTE--
		this.accentuatedNote = new ToolItem(toolBar, SWT.CHECK);
		this.accentuatedNote.addSelectionListener(TuxGuitar.instance().getAction(ChangeAccentuatedNoteAction.NAME));
		
		//--HEAVY ACCENTUATED NOTE--
		this.heavyAccentuatedNote = new ToolItem(toolBar, SWT.CHECK);
		this.heavyAccentuatedNote.addSelectionListener(TuxGuitar.instance().getAction(ChangeHeavyAccentuatedNoteAction.NAME));
		
		//--HARMONIC NOTE--
		this.harmonicNote = new ToolItem(toolBar, SWT.CHECK);
		this.harmonicNote.addSelectionListener(TuxGuitar.instance().getAction(ChangeHarmonicNoteAction.NAME));
		
		//--GRACE NOTE--
		this.graceNote = new ToolItem(toolBar, SWT.CHECK);
		this.graceNote.addSelectionListener(TuxGuitar.instance().getAction(ChangeGraceNoteAction.NAME));
		
		//--SEPARATOR--
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		//--VIBRATO--
		this.vibrato = new ToolItem(toolBar, SWT.CHECK);
		this.vibrato.addSelectionListener(TuxGuitar.instance().getAction(ChangeVibratoNoteAction.NAME));
		
		//--BEND--
		this.bend = new ToolItem(toolBar, SWT.CHECK);
		this.bend.addSelectionListener(TuxGuitar.instance().getAction(ChangeBendNoteAction.NAME));
		
		//--BEND--
		this.tremoloBar = new ToolItem(toolBar, SWT.CHECK);
		this.tremoloBar.addSelectionListener(TuxGuitar.instance().getAction(ChangeTremoloBarAction.NAME));
		
		//--SLIDE--
		this.slide = new ToolItem(toolBar, SWT.CHECK);
		this.slide.addSelectionListener(TuxGuitar.instance().getAction(ChangeSlideNoteAction.NAME));
		
		//--HAMMER--
		this.hammer = new ToolItem(toolBar, SWT.CHECK);
		this.hammer.addSelectionListener(TuxGuitar.instance().getAction(ChangeHammerNoteAction.NAME));
		
		//--SEPARATOR--
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		//--TRILL--
		this.trill = new ToolItem(toolBar, SWT.CHECK);
		this.trill.addSelectionListener(TuxGuitar.instance().getAction(ChangeTrillNoteAction.NAME));
		
		//--TREMOLO PICKING--
		this.tremoloPicking = new ToolItem(toolBar, SWT.CHECK);
		this.tremoloPicking.addSelectionListener(TuxGuitar.instance().getAction(ChangeTremoloPickingAction.NAME));
		
		//--PALM MUTE--
		this.palmMute = new ToolItem(toolBar, SWT.CHECK);
		this.palmMute.addSelectionListener(TuxGuitar.instance().getAction(ChangePalmMuteAction.NAME));
		
		//--STACCATO
		this.staccato = new ToolItem(toolBar, SWT.CHECK);
		this.staccato.addSelectionListener(TuxGuitar.instance().getAction(ChangeStaccatoAction.NAME));
		
		//--SEPARATOR--
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		//--TAPPING
		this.tapping = new ToolItem(toolBar, SWT.CHECK);
		this.tapping.addSelectionListener(TuxGuitar.instance().getAction(ChangeTappingAction.NAME));
		
		//--SLAPPING
		this.slapping = new ToolItem(toolBar, SWT.CHECK);
		this.slapping.addSelectionListener(TuxGuitar.instance().getAction(ChangeSlappingAction.NAME));
		
		//--POPPING
		this.popping = new ToolItem(toolBar, SWT.CHECK);
		this.popping.addSelectionListener(TuxGuitar.instance().getAction(ChangePoppingAction.NAME));
		
		//--SEPARATOR--
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		//--FADE IN
		this.fadeIn = new ToolItem(toolBar, SWT.CHECK);
		this.fadeIn.addSelectionListener(TuxGuitar.instance().getAction(ChangeFadeInAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		TGNote note = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getSelectedNote();
		boolean running = TuxGuitar.instance().getPlayer().isRunning();
		this.vibrato.setSelection(note != null && note.getEffect().isVibrato());
		this.vibrato.setEnabled(!running && note != null);
		this.bend.setSelection(note != null && note.getEffect().isBend());
		this.bend.setEnabled(!running && note != null);
		this.tremoloBar.setSelection(note != null && note.getEffect().isTremoloBar());
		this.tremoloBar.setEnabled(!running && note != null);
		this.deadNote.setSelection(note != null && note.getEffect().isDeadNote());
		this.deadNote.setEnabled(!running && note != null);
		this.slide.setSelection(note != null && note.getEffect().isSlide());
		this.slide.setEnabled(!running && note != null);
		this.hammer.setSelection(note != null && note.getEffect().isHammer());
		this.hammer.setEnabled(!running && note != null);
		this.ghostNote.setSelection(note != null && note.getEffect().isGhostNote());
		this.ghostNote.setEnabled(!running && note != null);
		this.accentuatedNote.setSelection(note != null && note.getEffect().isAccentuatedNote());
		this.accentuatedNote.setEnabled(!running && note != null);
		this.heavyAccentuatedNote.setSelection(note != null && note.getEffect().isHeavyAccentuatedNote());
		this.heavyAccentuatedNote.setEnabled(!running && note != null);
		this.harmonicNote.setSelection(note != null && note.getEffect().isHarmonic());
		this.harmonicNote.setEnabled(!running && note != null);
		this.graceNote.setSelection(note != null && note.getEffect().isGrace());
		this.graceNote.setEnabled(!running && note != null);
		this.trill.setSelection(note != null && note.getEffect().isTrill());
		this.trill.setEnabled(!running && note != null);
		this.tremoloPicking.setSelection(note != null && note.getEffect().isTremoloPicking());
		this.tremoloPicking.setEnabled(!running && note != null);
		this.palmMute.setSelection(note != null && note.getEffect().isPalmMute());
		this.palmMute.setEnabled(!running && note != null);
		this.staccato.setSelection(note != null && note.getEffect().isStaccato());
		this.staccato.setEnabled(!running && note != null);
		this.tapping.setSelection(note != null && note.getEffect().isTapping());
		this.tapping.setEnabled(!running && note != null);
		this.slapping.setSelection(note != null && note.getEffect().isSlapping());
		this.slapping.setEnabled(!running && note != null);
		this.popping.setSelection(note != null && note.getEffect().isPopping());
		this.popping.setEnabled(!running && note != null);
		this.fadeIn.setSelection(note != null && note.getEffect().isFadeIn());
		this.fadeIn.setEnabled(!running && note != null);
	}
	
	public void loadProperties(){
		this.vibrato.setToolTipText(TuxGuitar.getProperty("effects.vibrato"));
		this.bend.setToolTipText(TuxGuitar.getProperty("effects.bend"));
		this.tremoloBar.setToolTipText(TuxGuitar.getProperty("effects.tremolo-bar"));
		this.popping.setToolTipText(TuxGuitar.getProperty("effects.popping"));
		this.deadNote.setToolTipText(TuxGuitar.getProperty("effects.deadnote"));
		this.slide.setToolTipText(TuxGuitar.getProperty("effects.slide"));
		this.hammer.setToolTipText(TuxGuitar.getProperty("effects.hammer"));
		this.ghostNote.setToolTipText(TuxGuitar.getProperty("effects.ghostnote"));
		this.accentuatedNote.setToolTipText(TuxGuitar.getProperty("effects.accentuatednote"));
		this.heavyAccentuatedNote.setToolTipText(TuxGuitar.getProperty("effects.heavyaccentuatednote"));
		this.harmonicNote.setToolTipText(TuxGuitar.getProperty("effects.harmonic"));
		this.graceNote.setToolTipText(TuxGuitar.getProperty("effects.grace"));
		this.trill.setToolTipText(TuxGuitar.getProperty("effects.trill"));
		this.tremoloPicking.setToolTipText(TuxGuitar.getProperty("effects.tremolo-picking"));
		this.palmMute.setToolTipText(TuxGuitar.getProperty("effects.palm-mute"));
		this.staccato.setToolTipText(TuxGuitar.getProperty("effects.staccato"));
		this.tapping.setToolTipText(TuxGuitar.getProperty("effects.tapping"));
		this.slapping.setToolTipText(TuxGuitar.getProperty("effects.slapping"));
		this.popping.setToolTipText(TuxGuitar.getProperty("effects.popping"));
		this.fadeIn.setToolTipText(TuxGuitar.getProperty("effects.fade-in"));
	}
	
	public void loadIcons(){
		this.deadNote.setImage(TuxGuitar.instance().getIconManager().getEffectDead());
		this.ghostNote.setImage(TuxGuitar.instance().getIconManager().getEffectGhost());
		this.accentuatedNote.setImage(TuxGuitar.instance().getIconManager().getEffectAccentuated());
		this.heavyAccentuatedNote.setImage(TuxGuitar.instance().getIconManager().getEffectHeavyAccentuated());
		this.harmonicNote.setImage(TuxGuitar.instance().getIconManager().getEffectHarmonic());
		this.graceNote.setImage(TuxGuitar.instance().getIconManager().getEffectGrace());
		this.vibrato.setImage(TuxGuitar.instance().getIconManager().getEffectVibrato());
		this.bend.setImage(TuxGuitar.instance().getIconManager().getEffectBend());
		this.tremoloBar.setImage(TuxGuitar.instance().getIconManager().getEffectTremoloBar());
		this.slide.setImage(TuxGuitar.instance().getIconManager().getEffectSlide());
		this.hammer.setImage(TuxGuitar.instance().getIconManager().getEffectHammer());
		this.trill.setImage(TuxGuitar.instance().getIconManager().getEffectTrill());
		this.tremoloPicking.setImage(TuxGuitar.instance().getIconManager().getEffectTremoloPicking());
		this.palmMute.setImage(TuxGuitar.instance().getIconManager().getEffectPalmMute());
		this.staccato.setImage(TuxGuitar.instance().getIconManager().getEffectStaccato());
		this.tapping.setImage(TuxGuitar.instance().getIconManager().getEffectTapping());
		this.slapping.setImage(TuxGuitar.instance().getIconManager().getEffectSlapping());
		this.popping.setImage(TuxGuitar.instance().getIconManager().getEffectPopping());
		this.fadeIn.setImage(TuxGuitar.instance().getIconManager().getEffectFadeIn());
	}
}
