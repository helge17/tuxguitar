package app.tuxguitar.app.view.dialog.tremolopicking;

import java.util.Iterator;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.effect.TGChangeTremoloPickingAction;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIRadioButton;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGTremoloPickingDialog {

	private UIRadioButton thirtySecondButton;
	private UIRadioButton sixTeenthButton;
	private UIRadioButton eighthButton;

	public TGTremoloPickingDialog(){
		super();
	}

	public void show(final TGViewContext context){
		final TGNoteRange noteRange = (TGNoteRange) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);

		if( (noteRange != null) && !noteRange.isEmpty() ) {
			final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
			final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
			final UITableLayout dialogLayout = new UITableLayout();
			final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

			dialog.setLayout(dialogLayout);
			dialog.setText(TuxGuitar.getProperty("effects.tremolo-picking-editor"));

			// look for first note with tremoloPicking effect within selection to initialize dialog
			int duration = 0;
			Iterator<TGNote> it = noteRange.getNotes().iterator();
			while (it.hasNext() && (duration == 0)) {
				TGNote n = it.next();
				if (n.getEffect().isTremoloPicking()) {
					duration = n.getEffect().getTremoloPicking().getDuration().getValue();
				}
			}
			if (duration == 0) {
				// nothing found, use default
				duration = TGDuration.EIGHTH;
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
					changeTremoloPicking(context.getContext(), noteRange, getTremoloPicking());
					dialog.dispose();
				}
			});
			buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

			UIButton buttonClean = uiFactory.createButton(buttons);
			buttonClean.setText(TuxGuitar.getProperty("clean"));
			buttonClean.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					changeTremoloPicking(context.getContext(), noteRange, null);
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

	public void changeTremoloPicking(TGContext context, TGNoteRange noteRange, TGEffectTremoloPicking effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeTremoloPickingAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE, noteRange);
		tgActionProcessor.setAttribute(TGChangeTremoloPickingAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}
