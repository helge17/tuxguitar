/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.composition;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.undo.undoables.custom.UndoableChangeInfo;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.song.models.TGSong;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeInfoAction extends Action{
	public static final String NAME = "action.composition.change-info";
	
	private static final int TEXT_WIDTH = 300;
	
	public ChangeInfoAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		showDialog(getEditor().getTablature().getShell());
		return 0;
	}
	
	public void showDialog(Shell shell) {
		TGSong song = getSongManager().getSong();
		if (song != null) {
			final Shell dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			
			dialog.setLayout(new GridLayout());
			dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			dialog.setText(TuxGuitar.getProperty("composition.properties"));
			
			Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			group.setLayout(makeGroupLayout(5));
			group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			group.setText(TuxGuitar.getProperty("composition.properties"));
			
			//-------NAME------------------------------------
			Label nameLabel = new Label(group, SWT.NULL);
			nameLabel.setLayoutData(makeLabelData()); 
			nameLabel.setText(TuxGuitar.getProperty("composition.name") + ":");
			
			final Text name = new Text(group, SWT.BORDER);
			name.setLayoutData(makeTextData());
			name.setText(song.getName());
			//-------ARTIST------------------------------------
			Label artistLabel = new Label(group, SWT.NULL);
			artistLabel.setLayoutData(makeLabelData());
			artistLabel.setText(TuxGuitar.getProperty("composition.artist") + ":");
			
			final Text artist = new Text(group, SWT.BORDER);
			artist.setLayoutData(makeTextData());
			artist.setText(song.getArtist());
			//-------ALBUM------------------------------------
			Label albumLabel = new Label(group, SWT.NULL);
			albumLabel.setLayoutData(makeLabelData());
			albumLabel.setText(TuxGuitar.getProperty("composition.album") + ":");
			
			final Text album = new Text(group, SWT.BORDER);
			album.setLayoutData(makeTextData());
			album.setText(song.getAlbum());
			//-------AUTHOR------------------------------------
			Label authorLabel = new Label(group, SWT.NULL);
			authorLabel.setLayoutData(makeLabelData());
			authorLabel.setText(TuxGuitar.getProperty("composition.author") + ":");
			
			final Text author = new Text(group, SWT.BORDER);
			author.setLayoutData(makeTextData());
			author.setText(song.getAuthor());
			
			//------------------BUTTONS--------------------------
			Composite buttons = new Composite(dialog, SWT.NONE);
			buttons.setLayout(new GridLayout(2,false));
			buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
			
			final Button buttonOK = new Button(buttons, SWT.PUSH);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setLayoutData(getButtonData());
			buttonOK.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					setProperties(name.getText(),artist.getText(),album.getText(),author.getText());
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
			
			DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		}
	}
	
	private GridLayout makeGroupLayout(int spacing){
		GridLayout layout = new GridLayout(2,false);
		layout.marginTop = spacing;
		layout.marginBottom = spacing;
		layout.marginLeft = spacing;
		layout.marginRight = spacing;
		layout.verticalSpacing = spacing;
		layout.horizontalSpacing = spacing;
		return layout;
	}
	
	private GridData makeTextData(){
		return new GridData(TEXT_WIDTH,SWT.DEFAULT);
	}
	
	private GridData makeLabelData(){
		return new GridData(SWT.RIGHT,SWT.CENTER,true,true);
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected void setProperties(String name,String artist,String album,String author){
		//comienza el undoable
		UndoableChangeInfo undoable = UndoableChangeInfo.startUndo();
		
		getSongManager().setProperties(name,artist,album,author);
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		TuxGuitar.instance().showTitle();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
	}
}
