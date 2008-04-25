/*
 * Created on 02-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.editors.chord;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.song.models.TGChord;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ChordCustomList extends Composite {
	
	private ChordDialog dialog;
	private List chords;
	
	public ChordCustomList(ChordDialog dialog,Composite parent,int style,int height) {
		super(parent,style);
		this.setLayout(dialog.gridLayout(1,false,0,0));
		this.setLayoutData(makeGridData(height));
		this.dialog = dialog;
		this.init();
	}
	
	public GridData makeGridData(int height){
		GridData data = new GridData(SWT.FILL,SWT.TOP,true,true);
		data.heightHint = height;
		
		return data;
	}
	
	public void init(){
		Composite composite = new Composite(this,SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.chords = new List(composite,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		this.chords.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.chords.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(getDialog().getEditor() != null){
					showChord(getChords().getSelectionIndex());
				}
			}
		});
		
		//-------------BUTTONS-----------------------------
		Composite buttons = new Composite(this,SWT.NONE);
		buttons.setLayout(new GridLayout(3,false));
		
		Button add = new Button(buttons,SWT.PUSH);
		add.setText(TuxGuitar.getProperty("add"));
		add.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				addCustomChord();
			}
		});
		
		Button rename = new Button(buttons,SWT.PUSH);
		rename.setText(TuxGuitar.getProperty("rename"));
		rename.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				renameCustomChord(getChords().getSelectionIndex());
			}
		});
		
		Button remove = new Button(buttons,SWT.PUSH);
		remove.setText(TuxGuitar.getProperty("remove"));
		remove.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				removeCustomChord(getChords().getSelectionIndex());
			}
		});
		
		loadChords();
	}
	
	private void loadChords(){
		int selectionIndex = this.chords.getSelectionIndex();
		this.chords.removeAll();
		
		for(int i = 0;i < TuxGuitar.instance().getCustomChordManager().countChords();i ++){
			TGChord chord = TuxGuitar.instance().getCustomChordManager().getChord(i);
			if(chord != null){
				this.chords.add(chord.getName());
			}
		}
		
		if(selectionIndex >= 0 && selectionIndex < this.chords.getItemCount()){
			this.chords.select(selectionIndex);
		}else if(selectionIndex > 0 && (selectionIndex - 1) < this.chords.getItemCount()){
			this.chords.select((selectionIndex - 1));
		}
	}
	
	protected void showChord(int index) {
		TGChord chord = TuxGuitar.instance().getCustomChordManager().getChord(index);
		if (chord != null) {
			this.dialog.getEditor().setChord(chord);
		}
	}
	
	protected void addCustomChord(){
		TGChord chord = this.dialog.getEditor().getChord();
		if(chord != null){
			NameDialog nDialog = new NameDialog();
			nDialog.name = this.dialog.getEditor().getChordName().getText().trim();
			String name = nDialog.open();
			if(name != null){
				if(name.length() == 0){
					MessageDialog.errorMessage(getShell(),TuxGuitar.getProperty("chord.custom.name-empty-error"));
					return;
				}
				if(TuxGuitar.instance().getCustomChordManager().existOtherEqualCustomChord(name,-1)){
					MessageDialog.errorMessage(getShell(),TuxGuitar.getProperty("chord.custom.name-exist-error"));
					return;
				}
				chord.setName(name);
				TuxGuitar.instance().getCustomChordManager().addChord(chord);
				loadChords();
			}
		}
	}
	
	protected void renameCustomChord(int index){
		TGChord chord =  TuxGuitar.instance().getCustomChordManager().getChord(index);
		if(chord != null){
			String name = new NameDialog(chord.getName()).open();
			if(name != null){
				if(name.length() == 0){
					MessageDialog.errorMessage(getShell(),TuxGuitar.getProperty("chord.custom.name-empty-error"));
					return;
				}
				if(TuxGuitar.instance().getCustomChordManager().existOtherEqualCustomChord(name,index)){
					MessageDialog.errorMessage(getShell(),TuxGuitar.getProperty("chord.custom.name-exist-error"));
					return;
				}
				TuxGuitar.instance().getCustomChordManager().renameChord(index,name);
				loadChords();
			}
		}
	}
	
	protected void removeCustomChord(int index){
		if (index >= 0 && index < TuxGuitar.instance().getCustomChordManager().countChords()) {
			TuxGuitar.instance().getCustomChordManager().removeChord(index);
			loadChords();
		}
	}
	
	protected ChordDialog getDialog(){
		return this.dialog;
	}
	
	protected List getChords(){
		return this.chords;
	}
	
	private class NameDialog{
		protected String name;
		
		public NameDialog(String name){
			this.name = name;
		}
		
		public NameDialog(){
			this(new String());
		}
		
		public String open(){
			final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			dialog.setLayout(new GridLayout());
			dialog.setText(TuxGuitar.getProperty("chord.custom"));
			
			Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			group.setLayout(new GridLayout());
			group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			group.setText(TuxGuitar.getProperty("chord.custom"));
			
			Composite composite = new Composite(group, SWT.NONE);
			composite.setLayout(new GridLayout(2,false));
			composite.setLayoutData(getMainData());
			
			final Label label = new Label(composite,SWT.LEFT);
			label.setText(TuxGuitar.getProperty("chord.name") + ":");
			label.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
			
			final Text text = new Text(composite,SWT.BORDER | SWT.SINGLE);
			text.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			text.setText(this.name);
			
			//------------------BUTTONS--------------------------
			Composite buttons = new Composite(dialog, SWT.NONE);
			buttons.setLayout(new GridLayout(2,false));
			buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
			
			final Button buttonOK = new Button(buttons, SWT.PUSH);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setLayoutData(getButtonData());
			buttonOK.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					NameDialog.this.name = text.getText();
					dialog.dispose();
				}
			});
			
			Button buttonCancel = new Button(buttons, SWT.PUSH);
			buttonCancel.setText(TuxGuitar.getProperty("cancel"));
			buttonCancel.setLayoutData(getButtonData());
			buttonCancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					NameDialog.this.name = null;
					dialog.dispose();
				}
			});
			
			dialog.setDefaultButton( buttonOK );
			
			DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
			
			return this.name;
		}
		
		private GridData getMainData(){
			GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
			data.minimumWidth = 300;
			return data;
		}
		
		private GridData getButtonData(){
			GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			data.minimumWidth = 80;
			data.minimumHeight = 25;
			return data;
		}
	}
}