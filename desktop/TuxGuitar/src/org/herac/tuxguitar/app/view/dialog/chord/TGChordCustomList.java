package org.herac.tuxguitar.app.view.dialog.chord;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIListBoxSelect;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;

public class TGChordCustomList {
	
	private static final float MAXIMUM_LIST_PACKED_HEIGHT = 200f;
	
	private TGChordDialog dialog;
	private UIPanel control;
	private UIListBoxSelect<Integer> chords;
	
	public TGChordCustomList(TGChordDialog dialog, UIContainer parent) {
		this.dialog = dialog;
		this.createControl(parent);
	}
	
	public void createControl(UIContainer parent) {
		final UIFactory uiFactory = this.dialog.getUIFactory();
		UITableLayout layout = new UITableLayout(0f);
		
		this.control = uiFactory.createPanel(parent, true);
		this.control.setLayout(layout);
		
		UITableLayout compositeLayout = new UITableLayout();
		UIPanel composite = uiFactory.createPanel(this.control, false);
		composite.setLayout(compositeLayout);
		layout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.chords = uiFactory.createListBoxSelect(composite);
		this.chords.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				Integer index = getChords().getSelectedValue();
				if( index != null && getDialog().getEditor() != null) {
					showChord(index);
				}
			}
		});
		compositeLayout.set(this.chords, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		compositeLayout.set(this.chords, UITableLayout.MAXIMUM_PACKED_HEIGHT, MAXIMUM_LIST_PACKED_HEIGHT);
		
		//-------------BUTTONS-----------------------------
		UITableLayout buttonsLayout = new UITableLayout();
		UIPanel buttons = uiFactory.createPanel(this.control, false);
		buttons.setLayout(buttonsLayout);
		layout.set(buttons, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		
		UIButton add = uiFactory.createButton(buttons);
		add.setText(TuxGuitar.getProperty("add"));
		add.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				addCustomChord();
			}
		});
		buttonsLayout.set(add, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		
		UIButton rename = uiFactory.createButton(buttons);
		rename.setText(TuxGuitar.getProperty("rename"));
		rename.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				Integer index = getChords().getSelectedValue();
				if( index != null ) {
					renameCustomChord(index);
				}
			}
		});
		buttonsLayout.set(rename, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		
		UIButton remove = uiFactory.createButton(buttons);
		remove.setText(TuxGuitar.getProperty("remove"));
		remove.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				Integer index = getChords().getSelectedValue();
				if( index != null ) {
					removeCustomChord(index);
				}
			}
		});
		buttonsLayout.set(remove, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		
		loadChords();
	}
	
	public void loadChords(){
		this.chords.setIgnoreEvents(true);
		
		Integer selectionIndex = this.chords.getSelectedValue();
		if( selectionIndex == null ) {
			selectionIndex = 0;
		}
		this.chords.removeItems();
		
		for(int i = 0; i < TuxGuitar.getInstance().getCustomChordManager().countChords(); i ++){
			TGChord chord = TuxGuitar.getInstance().getCustomChordManager().getChord(i);
			
			this.chords.addItem(new UISelectItem<Integer>(chord.getName(), i));
		}
		
		if( selectionIndex >= 0 && selectionIndex < this.chords.getItemCount()){
			this.chords.setSelectedValue(selectionIndex);
		}else if(selectionIndex > 0 && (selectionIndex - 1) < this.chords.getItemCount()){
			this.chords.setSelectedValue((selectionIndex - 1));
		}
		this.chords.setIgnoreEvents(false);
	}
	
	public void showChord(int index) {
		TGChord chord = TuxGuitar.getInstance().getCustomChordManager().getChord(index);
		if (chord != null) {
			this.dialog.getEditor().setChord(chord);
		}
	}
	
	public void addCustomChord(){
		final TGChord chord = this.dialog.getEditor().getChord();
		if( chord != null ){
			TGChordCustomNameChooser nameChooser = new TGChordCustomNameChooser(this.dialog.getContext().getContext());
			nameChooser.setDefaultName(this.dialog.getEditor().getChordName().getText().trim());
			nameChooser.choose(this.dialog.getWindow(), new TGChordCustomNameChooserHandler() {
				public void onSelectName(String name) {
					if( name != null ) {
						if(name.length() == 0){
							TGMessageDialogUtil.errorMessage(getDialog().getContext().getContext(), getDialog().getWindow(), TuxGuitar.getProperty("chord.custom.name-empty-error"));
							return;
						}
						if(TuxGuitar.getInstance().getCustomChordManager().existOtherEqualCustomChord(name,-1)){
							TGMessageDialogUtil.errorMessage(getDialog().getContext().getContext(), getDialog().getWindow(), TuxGuitar.getProperty("chord.custom.name-exist-error"));
							return;
						}
						chord.setName(name);
						TuxGuitar.getInstance().getCustomChordManager().addChord(chord);
						loadChords();
					}
				}
			});
		}
	}
	
	public void renameCustomChord(final int index){
		TGChord chord =  TuxGuitar.getInstance().getCustomChordManager().getChord(index);
		if( chord != null){
			TGChordCustomNameChooser nameChooser = new TGChordCustomNameChooser(this.dialog.getContext().getContext());
			nameChooser.setDefaultName(chord.getName());
			nameChooser.choose(this.dialog.getWindow(), new TGChordCustomNameChooserHandler() {
				public void onSelectName(String name) {
					if(name != null){
						if(name.length() == 0){
							TGMessageDialogUtil.errorMessage(getDialog().getContext().getContext(), getDialog().getWindow(), TuxGuitar.getProperty("chord.custom.name-empty-error"));
							return;
						}
						if(TuxGuitar.getInstance().getCustomChordManager().existOtherEqualCustomChord(name,index)){
							TGMessageDialogUtil.errorMessage(getDialog().getContext().getContext(), getDialog().getWindow(), TuxGuitar.getProperty("chord.custom.name-exist-error"));
							return;
						}
						TuxGuitar.getInstance().getCustomChordManager().renameChord(index, name);
						loadChords();
					}
				}
			});
		}
	}
	
	public void removeCustomChord(int index){
		if (index >= 0 && index < TuxGuitar.getInstance().getCustomChordManager().countChords()) {
			TuxGuitar.getInstance().getCustomChordManager().removeChord(index);
			loadChords();
		}
	}
	
	public TGChordDialog getDialog(){
		return this.dialog;
	}
	
	public UIPanel getControl() {
		return control;
	}
	
	public UIListBoxSelect<Integer> getChords(){
		return this.chords;
	}
}