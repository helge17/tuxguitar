package org.herac.tuxguitar.app.view.dialog.trill;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeTrillNoteAction;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.effects.TGEffectTrill;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIRadioButton;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGTrillDialog {
	
	private UISpinner fretSpinner;
	private UIRadioButton sixtyFourthButton;
	private UIRadioButton thirtySecondButton;
	private UIRadioButton sixTeenthButton;
	
	public TGTrillDialog(){
		super();
	}
	
	public void show(final TGViewContext context){
		final TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final TGString string = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
		final TGNote note = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		if( measure != null && beat != null && note != null && string != null ) {
			final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
			final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
			final UITableLayout dialogLayout = new UITableLayout();
			final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
			
			dialog.setLayout(dialogLayout);
			dialog.setText(TuxGuitar.getProperty("effects.trill-editor"));
			
			//-----defaults-------------------------------------------------
			int fret = note.getValue();
			int duration = TGDuration.SIXTEENTH;
			if(note.getEffect().isTrill()){
				fret = note.getEffect().getTrill().getFret();
				duration = note.getEffect().getTrill().getDuration().getValue();
			}
			//---------------------------------------------------
			//------------------NOTE-----------------------------
			//---------------------------------------------------
			UITableLayout noteLayout = new UITableLayout();
			UILegendPanel noteGroup = uiFactory.createLegendPanel(dialog);
			noteGroup.setText(TuxGuitar.getProperty("note"));
			noteGroup.setLayout(noteLayout);
			dialogLayout.set(noteGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			UILabel fretLabel = uiFactory.createLabel(noteGroup);
			fretLabel.setText(TuxGuitar.getProperty("fret") + ": ");
			noteLayout.set(fretLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
			
			this.fretSpinner = uiFactory.createSpinner(noteGroup);
			this.fretSpinner.setValue(fret);
			this.fretSpinner.setMaximum(30);
			this.fretSpinner.setMinimum(0);
			noteLayout.set(this.fretSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			//---------------------------------------------------
			//------------------DURATION-------------------------
			//---------------------------------------------------
			UITableLayout durationLayout = new UITableLayout();
			UILegendPanel durationGroup = uiFactory.createLegendPanel(dialog);
			durationGroup.setText(TuxGuitar.getProperty("duration"));
			durationGroup.setLayout(durationLayout);
			dialogLayout.set(durationGroup, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.sixtyFourthButton = uiFactory.createRadioButton(durationGroup);
			this.sixtyFourthButton.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTY_FOURTH));
			this.sixtyFourthButton.setSelected(duration == TGDuration.SIXTY_FOURTH);
			durationLayout.set(this.sixtyFourthButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.thirtySecondButton = uiFactory.createRadioButton(durationGroup);
			this.thirtySecondButton.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.THIRTY_SECOND));
			this.thirtySecondButton.setSelected(duration == TGDuration.THIRTY_SECOND);
			durationLayout.set(this.thirtySecondButton, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.sixTeenthButton = uiFactory.createRadioButton(durationGroup);
			this.sixTeenthButton.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTEENTH));
			this.sixTeenthButton.setSelected(duration == TGDuration.SIXTEENTH);
			durationLayout.set(this.sixTeenthButton, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			//---------------------------------------------------
			//------------------BUTTONS--------------------------
			//---------------------------------------------------
			UITableLayout buttonsLayout = new UITableLayout(0f);
			UIPanel buttons = uiFactory.createPanel(dialog, false);
			buttons.setLayout(buttonsLayout);
			dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
			
			final UIButton buttonOK = uiFactory.createButton(buttons);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setDefaultButton();
			buttonOK.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					changeTrill(context.getContext(), measure, beat, string, getTrill());
					dialog.dispose();
				}
			});
			buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
			
			UIButton buttonClean = uiFactory.createButton(buttons);
			buttonClean.setText(TuxGuitar.getProperty("clean"));
			buttonClean.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					changeTrill(context.getContext(), measure, beat, string, null);
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
	
	public TGEffectTrill getTrill(){
		TGEffectTrill tgEffect = TuxGuitar.getInstance().getSongManager().getFactory().newEffectTrill();
		tgEffect.setFret(this.fretSpinner.getValue());
		if(this.sixtyFourthButton.isSelected()) {
			tgEffect.getDuration().setValue(TGDuration.SIXTY_FOURTH);
		} else if(this.thirtySecondButton.isSelected()) {
			tgEffect.getDuration().setValue(TGDuration.THIRTY_SECOND);
		} else if(this.sixTeenthButton.isSelected()) {
			tgEffect.getDuration().setValue(TGDuration.SIXTEENTH);
		} else {
			return null;
		}
		return tgEffect;
	}
	
	public void changeTrill(TGContext context, TGMeasure measure, TGBeat beat, TGString string, TGEffectTrill effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeTrillNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		tgActionProcessor.setAttribute(TGChangeTrillNoteAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}
