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
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.undo.undoables.custom.UndoableChangeInfo;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeInfoAction extends Action{
	public static final String NAME = "action.composition.change-info";
	
	private static final int GROUP_WIDTH  = 450;
	private static final int GROUP_HEIGHT = SWT.DEFAULT;
	
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
			group.setLayoutData(new GridData(GROUP_WIDTH,GROUP_HEIGHT));
			group.setText(TuxGuitar.getProperty("composition.properties"));
			
			//-------NAME------------------------------------
			Label nameLabel = new Label(group, SWT.NULL);
			nameLabel.setLayoutData(makeLabelData()); 
			nameLabel.setText(TuxGuitar.getProperty("composition.name") + ":");
			
			final Text nameText = new Text(group, SWT.BORDER);
			nameText.setLayoutData(makeTextData());
			nameText.setText(song.getName());
			//-------ARTIST------------------------------------
			Label artistLabel = new Label(group, SWT.NULL);
			artistLabel.setLayoutData(makeLabelData());
			artistLabel.setText(TuxGuitar.getProperty("composition.artist") + ":");
			
			final Text artistText = new Text(group, SWT.BORDER);
			artistText.setLayoutData(makeTextData());
			artistText.setText(song.getArtist());
			//-------ALBUM------------------------------------
			Label albumLabel = new Label(group, SWT.NULL);
			albumLabel.setLayoutData(makeLabelData());
			albumLabel.setText(TuxGuitar.getProperty("composition.album") + ":");
			
			final Text albumText = new Text(group, SWT.BORDER);
			albumText.setLayoutData(makeTextData());
			albumText.setText(song.getAlbum());
			//-------AUTHOR------------------------------------
			Label authorLabel = new Label(group, SWT.NULL);
			authorLabel.setLayoutData(makeLabelData());
			authorLabel.setText(TuxGuitar.getProperty("composition.author") + ":");
			
			final Text authorText = new Text(group, SWT.BORDER);
			authorText.setLayoutData(makeTextData());
			authorText.setText(song.getAuthor());
			//-------DATE------------------------------------
			Label dateLabel = new Label(group, SWT.NULL);
			dateLabel.setLayoutData(makeLabelData());
			dateLabel.setText(TuxGuitar.getProperty("composition.date") + ":");
			
			final Text dateText = new Text(group, SWT.BORDER);
			dateText.setLayoutData(makeTextData());
			dateText.setText(song.getDate());
			//-------COPYRIGHT------------------------------------
			Label copyrightLabel = new Label(group, SWT.NULL);
			copyrightLabel.setLayoutData(makeLabelData());
			copyrightLabel.setText(TuxGuitar.getProperty("composition.copyright") + ":");
			
			final Text copyrightText = new Text(group, SWT.BORDER);
			copyrightText.setLayoutData(makeTextData());
			copyrightText.setText(song.getCopyright());
			//-------WRITER-------------------------------------
			Label writerLabel = new Label(group, SWT.NULL);
			writerLabel.setLayoutData(makeLabelData());
			writerLabel.setText(TuxGuitar.getProperty("composition.writer") + ":");
			
			final Text writerText = new Text(group, SWT.BORDER);
			writerText.setLayoutData(makeTextData());
			writerText.setText(song.getWriter());
			//-------TRANSCRIBER------------------------------------
			Label transcriberLabel = new Label(group, SWT.NULL);
			transcriberLabel.setLayoutData(makeLabelData());
			transcriberLabel.setText(TuxGuitar.getProperty("composition.transcriber") + ":");
			
			final Text transcriberText = new Text(group, SWT.BORDER);
			transcriberText.setLayoutData(makeTextData());
			transcriberText.setText(song.getTranscriber());
			
			//-------COMMENTS------------------------------------
			Label commentsLabel = new Label(group, SWT.NULL);
			commentsLabel.setLayoutData(makeLabelData());
			commentsLabel.setText(TuxGuitar.getProperty("composition.comments") + ":");
			
			final Text commentsText = new Text(group, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			commentsText.setLayoutData(makeTextAreaData());
			commentsText.setText(song.getComments());
			
			//------------------BUTTONS--------------------------
			Composite buttons = new Composite(dialog, SWT.NONE);
			buttons.setLayout(new GridLayout(2,false));
			buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
			
			final Button buttonOK = new Button(buttons, SWT.PUSH);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setLayoutData(getButtonData());
			buttonOK.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					final String name = nameText.getText();
					final String artist = artistText.getText();
					final String album = albumText.getText();
					final String author = authorText.getText();
					final String date = dateText.getText();
					final String copyright = copyrightText.getText();
					final String writer = writerText.getText();
					final String transcriber = transcriberText.getText();
					final String comments = commentsText.getText();
					
					dialog.dispose();
					try {
						TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
							public void run() throws Throwable {
								ActionLock.lock();
								TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
								setProperties(name,artist,album,author,date,copyright,writer,transcriber,comments);
								TuxGuitar.instance().updateCache( true );
								TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
								ActionLock.unlock();
							}
						});
					} catch (Throwable throwable) {
						MessageDialog.errorMessage(throwable);
					}
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
	
	private GridData makeTextAreaData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumHeight = 100;
		return data;
	}
	
	private GridData makeTextData(){
		return new GridData(SWT.FILL, SWT.FILL, true, true);
	}
	
	private GridData makeLabelData(){
		return new GridData(SWT.RIGHT,SWT.CENTER,false,true);
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected void setProperties(String name,String artist,String album,String author,String date,String copyright,String writer,String transcriber,String comments){
		//comienza el undoable
		UndoableChangeInfo undoable = UndoableChangeInfo.startUndo();
		
		getSongManager().setProperties(name,artist,album,author,date,copyright,writer,transcriber,comments);
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		TuxGuitar.instance().showTitle();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
	}
}
