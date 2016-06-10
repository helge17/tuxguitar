package org.herac.tuxguitar.app.view.dialog.repeat;

import java.util.Iterator;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGRepeatAlternativeAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGRepeatAlternativeDialog {
	
	public void show(final TGViewContext context) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		
		final int existentEndings = getExistentEndings(song, header);
		final int selectedEndings = (header.getRepeatAlternative() > 0 ? header.getRepeatAlternative() : getDefaultEndings(existentEndings));
		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("repeat.alternative.editor"));
		
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("repeat.alternative"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 350f, null, null);		
		
		final UICheckBox[] selections = new UICheckBox[8];
		for(int i = 0; i < selections.length; i ++){
			boolean enabled = ((existentEndings & (1 << i)) == 0);
			selections[i] = uiFactory.createCheckBox(group);
			selections[i].setText(Integer.toString( i + 1 ));
			selections[i].setEnabled(enabled);
			selections[i].setSelected(enabled && ((selectedEndings & (1 << i)) != 0));
			groupLayout.set(selections[i], (1 + (int)(i / 4)), (1 + (i - (i & 4))), UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		}
		
		//----------------------BUTTONS--------------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		final UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int values = 0;
				for(int i = 0; i < selections.length; i ++){
					values |=  (  (selections[i].isSelected()) ? (1 << i) : 0  );
				}
				changeRepeatAlternative(context.getContext(), song, header, values);
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonClean = uiFactory.createButton(buttons);
		buttonClean.setText(TuxGuitar.getProperty("clean"));
		buttonClean.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				changeRepeatAlternative(context.getContext(), song, header, 0);
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
		
		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
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
