package app.tuxguitar.app.action.impl.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.file.TGWriteSongAction;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.io.base.TGFileFormatUtils;
import app.tuxguitar.util.TGContext;

public class TGWriteFileAction extends TGActionBase {

	public static final String NAME = "action.file.write";

	public static final String ATTRIBUTE_FILE_NAME = "fileName";
	public static final String ATTRIBUTE_NATIVE_FILE_FORMAT = "nativeFileFormat";

	public TGWriteFileAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		try {
			String fileName = context.getAttribute(ATTRIBUTE_FILE_NAME);

			context.setAttribute(TGWriteSongAction.ATTRIBUTE_OUTPUT_STREAM, new FileOutputStream(new File(fileName)));
			String formatCode = TGFileFormatUtils.getFileFormatCode(fileName);
			context.setAttribute(TGWriteSongAction.ATTRIBUTE_FORMAT_CODE, formatCode);
			boolean isNativeFormat;
			TGFileFormat fileFormat = context.getAttribute(TGWriteSongAction.ATTRIBUTE_FORMAT);
			if (fileFormat != null) {
				isNativeFormat = TGFileFormatManager.getInstance(getContext()).isNativeFileFormat(fileFormat);
			} else {
				isNativeFormat = TGFileFormatManager.getInstance(getContext()).isNativeFileFormat(formatCode);
			}
			context.setAttribute(ATTRIBUTE_NATIVE_FILE_FORMAT, isNativeFormat);

			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGWriteSongAction.NAME, context);
		} catch (FileNotFoundException e) {
			throw new TGActionException(e);
		}
	}
}
