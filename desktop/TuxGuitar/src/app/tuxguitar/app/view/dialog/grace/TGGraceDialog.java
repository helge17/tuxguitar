package app.tuxguitar.app.view.dialog.grace;

import java.util.Iterator;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.effect.TGChangeGraceNoteAction;
import app.tuxguitar.player.base.MidiPercussionKey;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVelocities;
import app.tuxguitar.song.models.effects.TGEffectGrace;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICheckBox;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIRadioButton;
import app.tuxguitar.ui.widget.UISpinner;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGGraceDialog {

	private UISpinner fretSpinner;
	private UICheckBox deadButton;
	private UIRadioButton beforeBeatButton;
	private UIRadioButton onBeatButton;
	private UIRadioButton durationButton64;
	private UIRadioButton durationButton32;
	private UIRadioButton durationButton16;
	private UIRadioButton pppButton;
	private UIRadioButton ppButton;
	private UIRadioButton pButton;
	private UIRadioButton mpButton;
	private UIRadioButton mfButton;
	private UIRadioButton fButton;
	private UIRadioButton ffButton;
	private UIRadioButton fffButton;
	private UIRadioButton noneButton;
	private UIRadioButton slideButton;
	private UIRadioButton bendButton;
	private UIRadioButton hammerButton;

	public TGGraceDialog(){
		super();
	}

	public void show(final TGViewContext context){
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGTrack track = (TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		TGEffectGrace grace = null;
		TGFactory factory = TuxGuitar.getInstance().getSongManager().getFactory();

		if((noteRange != null) && !noteRange.isEmpty()) {
			final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
			final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
			final UITableLayout dialogLayout = new UITableLayout();
			final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

			dialog.setLayout(dialogLayout);
			dialog.setText(TuxGuitar.getProperty("effects.grace-editor"));

			// look for first note with grace effect within selection to initialize dialog
			Iterator<TGNote> it = noteRange.getNotes().iterator();
			while (it.hasNext() && (grace == null)) {
				TGNote n = it.next();
				if (n.getEffect().isGrace()) {
					grace = n.getEffect().getGrace();
				}
			}
			if (grace == null) {
				// nothing found, create new
				grace = factory.newEffectGrace();
				// initialize fret value with first note (arbitrarily)
				if (!noteRange.getNotes().isEmpty()) {
					grace.setFret(noteRange.getNotes().get(0).getValue());
				}
			}

			//-----init-------------------------------------------------
			boolean dead = grace.isDead();
			boolean onBeat = grace.isOnBeat();
			int fret = grace.getFret();
			int duration = grace.getDuration();
			int dynamic = grace.getDynamic();
			int transition = grace.getTransition();

			//---------------------------------------------------
			//------------------NOTE-----------------------------
			//---------------------------------------------------
			UITableLayout noteLayout = new UITableLayout();
			UILegendPanel noteGroup = uiFactory.createLegendPanel(dialog);
			noteGroup.setLayout(noteLayout);
			noteGroup.setText(TuxGuitar.getProperty("note"));
			dialogLayout.set(noteGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 2, 350f, null, null);

			UILabel fretLabel = uiFactory.createLabel(noteGroup);
			fretLabel.setText(TuxGuitar.getProperty("fret") + ": ");
			noteLayout.set(fretLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

			this.fretSpinner = uiFactory.createSpinner(noteGroup);
			this.fretSpinner.setValue(fret);
			if (track.isPercussion()) {
				MidiPercussionKey[] percussionKeys = TuxGuitar.getInstance().getPlayer().getPercussionKeys();
				int max=0;
				int min=-1;
				for (MidiPercussionKey key : percussionKeys) {
					max = key.getValue() > max ? key.getValue() : max;
					min = (min<0 || key.getValue()<min) ? key.getValue() : min;
				}
				this.fretSpinner.setMinimum(min);
				this.fretSpinner.setMaximum(max);
			} else {
				this.fretSpinner.setMaximum(track.getMaxFret());
			}
			noteLayout.set(this.fretSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.deadButton = uiFactory.createCheckBox(noteGroup);
			this.deadButton.setText(TuxGuitar.getProperty("note.deadnote"));
			this.deadButton.setSelected(dead);
			noteLayout.set(this.deadButton, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 2);

			//---------------------------------------------------
			//------------------POSITION-------------------------
			//---------------------------------------------------
			UITableLayout positionLayout = new UITableLayout();
			UILegendPanel positionGroup = uiFactory.createLegendPanel(dialog);
			positionGroup.setLayout(positionLayout);
			positionGroup.setText(TuxGuitar.getProperty("position"));
			dialogLayout.set(positionGroup, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 2);

			this.beforeBeatButton = uiFactory.createRadioButton(positionGroup);
			this.beforeBeatButton.setText(TuxGuitar.getProperty("effects.grace.before-beat"));
			this.beforeBeatButton.setSelected(!onBeat);
			positionLayout.set(this.beforeBeatButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.onBeatButton = uiFactory.createRadioButton(positionGroup);
			this.onBeatButton.setText(TuxGuitar.getProperty("effects.grace.on-beat"));
			this.onBeatButton.setSelected(onBeat);
			positionLayout.set(this.onBeatButton, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			//---------------------------------------------------
			//------------------DURATION-------------------------
			//---------------------------------------------------
			UITableLayout durationLayout = new UITableLayout();
			UILegendPanel durationGroup = uiFactory.createLegendPanel(dialog);
			durationGroup.setLayout(durationLayout);
			durationGroup.setText(TuxGuitar.getProperty("duration"));
			dialogLayout.set(durationGroup, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 2);

			this.durationButton64 = uiFactory.createRadioButton(durationGroup);
			this.durationButton64.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTY_FOURTH));
			this.durationButton64.setSelected(duration == TGEffectGrace.DURATION_SIXTY_FOURTH);
			durationLayout.set(this.durationButton64, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.durationButton32 = uiFactory.createRadioButton(durationGroup);
			this.durationButton32.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.THIRTY_SECOND));
			this.durationButton32.setSelected(duration == TGEffectGrace.DURATION_THIRTY_SECOND);
			durationLayout.set(this.durationButton32, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.durationButton16 = uiFactory.createRadioButton(durationGroup);
			this.durationButton16.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTEENTH));
			this.durationButton16.setSelected(duration == TGEffectGrace.DURATION_SIXTEENTH);
			durationLayout.set(this.durationButton16, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			//---------------------------------------------------
			//------------------DYNAMIC--------------------------
			//---------------------------------------------------
			UITableLayout dynamicLayout = new UITableLayout();
			UILegendPanel dynamicGroup = uiFactory.createLegendPanel(dialog);
			dynamicGroup.setLayout(dynamicLayout);
			dynamicGroup.setText(TuxGuitar.getProperty("dynamic"));
			dialogLayout.set(dynamicGroup, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.pppButton = uiFactory.createRadioButton(dynamicGroup);
			this.pppButton.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_PPP));
			this.pppButton.setSelected(dynamic == TGVelocities.PIANO_PIANISSIMO);
			dynamicLayout.set(this.pppButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.mfButton = uiFactory.createRadioButton(dynamicGroup);
			this.mfButton.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_MF));
			this.mfButton.setSelected(dynamic == TGVelocities.MEZZO_FORTE);
			dynamicLayout.set(this.mfButton, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.ppButton = uiFactory.createRadioButton(dynamicGroup);
			this.ppButton.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_PP));
			this.ppButton.setSelected(dynamic == TGVelocities.PIANISSIMO);
			dynamicLayout.set(this.ppButton, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.fButton = uiFactory.createRadioButton(dynamicGroup);
			this.fButton.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_F));
			this.fButton.setSelected(dynamic == TGVelocities.FORTE);
			dynamicLayout.set(this.fButton, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.pButton = uiFactory.createRadioButton(dynamicGroup);
			this.pButton.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_P));
			this.pButton.setSelected(dynamic == TGVelocities.PIANO);
			dynamicLayout.set(this.pButton, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.ffButton = uiFactory.createRadioButton(dynamicGroup);
			this.ffButton.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_FF));
			this.ffButton.setSelected(dynamic == TGVelocities.FORTISSIMO);
			dynamicLayout.set(this.ffButton, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.mpButton = uiFactory.createRadioButton(dynamicGroup);
			this.mpButton.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_MP));
			this.mpButton.setSelected(dynamic == TGVelocities.MEZZO_PIANO);
			dynamicLayout.set(this.mpButton, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.fffButton = uiFactory.createRadioButton(dynamicGroup);
			this.fffButton.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.DYNAMIC_FFF));
			this.fffButton.setSelected(dynamic == TGVelocities.FORTE_FORTISSIMO);
			dynamicLayout.set(this.fffButton, 4, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			//---------------------------------------------------
			//------------------TRANSITION-----------------------
			//---------------------------------------------------
			UITableLayout transitionLayout = new UITableLayout();
			UILegendPanel transitionGroup = uiFactory.createLegendPanel(dialog);
			transitionGroup.setLayout(transitionLayout);
			transitionGroup.setText(TuxGuitar.getProperty("effects.grace.transition"));
			dialogLayout.set(transitionGroup, 4, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.noneButton = uiFactory.createRadioButton(transitionGroup);
			this.noneButton.setText(TuxGuitar.getProperty("effects.grace.transition-none"));
			this.noneButton.setSelected(transition == TGEffectGrace.TRANSITION_NONE);
			transitionLayout.set(this.noneButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.bendButton = uiFactory.createRadioButton(transitionGroup);
			this.bendButton.setText(TuxGuitar.getProperty("effects.grace.transition-bend"));
			this.bendButton.setSelected(transition == TGEffectGrace.TRANSITION_BEND);
			transitionLayout.set(this.bendButton, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.slideButton = uiFactory.createRadioButton(transitionGroup);
			this.slideButton.setText(TuxGuitar.getProperty("effects.grace.transition-slide"));
			this.slideButton.setSelected(transition == TGEffectGrace.TRANSITION_SLIDE);
			transitionLayout.set(this.slideButton, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			this.hammerButton = uiFactory.createRadioButton(transitionGroup);
			this.hammerButton.setText(TuxGuitar.getProperty("effects.grace.transition-hammer"));
			this.hammerButton.setSelected(transition == TGEffectGrace.TRANSITION_HAMMER);
			transitionLayout.set(this.hammerButton, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			//---------------------------------------------------
			//------------------BUTTONS--------------------------
			//---------------------------------------------------
			UITableLayout buttonsLayout = new UITableLayout(0f);
			UIPanel buttons = uiFactory.createPanel(dialog, false);
			buttons.setLayout(buttonsLayout);
			dialogLayout.set(buttons, 5, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true, 1, 2);

			final UIButton buttonOK = uiFactory.createButton(buttons);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setDefaultButton();
			buttonOK.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					changeGrace(context.getContext(), noteRange, getGrace());
					dialog.dispose();
				}
			});
			buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

			UIButton buttonClean = uiFactory.createButton(buttons);
			buttonClean.setText(TuxGuitar.getProperty("clean"));
			buttonClean.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					changeGrace(context.getContext(), noteRange, null);
					dialog.dispose();
				}
			});
			buttonsLayout.set(buttonClean, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

			UIButton buttonCancel = uiFactory.createButton(buttons);
			buttonCancel.setText(TuxGuitar.getProperty("cancel"));
			buttonCancel.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					dialog.dispose();
				}
			});
			buttonsLayout.set(buttonCancel, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
			buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);

			TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
		}
	}

	public TGEffectGrace getGrace(){
		TGEffectGrace effect = TuxGuitar.getInstance().getSongManager().getFactory().newEffectGrace();

		effect.setFret(this.fretSpinner.getValue());
		effect.setDead(this.deadButton.isSelected());
		effect.setOnBeat(this.onBeatButton.isSelected());

		//duration
		if(this.durationButton64.isSelected()){
			effect.setDuration(TGEffectGrace.DURATION_SIXTY_FOURTH);
		}else if(this.durationButton32.isSelected()){
			effect.setDuration(TGEffectGrace.DURATION_THIRTY_SECOND);
		}else if(this.durationButton16.isSelected()){
			effect.setDuration(TGEffectGrace.DURATION_SIXTEENTH);
		}
		//velocity
		if(this.pppButton.isSelected()){
			effect.setDynamic(TGVelocities.PIANO_PIANISSIMO);
		}else if(this.ppButton.isSelected()){
			effect.setDynamic(TGVelocities.PIANISSIMO);
		}else if(this.pButton.isSelected()){
			effect.setDynamic(TGVelocities.PIANO);
		}else if(this.mpButton.isSelected()){
			effect.setDynamic(TGVelocities.MEZZO_PIANO);
		}else if(this.mfButton.isSelected()){
			effect.setDynamic(TGVelocities.MEZZO_FORTE);
		}else if(this.fButton.isSelected()){
			effect.setDynamic(TGVelocities.FORTE);
		}else if(this.ffButton.isSelected()){
			effect.setDynamic(TGVelocities.FORTISSIMO);
		}else if(this.fffButton.isSelected()){
			effect.setDynamic(TGVelocities.FORTE_FORTISSIMO);
		}

		//transition
		if(this.noneButton.isSelected()){
			effect.setTransition(TGEffectGrace.TRANSITION_NONE);
		}else if(this.slideButton.isSelected()){
			effect.setTransition(TGEffectGrace.TRANSITION_SLIDE);
		}else if(this.bendButton.isSelected()){
			effect.setTransition(TGEffectGrace.TRANSITION_BEND);
		}else if(this.hammerButton.isSelected()){
			effect.setTransition(TGEffectGrace.TRANSITION_HAMMER);
		}

		return effect;
	}

	public void changeGrace(TGContext context, TGNoteRange noteRange, TGEffectGrace effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeGraceNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE, noteRange);
		tgActionProcessor.setAttribute(TGChangeGraceNoteAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}
