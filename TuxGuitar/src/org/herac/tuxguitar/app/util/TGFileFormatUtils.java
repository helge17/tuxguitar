package org.herac.tuxguitar.app.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;

public class TGFileFormatUtils {
	
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
	
	public static boolean isSupportedFormat(List<TGFileFormat> formats, String fileName) {
		if( fileName != null ){
			String extension = getFileExtension(fileName);
			if( extension != null ){
				for(TGFileFormat format : formats) {
					if( isSupportedExtension(format, extension)){
						return true;
					}
				}
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
