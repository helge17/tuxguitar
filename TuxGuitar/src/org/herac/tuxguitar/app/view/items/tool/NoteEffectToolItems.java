/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.items.tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenBendDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenGraceDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenHarmonicDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTremoloBarDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTremoloPickingDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTrillDialogAction;
import org.herac.tuxguitar.app.view.items.ToolItems;
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
		this.deadNote.addSelectionListener(this.createActionProcessor(TGChangeDeadNoteAction.NAME));
		
		//--GHOST NOTE--
		this.ghostNote = new ToolItem(toolBar, SWT.CHECK);
		this.ghostNote.addSelectionListener(this.createActionProcessor(TGChangeGhostNoteAction.NAME));
		
		//--ACCENTUATED NOTE--
		this.accentuatedNote = new ToolItem(toolBar, SWT.CHECK);
		this.accentuatedNote.addSelectionListener(this.createActionProcessor(TGChangeAccentuatedNoteAction.NAME));
		
		//--HEAVY ACCENTUATED NOTE--
		this.heavyAccentuatedNote = new ToolItem(toolBar, SWT.CHECK);
		this.heavyAccentuatedNote.addSelectionListener(this.createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME));
		
		//--HARMONIC NOTE--
		this.harmonicNote = new ToolItem(toolBar, SWT.CHECK);
		this.harmonicNote.addSelectionListener(this.createActionProcessor(TGOpenHarmonicDialogAction.NAME));
		
		//--GRACE NOTE--
		this.graceNote = new ToolItem(toolBar, SWT.CHECK);
		this.graceNote.addSelectionListener(this.createActionProcessor(TGOpenGraceDialogAction.NAME));
		
		//--SEPARATOR--
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		//--VIBRATO--
		this.vibrato = new ToolItem(toolBar, SWT.CHECK);
		this.vibrato.addSelectionListener(this.createActionProcessor(TGChangeVibratoNoteAction.NAME));
		
		//--BEND--
		this.bend = new ToolItem(toolBar, SWT.CHECK);
		this.bend.addSelectionListener(this.createActionProcessor(TGOpenBendDialogAction.NAME));
		
		//--BEND--
		this.tremoloBar = new ToolItem(toolBar, SWT.CHECK);
		this.tremoloBar.addSelectionListener(this.createActionProcessor(TGOpenTremoloBarDialogAction.NAME));
		
		//--SLIDE--
		this.slide = new ToolItem(toolBar, SWT.CHECK);
		this.slide.addSelectionListener(this.createActionProcessor(TGChangeSlideNoteAction.NAME));
		
		//--HAMMER--
		this.hammer = new ToolItem(toolBar, SWT.CHECK);
		this.hammer.addSelectionListener(this.createActionProcessor(TGChangeHammerNoteAction.NAME));
		
		//--SEPARATOR--
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		//--TRILL--
		this.trill = new ToolItem(toolBar, SWT.CHECK);
		this.trill.addSelectionListener(this.createActionProcessor(TGOpenTrillDialogAction.NAME));
		
		//--TREMOLO PICKING--
		this.tremoloPicking = new ToolItem(toolBar, SWT.CHECK);
		this.tremoloPicking.addSelectionListener(this.createActionProcessor(TGOpenTremoloPickingDialogAction.NAME));
		
		//--PALM MUTE--
		this.palmMute = new ToolItem(toolBar, SWT.CHECK);
		this.palmMute.addSelectionListener(this.createActionProcessor(TGChangePalmMuteAction.NAME));
		
		//--STACCATO
		this.staccato = new ToolItem(toolBar, SWT.CHECK);
		this.staccato.addSelectionListener(this.createActionProcessor(TGChangeStaccatoAction.NAME));
		
		//--SEPARATOR--
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		//--TAPPING
		this.tapping = new ToolItem(toolBar, SWT.CHECK);
		this.tapping.addSelectionListener(this.createActionProcessor(TGChangeTappingAction.NAME));
		
		//--SLAPPING
		this.slapping = new ToolItem(toolBar, SWT.CHECK);
		this.slapping.addSelectionListener(this.createActionProcessor(TGChangeSlappingAction.NAME));
		
		//--POPPING
		this.popping = new ToolItem(toolBar, SWT.CHECK);
		this.popping.addSelectionListener(this.createActionProcessor(TGChangePoppingAction.NAME));
		
		//--SEPARATOR--
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		//--FADE IN
		this.fadeIn = new ToolItem(toolBar, SWT.CHECK);
		this.fadeIn.addSelectionListener(this.createActionProcessor(TGChangeFadeInAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		TGNote note = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getSelectedNote();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
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
		this.deadNote.setImage(TuxGuitar.getInstance().getIconManager().getEffectDead());
		this.ghostNote.setImage(TuxGuitar.getInstance().getIconManager().getEffectGhost());
		this.accentuatedNote.setImage(TuxGuitar.getInstance().getIconManager().getEffectAccentuated());
		this.heavyAccentuatedNote.setImage(TuxGuitar.getInstance().getIconManager().getEffectHeavyAccentuated());
		this.harmonicNote.setImage(TuxGuitar.getInstance().getIconManager().getEffectHarmonic());
		this.graceNote.setImage(TuxGuitar.getInstance().getIconManager().getEffectGrace());
		this.vibrato.setImage(TuxGuitar.getInstance().getIconManager().getEffectVibrato());
		this.bend.setImage(TuxGuitar.getInstance().getIconManager().getEffectBend());
		this.tremoloBar.setImage(TuxGuitar.getInstance().getIconManager().getEffectTremoloBar());
		this.slide.setImage(TuxGuitar.getInstance().getIconManager().getEffectSlide());
		this.hammer.setImage(TuxGuitar.getInstance().getIconManager().getEffectHammer());
		this.trill.setImage(TuxGuitar.getInstance().getIconManager().getEffectTrill());
		this.tremoloPicking.setImage(TuxGuitar.getInstance().getIconManager().getEffectTremoloPicking());
		this.palmMute.setImage(TuxGuitar.getInstance().getIconManager().getEffectPalmMute());
		this.staccato.setImage(TuxGuitar.getInstance().getIconManager().getEffectStaccato());
		this.tapping.setImage(TuxGuitar.getInstance().getIconManager().getEffectTapping());
		this.slapping.setImage(TuxGuitar.getInstance().getIconManager().getEffectSlapping());
		this.popping.setImage(TuxGuitar.getInstance().getIconManager().getEffectPopping());
		this.fadeIn.setImage(TuxGuitar.getInstance().getIconManager().getEffectFadeIn());
	}
}
