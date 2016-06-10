package org.herac.tuxguitar.app.view.dialog.tremolopicking;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeTremoloPickingAction;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIRadioButton;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGTremoloPickingDialog {
	
	private UIRadioButton thirtySecondButton;
	private UIRadioButton sixTeenthButton;
	private UIRadioButton eighthButton;
	
	public TGTremoloPickingDialog(){
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
			dialog.setText(TuxGuitar.getProperty("effects.tremolo-picking-editor"));
			
			//-----defaults-------------------------------------------------
			int duration = TGDuration.EIGHTH;
			if(note.getEffect().isTremoloPicking()){
				duration = note.getEffect().getTremoloPicking().getDuration().getValue();
			}
			
			//---------------------------------------------------
			//------------------DURATION-------------------------
			//---------------------------------------------------
			UITableLayout durationLayout = new UITableLayout();
			UILegendPanel durationGroup = uiFactory.createLegendPanel(dialog);
			durationGroup.setText(TuxGuitar.getProperty("duration"));
			durationGroup.setLayout(durationLayout);
			dialogLayout.set(durationGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.thirtySecondButton = uiFactory.createRadioButton(durationGroup);
			this.thirtySecondButton.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.THIRTY_SECOND));
			this.thirtySecondButton.setSelected(duration == TGDuration.THIRTY_SECOND);
			durationLayout.set(this.thirtySecondButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.sixTeenthButton = uiFactory.createRadioButton(durationGroup);
			this.sixTeenthButton.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTEENTH));
			this.sixTeenthButton.setSelected(duration == TGDuration.SIXTEENTH);
			durationLayout.set(this.sixTeenthButton, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.eighthButton = uiFactory.createRadioButton(durationGroup);
			this.eighthButton.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.EIGHTH));
			this.eighthButton.setSelected(duration == TGDuration.EIGHTH);
			durationLayout.set(this.eighthButton, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			//---------------------------------------------------
			//------------------BUTTONS--------------------------
			//---------------------------------------------------
			UITableLayout buttonsLayout = new UITableLayout(0f);
			UIPanel buttons = uiFactory.createPanel(dialog, false);
			buttons.setLayout(buttonsLayout);
			dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
			
			final UIButton buttonOK = uiFactory.createButton(buttons);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setDefaultButton();
			buttonOK.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					changeTremoloPicking(context.getContext(), measure, beat, string, getTremoloPicking());
					dialog.dispose();
				}
			});
			buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
			
			UIButton buttonClean = uiFactory.createButton(buttons);
			buttonClean.setText(TuxGuitar.getProperty("clean"));
			buttonClean.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					changeTremoloPicking(context.getContext(), measure, beat, string, null);
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
	
	public TGEffectTremoloPicking getTremoloPicking(){
		TGEffectTremoloPicking effect = TuxGuitar.getInstance().getSongManager().getFactory().newEffectTremoloPicking();
		if(this.thirtySecondButton.isSelected()) {
			effect.getDuration().setValue(TGDuration.THIRTY_SECOND);
		} else if(this.sixTeenthButton.isSelected()) {
			effect.getDuration().setValue(TGDuration.SIXTEENTH);
		} else if(this.eighthButton.isSelected()) {
			effect.getDuration().setValue(TGDuration.EIGHTH);
		} else {
			return null;
		}
		return effect;
	}
	
	public void changeTremoloPicking(TGContext context, TGMeasure measure, TGBeat beat, TGString string, TGEffectTremoloPicking effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeTremoloPickingAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		tgActionProcessor.setAttribute(TGChangeTremoloPickingAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}
