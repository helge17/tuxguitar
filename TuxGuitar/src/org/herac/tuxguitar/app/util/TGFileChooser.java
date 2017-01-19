package org.herac.tuxguitar.app.util;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.helper.TGFileHistory;
import org.herac.tuxguitar.app.view.dialog.file.TGFileChooserDialog;
import org.herac.tuxguitar.app.view.dialog.file.TGFileChooserDialogController;
import org.herac.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatUtils;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGFileChooser {
	
	public static final String DEFAULT_OPEN_FILENAME = null;
	
	public static final String DEFAULT_SAVE_FILENAME = ("Untitled" + TGFileFormatUtils.DEFAULT_EXTENSION);
	
	public static TGFileFormat ALL_FORMATS = new TGFileFormat("All Files", "*/*", new String[]{"*"});
	
	private TGContext context;
	
	private TGFileChooser(TGContext context) {
		this.context = context;
	}
	
	public void openChooser(TGFileChooserHandler handler, List<TGFileFormat> formats, Integer style, String fileName, String chooserPath, String defaultExtension) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGFileChooserDialogController());
		tgActionProcessor.setAttribute(TGFileChooserDialog.ATTRIBUTE_HANDLER, handler);
		tgActionProcessor.setAttribute(TGFileChooserDialog.ATTRIBUTE_STYLE, style);
		tgActionProcessor.setAttribute(TGFileChooserDialog.ATTRIBUTE_CHOOSER_PATH, chooserPath);
		tgActionProcessor.setAttribute(TGFileChooserDialog.ATTRIBUTE_FILE_FORMATS, formats);
		tgActionProcessor.setAttribute(TGFileChooserDialog.ATTRIBUTE_FILE_NAME, fileName);
		tgActionProcessor.setAttribute(TGFileChooserDialog.ATTRIBUTE_DEFAULT_EXTENSION, defaultExtension);
		tgActionProcessor.process();
	}
	
	public void openChooser(TGFileChooserHandler handler, TGFileFormat format, Integer style, String fileName, String chooserPath, String defaultExtension) {
		this.openChooser(handler, toFileFormatList(format), style, fileName, chooserPath, defaultExtension);
	}
	
	public void openChooser(TGFileChooserHandler handler, List<TGFileFormat> formats, Integer style, String fileName, String chooserPath) {
		this.openChooser(handler, formats, style, fileName, chooserPath, null);
	}
	
	public void openChooser(TGFileChooserHandler handler, TGFileFormat format, Integer style, String fileName, String chooserPath) {
		this.openChooser(handler, toFileFormatList(format), style, fileName, chooserPath);
	}
	
	public void openChooser(TGFileChooserHandler handler, List<TGFileFormat> formats, Integer style, String fileName) {
		this.openChooser(handler, formats, style, fileName, getDefaultChooserPath());
	}
	
	public void openChooser(TGFileChooserHandler handler, TGFileFormat format, Integer style, String fileName) {
		this.openChooser(handler, format, style, fileName, getDefaultChooserPath());
	}
	
	public void openChooser(TGFileChooserHandler handler, List<TGFileFormat> formats, Integer style) {
		this.openChooser(handler, formats, style, null);
	}
	
	public void openChooser(TGFileChooserHandler handler, TGFileFormat format, Integer style) {
		this.openChooser(handler, format, style, null);
	}
	
	public List<TGFileFormat> toFileFormatList(TGFileFormat format) {
		List<TGFileFormat> formats = new ArrayList<TGFileFormat>();
		formats.add(format);
		return formats;
	}
	
	public String getDefaultChooserPath() {
		return TGFileHistory.getInstance(this.context).getChooserPath();
	}
	
	public static TGFileChooser getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGFileChooser.class.getName(), new TGSingletonFactory<TGFileChooser>() {
			public TGFileChooser createInstance(TGContext context) {
				return new TGFileChooser(context);
			}
		});
	}
}