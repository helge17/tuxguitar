package org.herac.tuxguitar.io.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.herac.tuxguitar.io.tg.TGStream;

public class TGFileFormatUtils {
	
	public static final String DEFAULT_EXTENSION = ("." + TGStream.TG_FORMAT_CODE);
	
	public static String getDefaultExtension(TGFileFormat format, String defaultValue) {
		String[] supportedFormats = format.getSupportedFormats();
		if( supportedFormats != null && supportedFormats.length > 0 ) {
			return ("." + supportedFormats[0]);
		}
		return defaultValue;
	}
	
	public static String getDefaultExtension(TGFileFormat format) {
		return getDefaultExtension(format, DEFAULT_EXTENSION);
	}
	
	public static String getFileExtension(String path) {
		return getFileExtension(path, null);
	}

	public static String getFileExtension(String path, String defaultValue) {
		int index = path.lastIndexOf(".");
		if( index > 0 ){
			return path.substring(index);
		}
		return defaultValue;
	}
	
	public static String getFileFormatCode(String path) {
		return getFileFormatCode(path, null);
	}
	
	public static String getFileFormatCode(String path, String defaultValue) {
		String extension = getFileExtension(path);
		if( extension != null && extension.length() > 1 ) {
			return extension.substring(1);
		}
		return defaultValue;
	}

	public static boolean isSupportedFormat(List<TGFileFormat> formats, String fileName) {
		if( fileName != null ){
			String formatCode = getFileFormatCode(fileName);
			if( formatCode != null ){
				for(TGFileFormat format : formats) {
					if( format.isSupportedCode(formatCode)){
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
