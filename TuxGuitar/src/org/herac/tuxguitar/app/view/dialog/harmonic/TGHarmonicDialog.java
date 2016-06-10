package org.herac.tuxguitar.app.view.dialog.harmonic;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeHarmonicNoteAction;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIRadioButton;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGHarmonicDialog {
	
	private static final String TYPE_DATA = "type";

	public static final float WIDTH = 400;
	
	protected UIDropDownSelect<Integer> harmonicType;
	protected UIDropDownSelect<Integer> harmonicDataCombo;
	protected UIRadioButton[] typeButtons;
	
	public TGHarmonicDialog(){
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
			dialog.setText(TuxGuitar.getProperty("effects.harmonic-editor"));
			
			//---------------------------------------------------------------------
			//------------HARMONIC-------------------------------------------------
			//---------------------------------------------------------------------
			UITableLayout groupLayout = new UITableLayout();
			UILegendPanel group = uiFactory.createLegendPanel(dialog);
			group.setLayout(groupLayout);
			group.setText(TuxGuitar.getProperty("effects.harmonic.type-of-harmonic"));
			dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, null, null, WIDTH, null, null);
			
			this.typeButtons = new UIRadioButton[5];
			UISelectionListener listener = new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					update(note, getSelectedType());
				}
			};
			
			// Natural
			String label = "[" + TGEffectHarmonic.KEY_NATURAL + "] " + TuxGuitar.getProperty("effects.harmonic.natural");
			initButton(uiFactory, group, listener, 0, TGEffectHarmonic.TYPE_NATURAL, label);
			
			// Artificial
			label = ("[" + TGEffectHarmonic.KEY_ARTIFICIAL + "] " + TuxGuitar.getProperty("effects.harmonic.artificial"));
			initButton(uiFactory, group, listener, 1, TGEffectHarmonic.TYPE_ARTIFICIAL, label);
			
			// Tapped
			label = ("[" + TGEffectHarmonic.KEY_TAPPED + "] " + TuxGuitar.getProperty("effects.harmonic.tapped"));
			initButton(uiFactory, group, listener, 2, TGEffectHarmonic.TYPE_TAPPED, label);
			
			// Pinch
			label = ("[" + TGEffectHarmonic.KEY_PINCH + "] " + TuxGuitar.getProperty("effects.harmonic.pinch"));
			initButton(uiFactory, group, listener, 3, TGEffectHarmonic.TYPE_PINCH, label);
			
			// Semi
			label = ("[" + TGEffectHarmonic.KEY_SEMI + "] " + TuxGuitar.getProperty("effects.harmonic.semi"));
			initButton(uiFactory, group, listener, 4, TGEffectHarmonic.TYPE_SEMI, label);
			
			this.harmonicDataCombo = uiFactory.createDropDownSelect(group);
			groupLayout.set(this.harmonicDataCombo, 6, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			//---------------------------------------------------
			//------------------BUTTONS--------------------------
			//---------------------------------------------------
			UITableLayout buttonsLayout = new UITableLayout(0f);
			UIPanel buttons = uiFactory.createPanel(dialog, false);
			buttons.setLayout(buttonsLayout);
			dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_BOTTOM, true, true);
			
			UIButton buttonOK = uiFactory.createButton(buttons);
			buttonOK.setDefaultButton();
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					changeHarmonic(context.getContext(), measure, beat, string, getHarmonic());
					dialog.dispose();
				}
			});
			buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
			
			UIButton buttonClean = uiFactory.createButton(buttons);
			buttonClean.setText(TuxGuitar.getProperty("clean"));
			buttonClean.setEnabled( note.getEffect().isHarmonic());
			buttonClean.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					changeHarmonic(context.getContext(), measure, beat, string, null);
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
			
			this.initDefaults(note);
			
			TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
		}
	}
	
	private void initButton(UIFactory uiFactory, UILayoutContainer parent, UISelectionListener listener, int index, int type, String label){
		this.typeButtons[index] = uiFactory.createRadioButton(parent);
		this.typeButtons[index].setText(label);
		this.typeButtons[index].setData(TYPE_DATA, type);
		this.typeButtons[index].addSelectionListener(listener);
		
		UITableLayout uiLayout = (UITableLayout) parent.getLayout();
		uiLayout.set(this.typeButtons[index], (index + 1), 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
	}
	
	protected void initDefaults(TGNote note){
		int type = TGEffectHarmonic.TYPE_NATURAL;
		if(note.getEffect().isHarmonic()){
			type = note.getEffect().getHarmonic().getType();
		}
		else{
			boolean naturalValid = false;
			for(int i = 0;i < TGEffectHarmonic.NATURAL_FREQUENCIES.length;i ++){
				if((note.getValue() % 12) == (TGEffectHarmonic.NATURAL_FREQUENCIES[i][0] % 12)  ){
					naturalValid = true;
					break;
				}
			}
			if(!naturalValid){
				this.typeButtons[0].setEnabled(false);
				type = TGEffectHarmonic.TYPE_ARTIFICIAL;
			}
			
		}
		for(int i = 0; i < this.typeButtons.length; i ++){
			int data = ((Integer)this.typeButtons[i].getData(TYPE_DATA)).intValue();
			this.typeButtons[i].setSelected((data == type));
		}
		update(note,type);
	}
	
	protected int getSelectedType(){
		for(int i = 0; i < this.typeButtons.length; i ++){
			if(this.typeButtons[i].isSelected()){
				return ((Integer)this.typeButtons[i].getData(TYPE_DATA)).intValue();
			}
		}
		return 0;
	}
	
	protected void update(TGNote note,int type){
		TGEffectHarmonic h = note.getEffect().getHarmonic();
		this.harmonicDataCombo.removeItems();
		this.harmonicDataCombo.setEnabled(type != TGEffectHarmonic.TYPE_NATURAL);
		if( type != TGEffectHarmonic.TYPE_NATURAL ){
			String label = getTypeLabel(type);
			for(int i = 0; i < TGEffectHarmonic.NATURAL_FREQUENCIES.length;i ++){
				this.harmonicDataCombo.addItem(new UISelectItem<Integer>(label + "(" + Integer.toString(TGEffectHarmonic.NATURAL_FREQUENCIES[i][0]) + ")", i));
			}
			this.harmonicDataCombo.setSelectedValue((h != null && h.getType() == type)?h.getData():0);
		}
	}
	
	private String getTypeLabel(int type){
		if(type == TGEffectHarmonic.TYPE_NATURAL){
			return TGEffectHarmonic.KEY_NATURAL;
		}
		if(type == TGEffectHarmonic.TYPE_ARTIFICIAL){
			return TGEffectHarmonic.KEY_ARTIFICIAL;
		}
		if(type == TGEffectHarmonic.TYPE_TAPPED){
			return TGEffectHarmonic.KEY_TAPPED;
		}
		if(type == TGEffectHarmonic.TYPE_PINCH){
			return TGEffectHarmonic.KEY_PINCH;
		}
		if(type == TGEffectHarmonic.TYPE_SEMI){
			return TGEffectHarmonic.KEY_SEMI;
		}
		return new String();
	}
	
	public TGEffectHarmonic getHarmonic(){
		int type = getSelectedType();
		if(	type > 0 ){
			Integer data = this.harmonicDataCombo.getSelectedValue();
			TGEffectHarmonic effect = TuxGuitar.getInstance().getSongManager().getFactory().newEffectHarmonic();
			effect.setType(type);
			effect.setData(data != null ? data : 0);
			return effect;
		}
		return null;
	}
	
	public void changeHarmonic(TGContext context, TGMeasure measure, TGBeat beat, TGString string, TGEffectHarmonic effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeHarmonicNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		tgActionProcessor.setAttribute(TGChangeHarmonicNoteAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}
