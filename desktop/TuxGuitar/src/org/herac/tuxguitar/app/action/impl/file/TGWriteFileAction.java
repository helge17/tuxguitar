package org.herac.tuxguitar.app.action.impl.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.document.TGDocument;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.file.TGWriteSongAction;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGFileFormatUtils;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGWriteFileAction extends TGActionBase {
	
	public static final String NAME = "action.file.write";
	
	public static final String ATTRIBUTE_FILE_NAME = "fileName";
	// file export: boolean attribute, set to true if writing to a non commonFileFormat
	public static final String ATTRIBUTE_FILE_EXPORT = "fileExport";
	
	public TGWriteFileAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		try {
			TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(getContext());
			
			String fileName = context.getAttribute(ATTRIBUTE_FILE_NAME);
			
			context.setAttribute(TGWriteSongAction.ATTRIBUTE_OUTPUT_STREAM, new FileOutputStream(new File(fileName)));
			String formatCode = TGFileFormatUtils.getFileFormatCode(fileName);
			context.setAttribute(TGWriteSongAction.ATTRIBUTE_FORMAT_CODE, formatCode);
			context.setAttribute(ATTRIBUTE_FILE_EXPORT, !fileFormatManager.isCommonWriteFileFormat(formatCode));
			
			TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			TGDocument document = TGDocumentListManager.getInstance(getContext()).findDocument(song);
			if (!fileFormatManager.isNativeWriteFileFormat(formatCode) && (!document.isForeignFormatConfirmed())) {
				// foreign format, not explicitly accepted by user: issue a warning
				TGActionProcessor tgActionProcessor = new TGActionProcessor(getContext(), TGOpenViewAction.NAME);
				tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGConfirmDialogController());
				tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("file.confirm-foreign-format"));
				tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_STYLE, TGConfirmDialog.BUTTON_YES | TGConfirmDialog.BUTTON_CANCEL);
				tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_DEFAULT_BUTTON, TGConfirmDialog.BUTTON_CANCEL);
				tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_RUNNABLE_YES, new Runnable() {
					// user confirmed use of foreign format, store preference and save file
					@Override
					public void run() {
						document.confirmForeignFormat();
						TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
						tgActionManager.execute(TGWriteSongAction.NAME, context);
					}});
				tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_RUNNABLE_CANCEL, new Runnable() {
					// user canceled use of foreign format, open "save as"
					@Override
					public void run() {
						TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
						tgActionManager.execute(TGSaveAsFileAction.NAME, context);
					}});
				tgActionProcessor.process();
			}
			else {
				// no warning required, write song
				TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
				tgActionManager.execute(TGWriteSongAction.NAME, context);
			}
			
		} catch (FileNotFoundException e) {
			throw new TGActionException(e);
		}
	}
}
