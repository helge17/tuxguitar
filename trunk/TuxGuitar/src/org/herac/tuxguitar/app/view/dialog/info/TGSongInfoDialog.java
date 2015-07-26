package org.herac.tuxguitar.app.view.dialog.info;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeInfoAction;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGSongInfoDialog {
	
	private static final int GROUP_WIDTH  = 450;
	private static final int GROUP_HEIGHT = SWT.DEFAULT;
	
	public void show(final TGViewContext context) {
		Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
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
				String name = nameText.getText();
				String artist = artistText.getText();
				String album = albumText.getText();
				String author = authorText.getText();
				String date = dateText.getText();
				String copyright = copyrightText.getText();
				String writer = writerText.getText();
				String transcriber = transcriberText.getText();
				String comments = commentsText.getText();
				
				updateSongInfo(context.getContext(), song, name, artist, album, author, date, copyright, writer, transcriber, comments);
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
	
	public void updateSongInfo(TGContext context, TGSong song, String name, String artist, String album, String author, String date, String copyright, String writer, String transcriber, String comments) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeInfoAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_NAME, name);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_ARTIST, artist);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_ALBUM, album);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_AUTHOR, author);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_DATE, date);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_COPYRIGHT, copyright);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_WRITER, writer);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_TRANSCRIBER, transcriber);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_COMMENTS, comments);
		tgActionProcessor.processOnNewThread();
	}
}
