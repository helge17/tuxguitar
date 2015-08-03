/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.menu.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenBendDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenGraceDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenHarmonicDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTremoloBarDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTremoloPickingDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTrillDialogAction;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.editor.action.effect.TGChangeAccentuatedNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeDeadNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeFadeInAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeGhostNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeHammerNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeHeavyAccentuatedNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeLetRingAction;
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
public class NoteEffectsMenuItem extends TGMenuItem{
	
	private MenuItem noteEffectsMenuItem;
	private Menu menu; 
	private MenuItem vibrato;
	private MenuItem bend;
	private MenuItem tremoloBar;
	private MenuItem deadNote;
	private MenuItem slide;
	private MenuItem hammer;
	private MenuItem ghostNote;
	private MenuItem accentuatedNote;
	private MenuItem heavyAccentuatedNote;
	private MenuItem letRing;
	private MenuItem harmonicNote;
	private MenuItem graceNote;
	private MenuItem trill;
	private MenuItem tremoloPicking;
	private MenuItem palmMute;
	private MenuItem staccato;
	private MenuItem tapping;
	private MenuItem slapping;
	private MenuItem popping;
	
	private MenuItem fadeIn;
	
	public NoteEffectsMenuItem(Shell shell,Menu parent, int style) {
		this.noteEffectsMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems(){
		//--VIBRATO--
		this.vibrato = new MenuItem(this.menu, SWT.CHECK);
		this.vibrato.addSelectionListener(this.createActionProcessor(TGChangeVibratoNoteAction.NAME));
		
		//--BEND--
		this.bend = new MenuItem(this.menu, SWT.CHECK);
		this.bend.addSelectionListener(this.createActionProcessor(TGOpenBendDialogAction.NAME));
		
		//--BEND--
		this.tremoloBar = new MenuItem(this.menu, SWT.CHECK);
		this.tremoloBar.addSelectionListener(this.createActionProcessor(TGOpenTremoloBarDialogAction.NAME));
		
		//--SLIDE--
		this.slide = new MenuItem(this.menu, SWT.CHECK);
		this.slide.addSelectionListener(this.createActionProcessor(TGChangeSlideNoteAction.NAME));
		
		//--SLIDE--
		this.deadNote = new MenuItem(this.menu, SWT.CHECK);
		this.deadNote.addSelectionListener(this.createActionProcessor(TGChangeDeadNoteAction.NAME));
		
		//--HAMMER--
		this.hammer = new MenuItem(this.menu, SWT.CHECK);
		this.hammer.addSelectionListener(this.createActionProcessor(TGChangeHammerNoteAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--GHOST NOTE--
		this.ghostNote = new MenuItem(this.menu, SWT.CHECK);
		this.ghostNote.addSelectionListener(this.createActionProcessor(TGChangeGhostNoteAction.NAME));
		
		//--ACCENTUATED NOTE--
		this.accentuatedNote = new MenuItem(this.menu, SWT.CHECK);
		this.accentuatedNote.addSelectionListener(this.createActionProcessor(TGChangeAccentuatedNoteAction.NAME));
		
		//--HEAVY ACCENTUATED NOTE--
		this.heavyAccentuatedNote = new MenuItem(this.menu, SWT.CHECK);
		this.heavyAccentuatedNote.addSelectionListener(this.createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME));
		
		//--LET RING--
		this.letRing = new MenuItem(this.menu, SWT.CHECK);
		this.letRing.addSelectionListener(this.createActionProcessor(TGChangeLetRingAction.NAME));
		
		//--HARMONIC NOTE--
		this.harmonicNote = new MenuItem(this.menu, SWT.CHECK);
		this.harmonicNote.addSelectionListener(this.createActionProcessor(TGOpenHarmonicDialogAction.NAME));
		
		//--GRACE NOTE--
		this.graceNote = new MenuItem(this.menu, SWT.CHECK);
		this.graceNote.addSelectionListener(this.createActionProcessor(TGOpenGraceDialogAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--TRILL--
		this.trill = new MenuItem(this.menu, SWT.CHECK);
		this.trill.addSelectionListener(this.createActionProcessor(TGOpenTrillDialogAction.NAME));
		
		//--TREMOLO PICKING--
		this.tremoloPicking = new MenuItem(this.menu, SWT.CHECK);
		this.tremoloPicking.addSelectionListener(this.createActionProcessor(TGOpenTremoloPickingDialogAction.NAME));
		
		//--PALM MUTE--
		this.palmMute = new MenuItem(this.menu, SWT.CHECK);
		this.palmMute.addSelectionListener(this.createActionProcessor(TGChangePalmMuteAction.NAME));
		
		//--STACCATO
		this.staccato = new MenuItem(this.menu, SWT.CHECK);
		this.staccato.addSelectionListener(this.createActionProcessor(TGChangeStaccatoAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--TAPPING
		this.tapping = new MenuItem(this.menu, SWT.CHECK);
		this.tapping.addSelectionListener(this.createActionProcessor(TGChangeTappingAction.NAME));
		
		//--SLAPPING
		this.slapping = new MenuItem(this.menu, SWT.CHECK);
		this.slapping.addSelectionListener(this.createActionProcessor(TGChangeSlappingAction.NAME));
		
		//--POPPING
		this.popping = new MenuItem(this.menu, SWT.CHECK);
		this.popping.addSelectionListener(this.createActionProcessor(TGChangePoppingAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--FADE IN
		this.fadeIn = new MenuItem(this.menu, SWT.CHECK);
		this.fadeIn.addSelectionListener(this.createActionProcessor(TGChangeFadeInAction.NAME));
		
		this.noteEffectsMenuItem.setMenu(this.menu);
		
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
		this.letRing.setSelection(note != null && note.getEffect().isLetRing());
		this.letRing.setEnabled(!running && note != null);
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
		//Nothing to do
	}
}
