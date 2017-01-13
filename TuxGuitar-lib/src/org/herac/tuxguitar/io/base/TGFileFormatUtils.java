package org.herac.tuxguitar.io.base;

import org.herac.tuxguitar.util.TGContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class TGFileFormatUtils {

	public static String getFileExtension(String path){
		return getFileExtension(path, null);
	}

	public static String getFileExtension(String path, String defaultValue){
		int index = path.lastIndexOf(".");
		if( index > 0 ){
			return path.substring(index);
		}
		return defaultValue;
	}
	
	public static TGFileFormat getOutputFileFormat(TGContext context, String path){
		if( path != null ){
			String extension = getFileExtension(path);
			if( extension != null ){
				Iterator<TGOutputStreamBase> it = TGFileFormatManager.getInstance(context).getOutputStreams();
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
	
	public static TGFileFormat getInputFileFormat(TGContext context, String path){
		if( path != null ){
			String extension = getFileExtension(path);
			if( extension != null ){
				Iterator<TGInputStreamBase> it = TGFileFormatManager.getInstance(context).getInputStreams();
				while(it.hasNext()){
					TGInputStreamBase writer = (TGInputStreamBase)it.next();
					if( isSupportedExtension(writer.getFileFormat(), extension) ){
						return writer.getFileFormat();
					}
				}
			}
		}
		return null;
	}
	
	public static TGFileFormat getImporterFileFormat(TGContext context, String path) {
		if( path != null ){
			String extension = getFileExtension(path);
			if( extension != null ){
				Iterator<TGRawImporter> importers = TGFileFormatManager.getInstance(context).getImporters();
				while (importers.hasNext() ) {
					TGRawImporter rawImporter = importers.next();
					if( rawImporter instanceof TGLocalFileImporter ) {
						TGLocalFileImporter fileImporter = (TGLocalFileImporter) rawImporter;
						if( isSupportedExtension(fileImporter.getFileFormat(), extension) ){
							return fileImporter.getFileFormat();
						}
					}
				}
			}
		}
		return null;
	}

	public static TGFileFormat getExporterFileFormat(TGContext context, String path) {
		if( path != null ){
			String extension = getFileExtension(path);
			if( extension != null ){
				Iterator<TGRawExporter> exporters = TGFileFormatManager.getInstance(context).getExporters();
				while (exporters.hasNext() ) {
					TGRawExporter rawExporter = exporters.next();
					if( rawExporter instanceof TGLocalFileExporter ) {
						TGLocalFileExporter fileExporter = (TGLocalFileExporter) rawExporter;
						if( isSupportedExtension(fileExporter.getFileFormat(), extension) ){
							return fileExporter.getFileFormat();
						}
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
