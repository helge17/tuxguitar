package org.herac.tuxguitar.app.view.dialog.file;

import java.io.File;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.helper.TGFileHistory;
import org.herac.tuxguitar.app.util.TGFileFormatUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.io.base.TGFileFormat;

public class TGFileChooserDialog {
	
	public static final String ATTRIBUTE_HANDLER = TGFileChooserHandler.class.getName();
	public static final String ATTRIBUTE_STYLE = "style";
	public static final String ATTRIBUTE_FILE_FORMATS = "fileFormats";
	public static final String ATTRIBUTE_FILE_NAME = "fileName";
	public static final String ATTRIBUTE_CHOOSER_PATH = "chooserPath";
	public static final String ATTRIBUTE_DEFAULT_EXTENSION = "defaultExtension";
	
	public static final Integer STYLE_OPEN = SWT.OPEN;
	public static final Integer STYLE_SAVE = SWT.SAVE;
	
	public void show(TGViewContext context) {
		Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		Integer style = context.getAttribute(ATTRIBUTE_STYLE);
		String chooserPath = context.getAttribute(ATTRIBUTE_CHOOSER_PATH);
		String fileName = context.getAttribute(ATTRIBUTE_FILE_NAME);
		List<TGFileFormat> formats = context.getAttribute(ATTRIBUTE_FILE_FORMATS);
		
		FilterList filter = new FilterList(formats);
		FileDialog dialog = new FileDialog(parent, style);
		dialog.setFileName(fileName);
		dialog.setFilterPath(chooserPath);
		dialog.setFilterNames(filter.getFilterNames());
		dialog.setFilterExtensions(filter.getFilterExtensions());
		
		String path = dialog.open();
		if( path != null ){
			File file = new File(path);
			File fileParent = file.getParentFile();
			if( fileParent != null && fileParent.exists() && fileParent.isDirectory() ){
				TGFileHistory.getInstance(context.getContext()).setChooserPath( fileParent.getAbsolutePath() );
			}
			
			if( STYLE_SAVE.equals(style) && !TGFileFormatUtils.isSupportedFormat(formats, path)) {
				String defaultExtension = context.getAttribute(ATTRIBUTE_DEFAULT_EXTENSION);
				if( defaultExtension != null ) {
					path += defaultExtension;
				}
			}
			
			this.handleFileName(context, path);
		}
	}
	
	public void handleFileName(TGViewContext context, String fileName) {
		if( STYLE_SAVE.equals(context.getAttribute(ATTRIBUTE_STYLE)) && new File(fileName).exists() ) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(context.getContext(), TGOpenViewAction.NAME);
			tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGConfirmDialogController());
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("file.overwrite-question"));
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_STYLE, TGConfirmDialog.BUTTON_YES | TGConfirmDialog.BUTTON_NO);
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_DEFAULT_BUTTON, TGConfirmDialog.BUTTON_NO);
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_RUNNABLE_YES, this.createUpdateFileNameRunnable(context, fileName));
			tgActionProcessor.process();
		} else {
			updateFileName(context, fileName);
		}
	}
	
	public void updateFileName(TGViewContext context, String fileName) {
		TGFileChooserHandler tgFileChooserHandler = context.getAttribute(ATTRIBUTE_HANDLER);
		tgFileChooserHandler.updateFileName(fileName);
	}
	
	public void updateFileNameInNewThread(final TGViewContext context, final String fileName) {
		new Thread(new Runnable() {
			public void run() {
				updateFileName(context, fileName);
			}
		}).start();
	}
	
	public Runnable createUpdateFileNameRunnable(final TGViewContext context, final String fileName) {
		return new Runnable() {
			public void run() {
				updateFileNameInNewThread(context, fileName);
			}
		};
	}
	
	private class FilterList {
		
		private String[] filterExtensions;
		private String[] filterNames;
		
		public  FilterList(List<TGFileFormat> formats) {
			int size = (formats.size() + 2);
			this.filterNames = new String[size];
			this.filterExtensions = new String[size];
			this.filterNames[0] = new String("All Supported Formats");
			this.filterExtensions[0] = new String();
			for(int i = 1; i < (size - 1); i ++){
				TGFileFormat format = (TGFileFormat)formats.get(i-1);
				this.filterNames[i] = format.getName();
				this.filterExtensions[i] = createFilterExtensions(format.getSupportedFormats());
				this.filterExtensions[0] += (i > 1)?";":"";
				this.filterExtensions[0] += this.filterExtensions[i];
			}
			this.filterNames[(size - 1)] = new String("All Files");
			this.filterExtensions[(size - 1)] = new String("*.*");
		}
		
		private String createFilterExtensions(String[] supportedFormats) {
			String separator = "";
			StringBuffer sb = new StringBuffer();
			for(int i = 0 ; i < supportedFormats.length ; i ++) {
				sb.append(separator);
				sb.append("*.");
				sb.append(supportedFormats[i]);
				separator = ";";
			}
			return sb.toString();
		}
		
		public String[] getFilterExtensions() {
			return this.filterExtensions;
		}
		
		public String[] getFilterNames() {
			return this.filterNames;
		}
	}
}
