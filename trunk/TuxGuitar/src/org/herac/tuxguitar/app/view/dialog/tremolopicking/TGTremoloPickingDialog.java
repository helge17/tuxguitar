package org.herac.tuxguitar.app.view.dialog.tremolopicking;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeTremoloPickingAction;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import org.herac.tuxguitar.util.TGContext;

public class TGTremoloPickingDialog extends SelectionAdapter{
	
	private Button thirtySecondButton;
	private Button sixTeenthButton;
	private Button eighthButton;
	
	public TGTremoloPickingDialog(){
		super();
	}
	
	public void show(final TGViewContext context){
		final TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final TGString string = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
		final TGNote note = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		if( measure != null && beat != null && note != null && string != null ) {
			final Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
			final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			
			dialog.setLayout(new GridLayout());
			dialog.setText(TuxGuitar.getProperty("effects.tremolo-picking-editor"));
			
			Composite composite = new Composite(dialog,SWT.NONE);
			composite.setLayout(new GridLayout());
			composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			
			int horizontalSpan = 2;
			
			//-----defaults-------------------------------------------------
			int duration = TGDuration.EIGHTH;
			if(note.getEffect().isTremoloPicking()){
				duration = note.getEffect().getTremoloPicking().getDuration().getValue();
			}
			
			//---------------------------------------------------
			//------------------DURATION-------------------------
			//---------------------------------------------------
			Group durationGroup = makeGroup(composite,horizontalSpan, TuxGuitar.getProperty("duration"));
			durationGroup.setLayout(new GridLayout(3,false));
			
			this.thirtySecondButton = new Button(durationGroup,SWT.RADIO);
			this.thirtySecondButton.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.THIRTY_SECOND));
			this.thirtySecondButton.setLayoutData(makeGridData(1));
			this.thirtySecondButton.setSelection(duration == TGDuration.THIRTY_SECOND);
			
			this.sixTeenthButton = new Button(durationGroup,SWT.RADIO);
			this.sixTeenthButton.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTEENTH));
			this.sixTeenthButton.setLayoutData(makeGridData(1));
			this.sixTeenthButton.setSelection(duration == TGDuration.SIXTEENTH);
			
			this.eighthButton = new Button(durationGroup,SWT.RADIO);
			this.eighthButton.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.EIGHTH));
			this.eighthButton.setLayoutData(makeGridData(1));
			this.eighthButton.setSelection(duration == TGDuration.EIGHTH);
			//---------------------------------------------------
			//------------------BUTTONS--------------------------
			//---------------------------------------------------
			Composite buttons = new Composite(dialog, SWT.NONE);
			buttons.setLayout(new GridLayout(3,false));
			buttons.setLayoutData(new GridData(SWT.END,SWT.BOTTOM,true,true));
			
			final Button buttonOK = new Button(buttons, SWT.PUSH);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setLayoutData(getButtonData());
			buttonOK.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					changeTremoloPicking(context.getContext(), measure, beat, string, getTremoloPicking());
					dialog.dispose();
				}
			});
			
			Button buttonClean = new Button(buttons, SWT.PUSH);
			buttonClean.setText(TuxGuitar.getProperty("clean"));
			buttonClean.setLayoutData(getButtonData());
			buttonClean.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					changeTremoloPicking(context.getContext(), measure, beat, string, null);
					dialog.dispose();
				}
			});
			
			Button buttonCancel = new Button(buttons, SWT.PUSH);
			buttonCancel.setText(TuxGuitar.getProperty("cancel"));
			buttonCancel.setLayoutData(getButtonData());
			buttonCancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					dialog.dispose();
				}
			});
			
			dialog.setDefaultButton( buttonOK );
			
			DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
		}
	}
	
	private Group makeGroup(Composite parent,int horizontalSpan,String text){
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(makeGridData(horizontalSpan));
		group.setText(text);
		
		return group;
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	private GridData makeGridData(int horizontalSpan){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.horizontalSpan = horizontalSpan;
		return data;
	}
	
	public TGEffectTremoloPicking getTremoloPicking(){
		TGEffectTremoloPicking effect = TuxGuitar.getInstance().getSongManager().getFactory().newEffectTremoloPicking();
		if(this.thirtySecondButton.getSelection()){
			effect.getDuration().setValue(TGDuration.THIRTY_SECOND);
		}else if(this.sixTeenthButton.getSelection()){
			effect.getDuration().setValue(TGDuration.SIXTEENTH);
		}else if(this.eighthButton.getSelection()){
			effect.getDuration().setValue(TGDuration.EIGHTH);
		}else{
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
