package org.herac.tuxguitar.app.document;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.helper.TGFileHistory;
import org.herac.tuxguitar.app.util.TGFileChooser;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.app.view.dialog.file.TGFileChooserDialog;
import org.herac.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatUtils;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGDocumentFileManager {
	
	public static final String DEFAULT_FILENAME = ("Untitled" + TGFileFormatUtils.DEFAULT_EXTENSION);
	
	private TGContext context;
	
	public TGDocumentFileManager(TGContext context) {
		this.context = context;
	}
	
	public void chooseFileNameForOpen(TGFileFormat format, TGFileChooserHandler handler) {
		this.chooseFileNameForOpen(this.toFileFormatList(format), handler);
	}
	
	public void chooseFileNameForOpen(List<TGFileFormat> formats, TGFileChooserHandler handler) {
		String chooserPath = TGFileHistory.getInstance(this.context).getChooserPath();
		String currentPath = this.getCurrentFilePath();
		boolean localFile = this.isLocalFile();
		boolean existentFile = (localFile && currentPath != null && chooserPath != null && currentPath.equals(chooserPath));
		String fileName = (existentFile ? this.createFileName(formats, null, false) : null );
		
		TGFileChooser.getInstance(this.context).openChooser(handler, formats, TGFileChooserDialog.STYLE_OPEN, fileName, chooserPath);
	}
	
	public void chooseFileNameForSave(TGFileFormat format, TGFileChooserHandler handler) {
		this.chooseFileNameForSave(this.toFileFormatList(format), handler);
	}
	
	public void chooseFileNameForSave(List<TGFileFormat> formats, TGFileChooserHandler handler) {
		String chooserPath = getCurrentFilePath();
		if( chooserPath == null ) {
			chooserPath = TGFileHistory.getInstance(this.context).getChooserPath();
		}
		
		String fileName = this.createFileName(formats, DEFAULT_FILENAME, true);
		String defaultExtension = TGFileFormatUtils.DEFAULT_EXTENSION;
		
		TGFileChooser.getInstance(this.context).openChooser(handler, formats, TGFileChooserDialog.STYLE_SAVE, fileName, chooserPath, defaultExtension);
	}
	
	public void findFileNameForSave(List<TGFileFormat> formats, TGFileChooserHandler handler) {
		if( this.isNewFile() || !this.isLocalFile()) {
			this.chooseFileNameForSave(formats, handler);
		} 
		else {
			String fullPath = null;
			String path = this.getCurrentFilePath();
			String file = this.getCurrentFileName(DEFAULT_FILENAME);
			if( path != null && file != null ) {
				fullPath = (path + File.separator + file);
			}
			
			if( fullPath != null && TGFileFormatUtils.isSupportedFormat(formats, fullPath) ) {
				handler.updateFileName(fullPath);
			} else  {
				this.chooseFileNameForSave(formats, handler);
			}
		}
	}
	
	public List<TGFileFormat> toFileFormatList(TGFileFormat format) {
		List<TGFileFormat> formats = new ArrayList<TGFileFormat>();
		formats.add(format);
		return formats;
	}
	
	public String createFileName(List<TGFileFormat> formats, String defaultName, boolean replaceExtension){
		if(formats == null || formats.isEmpty()){
			return defaultName;
		}
		String file = getCurrentFileName(defaultName);
		if( file != null && file.length() > 0 ){
			int index = file.lastIndexOf('.');
			if(index > 0){
				String fileName = file.substring(0,index);
				String fileExtension = file.substring(index).toLowerCase();
				Iterator<TGFileFormat> it = formats.iterator();
				while(it.hasNext()){
					TGFileFormat format = (TGFileFormat)it.next();
					if(format.getSupportedFormats() != null){
						String[] supportedFormats = format.getSupportedFormats();
						if( supportedFormats != null && supportedFormats.length > 0 ){
							for(int i = 0; i < supportedFormats.length; i ++){
								if( fileExtension.equals("." + supportedFormats[i]) ){
									return file;
								}
							}
						}
					}
				}
				if( replaceExtension ){
					TGFileFormat format = (TGFileFormat)formats.get(0);
					if( format.getSupportedFormats() != null ){
						String[] supportedFormats = format.getSupportedFormats();
						if( supportedFormats != null && supportedFormats.length > 0 ){
							return (fileName + "." + supportedFormats[0]);
						}
					}
				}
			}
		}
		return defaultName;
	}
	
	public String getCurrentFileName(String defaultName) {
		if(!this.isNewFile()){
			URI uri = getCurrentURI();
			if( uri != null && !uri.isOpaque()){
				return decode(new File(uri.getPath()).getName());
			}
		}
		return defaultName;
	}
	
	public String getCurrentFilePath() {
		if(!this.isNewFile()){
			URI uri = getCurrentURI();
			if(uri != null){
				String file = getFilePath(uri);
				if( file != null ) {
					return decode(file);
				}
			}
		}
		return null;
	}
	
	public URI getCurrentURI(){
		TGDocument document = TGDocumentListManager.getInstance(this.context).findCurrentDocument();
		if( document != null ) {
			return document.getUri();
		}
		return null;
	}
	
	public String getFilePath(URI uri){
		if( TGFileUtils.isLocalFile(uri) ){
			return new File(uri).getParent();
		}
		return null;
	}
	
	public boolean isNewFile(){
		return (this.getCurrentURI() == null);
	}
	
	public boolean isLocalFile(){
		URI uri = getCurrentURI();
		
		return (uri != null && TGFileUtils.isLocalFile(uri));
	}
	
	private String decode(String url){
		try {
			return URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	public static TGDocumentFileManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGDocumentFileManager.class.getName(), new TGSingletonFactory<TGDocumentFileManager>() {
			public TGDocumentFileManager createInstance(TGContext context) {
				return new TGDocumentFileManager(context);
			}
		});
	}
}
