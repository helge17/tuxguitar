package org.herac.tuxguitar.app.view.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
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

public class TGToolBarSectionEffect implements TGToolBarSection {
	
	private ToolItem menuItem;
	
	private Menu menu;
	private MenuItem deadNote;
	private MenuItem ghostNote;
	private MenuItem accentuatedNote;
	private MenuItem heavyAccentuatedNote;
	private MenuItem harmonicNote;
	private MenuItem graceNote;
	private MenuItem vibrato;
	private MenuItem bend;
	private MenuItem tremoloBar;
	private MenuItem slide;
	private MenuItem hammer;
	private MenuItem trill;
	private MenuItem tremoloPicking;
	private MenuItem palmMute;
	private MenuItem staccato;
	private MenuItem tapping;
	private MenuItem slapping;
	private MenuItem popping;
	private MenuItem fadeIn;
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				displayMenu();
			}
		});
		
		this.menu = new Menu(this.menuItem.getParent().getShell());
		
		//--DEAD NOTE--
		this.deadNote = new MenuItem(this.menu, SWT.PUSH);
		this.deadNote.addSelectionListener(toolBar.createActionProcessor(TGChangeDeadNoteAction.NAME));
		
		//--GHOST NOTE--
		this.ghostNote = new MenuItem(this.menu, SWT.PUSH);
		this.ghostNote.addSelectionListener(toolBar.createActionProcessor(TGChangeGhostNoteAction.NAME));
		
		//--ACCENTUATED NOTE--
		this.accentuatedNote = new MenuItem(this.menu, SWT.PUSH);
		this.accentuatedNote.addSelectionListener(toolBar.createActionProcessor(TGChangeAccentuatedNoteAction.NAME));
		
		//--HEAVY ACCENTUATED NOTE--
		this.heavyAccentuatedNote = new MenuItem(this.menu, SWT.PUSH);
		this.heavyAccentuatedNote.addSelectionListener(toolBar.createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME));
		
		//--HARMONIC NOTE--
		this.harmonicNote = new MenuItem(this.menu, SWT.PUSH);
		this.harmonicNote.addSelectionListener(toolBar.createActionProcessor(TGOpenHarmonicDialogAction.NAME));
		
		//--GRACE NOTE--
		this.graceNote = new MenuItem(this.menu, SWT.PUSH);
		this.graceNote.addSelectionListener(toolBar.createActionProcessor(TGOpenGraceDialogAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--VIBRATO--
		this.vibrato = new MenuItem(this.menu, SWT.PUSH);
		this.vibrato.addSelectionListener(toolBar.createActionProcessor(TGChangeVibratoNoteAction.NAME));
		
		//--BEND--
		this.bend = new MenuItem(this.menu, SWT.PUSH);
		this.bend.addSelectionListener(toolBar.createActionProcessor(TGOpenBendDialogAction.NAME));
		
		//--BEND--
		this.tremoloBar = new MenuItem(this.menu, SWT.PUSH);
		this.tremoloBar.addSelectionListener(toolBar.createActionProcessor(TGOpenTremoloBarDialogAction.NAME));
		
		//--SLIDE--
		this.slide = new MenuItem(this.menu, SWT.PUSH);
		this.slide.addSelectionListener(toolBar.createActionProcessor(TGChangeSlideNoteAction.NAME));
		
		//--HAMMER--
		this.hammer = new MenuItem(this.menu, SWT.PUSH);
		this.hammer.addSelectionListener(toolBar.createActionProcessor(TGChangeHammerNoteAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--TRILL--
		this.trill = new MenuItem(this.menu, SWT.PUSH);
		this.trill.addSelectionListener(toolBar.createActionProcessor(TGOpenTrillDialogAction.NAME));
		
		//--TREMOLO PICKING--
		this.tremoloPicking = new MenuItem(this.menu, SWT.PUSH);
		this.tremoloPicking.addSelectionListener(toolBar.createActionProcessor(TGOpenTremoloPickingDialogAction.NAME));
		
		//--PALM MUTE--
		this.palmMute = new MenuItem(this.menu, SWT.PUSH);
		this.palmMute.addSelectionListener(toolBar.createActionProcessor(TGChangePalmMuteAction.NAME));
		
		//--STACCATO
		this.staccato = new MenuItem(this.menu, SWT.PUSH);
		this.staccato.addSelectionListener(toolBar.createActionProcessor(TGChangeStaccatoAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--TAPPING
		this.tapping = new MenuItem(this.menu, SWT.PUSH);
		this.tapping.addSelectionListener(toolBar.createActionProcessor(TGChangeTappingAction.NAME));
		
		//--SLAPPING
		this.slapping = new MenuItem(this.menu, SWT.PUSH);
		this.slapping.addSelectionListener(toolBar.createActionProcessor(TGChangeSlappingAction.NAME));
		
		//--POPPING
		this.popping = new MenuItem(this.menu, SWT.PUSH);
		this.popping.addSelectionListener(toolBar.createActionProcessor(TGChangePoppingAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--FADE IN
		this.fadeIn = new MenuItem(this.menu, SWT.PUSH);
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
	
	public void displayMenu() {
		Rectangle rect = this.menuItem.getBounds();
		Point pt = this.menuItem.getParent().toDisplay(new Point(rect.x, rect.y));
		
		this.menu.setLocation(pt.x, pt.y + rect.height);
		this.menu.setVisible(true);
	}
}
