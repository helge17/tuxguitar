package org.herac.tuxguitar.app.view.dialog.repeat;

import java.util.Iterator;

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
import org.herac.tuxguitar.editor.action.composition.TGRepeatAlternativeAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGRepeatAlternativeDialog {
	
	public void show(final TGViewContext context) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		
		final int existentEndings = getExistentEndings(song, header);
		final int selectedEndings = (header.getRepeatAlternative() > 0 ? header.getRepeatAlternative() : getDefaultEndings(existentEndings));
		
		final Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("repeat.alternative.editor"));
		
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(4,true));
		group.setLayoutData(getMainData());
		group.setText(TuxGuitar.getProperty("repeat.alternative"));
		
		final Button[] selections = new Button[8];
		for(int i = 0; i < selections.length; i ++){
			boolean enabled = ((existentEndings & (1 << i)) == 0);
			selections[i] = new Button(group,SWT.CHECK);
			selections[i].setText(Integer.toString( i + 1 ));
			selections[i].setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			selections[i].setEnabled(enabled);
			selections[i].setSelection(enabled && ((selectedEndings & (1 << i)) != 0)  );
		}
		
		//----------------------BUTTONS--------------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(3,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				int values = 0;
				for(int i = 0; i < selections.length; i ++){
					values |=  (  (selections[i].getSelection()) ? (1 << i) : 0  );
				}
				changeRepeatAlternative(context.getContext(), song, header, values);
				dialog.dispose();
			}
		});
		Button buttonClean = new Button(buttons, SWT.PUSH);
		buttonClean.setText(TuxGuitar.getProperty("clean"));
		buttonClean.setLayoutData(getButtonData());
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				changeRepeatAlternative(context.getContext(), song, header, 0);
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
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	private GridData getMainData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 350;
		return data;
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected int getExistentEndings(TGSong song, TGMeasureHeader header){
		int existentEndings = 0;
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader currentHeader = (TGMeasureHeader)it.next();
			if( currentHeader.getNumber() == header.getNumber() ){
				break;
			}
			if( currentHeader.isRepeatOpen() ){
				existentEndings = 0;
			}
			existentEndings |= currentHeader.getRepeatAlternative();
		}
		return existentEndings;
	}
	
	protected int getDefaultEndings(int existentEndings){
		for(int i = 0; i < 8; i ++){
			if((existentEndings & (1 << i)) == 0){
				return (1 << i);
			}
		}
		return -1;
	}
	
	public void changeRepeatAlternative(TGContext context, TGSong song, TGMeasureHeader header, Integer repeatAlternative) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGRepeatAlternativeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGRepeatAlternativeAction.ATTRIBUTE_REPEAT_ALTERNATIVE, repeatAlternative);
		tgActionProcessor.processOnNewThread();
	}
}
