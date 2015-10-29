package org.herac.tuxguitar.app.view.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenBendDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenGraceDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenHarmonicDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTremoloBarDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTremoloPickingDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTrillDialogAction;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
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
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				createMenu(toolBar, (ToolItem) event.widget);
			}
		});
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar){
		this.menuItem.setToolTipText(toolBar.getText("effects"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.menuItem.setImage(toolBar.getIconManager().getEffectVibrato());
	}
	
	public void updateItems(TGToolBar toolBar){
		boolean running = MidiPlayer.getInstance(toolBar.getContext()).isRunning();
		TGNote note = TablatureEditor.getInstance(toolBar.getContext()).getTablature().getCaret().getSelectedNote();
		
		this.menuItem.setEnabled(!running && note != null);
	}
	
	public void createMenu(TGToolBar toolBar, ToolItem item) {
		boolean running = MidiPlayer.getInstance(toolBar.getContext()).isRunning();
		TGNote note = TablatureEditor.getInstance(toolBar.getContext()).getTablature().getCaret().getSelectedNote();
		
		Menu menu = new Menu(item.getParent().getShell());
		
		//--DEAD NOTE--
		MenuItem deadNote = new MenuItem(menu, SWT.PUSH);
		deadNote.addSelectionListener(toolBar.createActionProcessor(TGChangeDeadNoteAction.NAME));
		deadNote.setEnabled(!running && note != null);
		deadNote.setText(toolBar.getText("effects.deadnote", (note != null && note.getEffect().isDeadNote())));
		deadNote.setImage(TuxGuitar.getInstance().getIconManager().getEffectDead());
		
		//--GHOST NOTE--
		MenuItem ghostNote = new MenuItem(menu, SWT.PUSH);
		ghostNote.addSelectionListener(toolBar.createActionProcessor(TGChangeGhostNoteAction.NAME));
		ghostNote.setEnabled(!running && note != null);
		ghostNote.setImage(TuxGuitar.getInstance().getIconManager().getEffectGhost());
		ghostNote.setText(toolBar.getText("effects.ghostnote", (note != null && note.getEffect().isGhostNote())));
		
		//--ACCENTUATED NOTE--
		MenuItem accentuatedNote = new MenuItem(menu, SWT.PUSH);
		accentuatedNote.addSelectionListener(toolBar.createActionProcessor(TGChangeAccentuatedNoteAction.NAME));
		accentuatedNote.setEnabled(!running && note != null);
		accentuatedNote.setText(toolBar.getText("effects.accentuatednote", (note != null && note.getEffect().isAccentuatedNote())));
		accentuatedNote.setImage(TuxGuitar.getInstance().getIconManager().getEffectAccentuated());
		
		//--HEAVY ACCENTUATED NOTE--
		MenuItem heavyAccentuatedNote = new MenuItem(menu, SWT.PUSH);
		heavyAccentuatedNote.addSelectionListener(toolBar.createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME));
		heavyAccentuatedNote.setEnabled(!running && note != null);
		heavyAccentuatedNote.setText(toolBar.getText("effects.heavyaccentuatednote", (note != null && note.getEffect().isHeavyAccentuatedNote())));
		heavyAccentuatedNote.setImage(TuxGuitar.getInstance().getIconManager().getEffectHeavyAccentuated());
		
		//--HARMONIC NOTE--
		MenuItem harmonicNote = new MenuItem(menu, SWT.PUSH);
		harmonicNote.addSelectionListener(toolBar.createActionProcessor(TGOpenHarmonicDialogAction.NAME));
		harmonicNote.setEnabled(!running && note != null);
		harmonicNote.setText(toolBar.getText("effects.harmonic", (note != null && note.getEffect().isHarmonic())));
		harmonicNote.setImage(TuxGuitar.getInstance().getIconManager().getEffectHarmonic());
		
		//--GRACE NOTE--
		MenuItem graceNote = new MenuItem(menu, SWT.PUSH);
		graceNote.addSelectionListener(toolBar.createActionProcessor(TGOpenGraceDialogAction.NAME));
		graceNote.setEnabled(!running && note != null);
		graceNote.setText(toolBar.getText("effects.grace", (note != null && note.getEffect().isGrace())));
		graceNote.setImage(TuxGuitar.getInstance().getIconManager().getEffectGrace());
		
		//--SEPARATOR--
		new MenuItem(menu, SWT.SEPARATOR);
		
		//--VIBRATO--
		MenuItem vibrato = new MenuItem(menu, SWT.PUSH);
		vibrato.addSelectionListener(toolBar.createActionProcessor(TGChangeVibratoNoteAction.NAME));
		vibrato.setEnabled(!running && note != null);
		vibrato.setText(toolBar.getText("effects.vibrato", (note != null && note.getEffect().isVibrato())));
		vibrato.setImage(TuxGuitar.getInstance().getIconManager().getEffectVibrato());
		
		//--BEND--
		MenuItem bend = new MenuItem(menu, SWT.PUSH);
		bend.addSelectionListener(toolBar.createActionProcessor(TGOpenBendDialogAction.NAME));
		bend.setEnabled(!running && note != null);
		bend.setText(toolBar.getText("effects.bend", (note != null && note.getEffect().isBend())));
		bend.setImage(TuxGuitar.getInstance().getIconManager().getEffectBend());
		
		//--BEND--
		MenuItem tremoloBar = new MenuItem(menu, SWT.PUSH);
		tremoloBar.addSelectionListener(toolBar.createActionProcessor(TGOpenTremoloBarDialogAction.NAME));
		tremoloBar.setEnabled(!running && note != null);
		tremoloBar.setText(toolBar.getText("effects.tremolo-bar", (note != null && note.getEffect().isTremoloBar())));
		tremoloBar.setImage(TuxGuitar.getInstance().getIconManager().getEffectTremoloBar());
		
		//--SLIDE--
		MenuItem slide = new MenuItem(menu, SWT.PUSH);
		slide.addSelectionListener(toolBar.createActionProcessor(TGChangeSlideNoteAction.NAME));
		slide.setEnabled(!running && note != null);
		slide.setText(toolBar.getText("effects.slide", (note != null && note.getEffect().isSlide())));
		slide.setImage(TuxGuitar.getInstance().getIconManager().getEffectSlide());
		
		//--HAMMER--
		MenuItem hammer = new MenuItem(menu, SWT.PUSH);
		hammer.addSelectionListener(toolBar.createActionProcessor(TGChangeHammerNoteAction.NAME));
		hammer.setEnabled(!running && note != null);
		hammer.setText(toolBar.getText("effects.hammer", (note != null && note.getEffect().isHammer())));
		hammer.setImage(TuxGuitar.getInstance().getIconManager().getEffectHammer());
		
		//--SEPARATOR--
		new MenuItem(menu, SWT.SEPARATOR);
		
		//--TRILL--
		MenuItem trill = new MenuItem(menu, SWT.PUSH);
		trill.addSelectionListener(toolBar.createActionProcessor(TGOpenTrillDialogAction.NAME));
		trill.setEnabled(!running && note != null);
		trill.setText(toolBar.getText("effects.trill", (note != null && note.getEffect().isTrill())));
		trill.setImage(TuxGuitar.getInstance().getIconManager().getEffectTrill());
		
		//--TREMOLO PICKING--
		MenuItem tremoloPicking = new MenuItem(menu, SWT.PUSH);
		tremoloPicking.addSelectionListener(toolBar.createActionProcessor(TGOpenTremoloPickingDialogAction.NAME));
		tremoloPicking.setEnabled(!running && note != null);
		tremoloPicking.setText(toolBar.getText("effects.tremolo-picking", (note != null && note.getEffect().isTremoloPicking())));
		tremoloPicking.setImage(TuxGuitar.getInstance().getIconManager().getEffectTremoloPicking());
		
		//--PALM MUTE--
		MenuItem palmMute = new MenuItem(menu, SWT.PUSH);
		palmMute.addSelectionListener(toolBar.createActionProcessor(TGChangePalmMuteAction.NAME));
		palmMute.setEnabled(!running && note != null);
		palmMute.setText(toolBar.getText("effects.palm-mute", (note != null && note.getEffect().isPalmMute())));
		palmMute.setImage(TuxGuitar.getInstance().getIconManager().getEffectPalmMute());
		
		//--STACCATO
		MenuItem staccato = new MenuItem(menu, SWT.PUSH);
		staccato.addSelectionListener(toolBar.createActionProcessor(TGChangeStaccatoAction.NAME));
		staccato.setEnabled(!running && note != null);
		staccato.setText(toolBar.getText("effects.staccato", (note != null && note.getEffect().isStaccato())));
		staccato.setImage(TuxGuitar.getInstance().getIconManager().getEffectStaccato());
		
		//--SEPARATOR--
		new MenuItem(menu, SWT.SEPARATOR);
		
		//--TAPPING
		MenuItem tapping = new MenuItem(menu, SWT.PUSH);
		tapping.addSelectionListener(toolBar.createActionProcessor(TGChangeTappingAction.NAME));
		tapping.setEnabled(!running && note != null);
		tapping.setText(toolBar.getText("effects.tapping", (note != null && note.getEffect().isTapping())));
		tapping.setImage(TuxGuitar.getInstance().getIconManager().getEffectTapping());
		
		//--SLAPPING
		MenuItem slapping = new MenuItem(menu, SWT.PUSH);
		slapping.addSelectionListener(toolBar.createActionProcessor(TGChangeSlappingAction.NAME));
		slapping.setEnabled(!running && note != null);
		slapping.setText(toolBar.getText("effects.slapping", (note != null && note.getEffect().isSlapping())));
		slapping.setImage(TuxGuitar.getInstance().getIconManager().getEffectSlapping());
		
		//--POPPING
		MenuItem popping = new MenuItem(menu, SWT.PUSH);
		popping.addSelectionListener(toolBar.createActionProcessor(TGChangePoppingAction.NAME));
		popping.setEnabled(!running && note != null);
		popping.setText(toolBar.getText("effects.popping", (note != null && note.getEffect().isPopping())));
		popping.setImage(TuxGuitar.getInstance().getIconManager().getEffectPopping());
		
		//--SEPARATOR--
		new MenuItem(menu, SWT.SEPARATOR);
		
		//--FADE IN
		MenuItem fadeIn = new MenuItem(menu, SWT.PUSH);
		fadeIn.addSelectionListener(toolBar.createActionProcessor(TGChangeFadeInAction.NAME));
		fadeIn.setEnabled(!running && note != null);
		fadeIn.setText(toolBar.getText("effects.fade-in", (note != null && note.getEffect().isFadeIn())));
		fadeIn.setImage(TuxGuitar.getInstance().getIconManager().getEffectFadeIn());

		
		Rectangle rect = item.getBounds();
		Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
		
		menu.setLocation(pt.x, pt.y + rect.height);
		menu.setVisible(true);
	}
}
