package org.herac.tuxguitar.app.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;

public class TGFileFormatUtils {
	
	public static String getFileName(){
		if (TuxGuitar.getInstance().getFileHistory().isNewFile() || !TuxGuitar.getInstance().getFileHistory().isLocalFile()) {
			return chooseFileName();
		}
		String path = TuxGuitar.getInstance().getFileHistory().getCurrentFilePath();
		String file = TuxGuitar.getInstance().getFileHistory().getCurrentFileName(FileChooser.DEFAULT_SAVE_FILENAME);
		String fullPath = path + File.separator + file;
		return ( isSupportedFormat(fullPath) ? fullPath : chooseFileName() );
	}
	
	public static String chooseFileName(){
		String fileName = FileChooser.instance().save(TuxGuitar.getInstance().getShell(), TuxGuitar.getInstance().getFileFormatManager().getOutputFormats());
		if (fileName != null) {
			if (!isSupportedFormat(fileName)) {
				fileName += TGFileFormatManager.DEFAULT_EXTENSION;
			}
			if(!canWrite(fileName)){
				return null;
			}
		}
		return fileName;
	}
	
	public static String chooseFileName(TGFileFormat format){
		String fileName = FileChooser.instance().save(TuxGuitar.getInstance().getShell(),format);
		if (fileName != null && !canWrite(fileName)){
			return null;
		}
		return fileName;
	}
	
	public static String getFileExtension(String path){
		int index = path.lastIndexOf(".");
		if(index > 0){
			return path.substring(index);
		}
		return null;
	}
	
	public static TGFileFormat getFileFormat(String path){
		if( path != null ){
			String extension = getFileExtension(path);
			if( extension != null ){
				Iterator<TGOutputStreamBase> it = TuxGuitar.getInstance().getFileFormatManager().getOutputStreams();
				while(it.hasNext()){
					TGOutputStreamBase writer = (TGOutputStreamBase)it.next();
					if( isSupportedExtension(writer.getFileFormat(), extension) ){
						return writer.getFileFormat();
					}
				}
			}
		}
		return null;
	}
	
	public static boolean isSupportedExtension(TGFileFormat format, String extension) {
		String[] supportedFormats = format.getSupportedFormats();
		for(int i = 0 ; i < supportedFormats.length ; i ++) {
			if( extension.toLowerCase().equals("." + supportedFormats[i].toLowerCase()) ) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isSupportedFormat(String path) {
		if( path != null ){
			String extension = getFileExtension(path);
			if( extension != null ){
				Iterator<TGOutputStreamBase> it = TuxGuitar.getInstance().getFileFormatManager().getOutputStreams();
				while(it.hasNext()){
					TGOutputStreamBase writer = (TGOutputStreamBase)it.next();
					if( isSupportedExtension(writer.getFileFormat(), extension)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean canWrite(String fileName){
		boolean canWrite = true;
		File file = new File(fileName);
		if (file.exists()) {
			ConfirmDialog confirm = new ConfirmDialog(TuxGuitar.getProperty("file.overwrite-question"));
			confirm.setDefaultStatus( ConfirmDialog.STATUS_NO );
			if (confirm.confirm(ConfirmDialog.BUTTON_YES | ConfirmDialog.BUTTON_NO , ConfirmDialog.BUTTON_NO ) == ConfirmDialog.STATUS_NO) {
				canWrite = false;
			}
		}
		return canWrite;
	}
	
	public static boolean isLocalFile(URL url){
		try {
			if( url.getProtocol().equals( new File(url.getFile()).toURI().toURL().getProtocol() ) ){
				return true;
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return false;
	}
	
	public static InputStream getInputStream(InputStream in)throws Throwable {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read = 0;
		while((read = in.read()) != -1){
			out.write(read);
		}
		byte[] bytes = out.toByteArray();
		in.close();
		out.close();
		out.flush();
		return new ByteArrayInputStream(bytes);
	}
}
