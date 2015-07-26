package org.herac.tuxguitar.app.action.impl.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.ConfirmDialog;
import org.herac.tuxguitar.app.util.FileChooser;
import org.herac.tuxguitar.app.util.MessageDialog;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.base.TGRawImporter;
import org.herac.tuxguitar.io.base.TGSongLoaderHandle;
import org.herac.tuxguitar.io.base.TGSongWriterHandle;
import org.herac.tuxguitar.song.models.TGSong;

public class FileActionUtils {
	
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
	
	public static void open(final String fileName){
		try {
			TGSongLoaderHandle tgSongLoaderHandle = new TGSongLoaderHandle();
			tgSongLoaderHandle.setFactory(TuxGuitar.getInstance().getSongManager().getFactory());
			tgSongLoaderHandle.setInputStream(new FileInputStream(fileName));
			TuxGuitar.getInstance().getFileFormatManager().getLoader().load(tgSongLoaderHandle);
			TuxGuitar.getInstance().fireNewSong(tgSongLoaderHandle.getSong(), new File(fileName).toURI().toURL());
		}catch (Throwable throwable) {
			TuxGuitar.getInstance().newSong();
			MessageDialog.errorMessage(new TGFileFormatException(TuxGuitar.getProperty("file.open.error", new String[]{fileName}),throwable));
		}
	}
	
	public static void save(final String fileName){
		try {
			OutputStream stream = new FileOutputStream(new File(fileName));
			TGDocumentManager manager = TuxGuitar.getInstance().getDocumentManager();
			
			TGSongWriterHandle tgSongWriterHandle = new TGSongWriterHandle();
			tgSongWriterHandle.setFactory(manager.getSongManager().getFactory());
			tgSongWriterHandle.setSong(manager.getSong());
			tgSongWriterHandle.setFormat(getFileFormat(fileName));
			tgSongWriterHandle.setOutputStream(stream);
			TuxGuitar.getInstance().getFileFormatManager().getWriter().write(tgSongWriterHandle);
			TuxGuitar.getInstance().fireSaveSong(new File(fileName).toURI().toURL());
		} catch (Throwable throwable) {
			MessageDialog.errorMessage(new TGFileFormatException(TuxGuitar.getProperty("file.save.error", new String[]{fileName}),throwable));
		}
	}
	
	public static void open(final URL url){
		try {
			InputStream stream = (isLocalFile(url) ? url.openStream() : getInputStream(url.openStream()));
			TGSongLoaderHandle tgSongLoaderHandle = new TGSongLoaderHandle();
			tgSongLoaderHandle.setFactory(TuxGuitar.getInstance().getSongManager().getFactory());
			tgSongLoaderHandle.setInputStream(stream);
			TuxGuitar.getInstance().getFileFormatManager().getLoader().load(tgSongLoaderHandle);
			TuxGuitar.getInstance().fireNewSong(tgSongLoaderHandle.getSong(), url);
		}catch (Throwable throwable) {
			TuxGuitar.getInstance().newSong();
			MessageDialog.errorMessage(new TGFileFormatException(TuxGuitar.getProperty("file.open.error", new String[]{url.toString()}),throwable));
		}
	}
	
	public static void exportSong(TGRawExporter exporter){
		try {
			TGDocumentManager manager = TuxGuitar.getInstance().getDocumentManager();
			exporter.exportSong(manager.getSong());
		} catch (Throwable throwable) {
			MessageDialog.errorMessage(new TGFileFormatException(TuxGuitar.getProperty("file.export.error"),throwable));
		}
	}
	
	public static void exportSong(TGLocalFileExporter exporter, String path){
		try {
			OutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(path)));
			TGDocumentManager manager = TuxGuitar.getInstance().getDocumentManager();
			exporter.init( manager.getSongManager().getFactory() , stream );
			exporter.exportSong(manager.getSong());
		} catch (Throwable throwable) {
			MessageDialog.errorMessage(new TGFileFormatException(TuxGuitar.getProperty("file.export.error", new String[]{path}),throwable));
		}
	}
	
	public static void importSong(final TGRawImporter importer){
		try {
			TGSong song = importer.importSong();
			TuxGuitar.getInstance().fireNewSong(song,null);
		}catch (Throwable throwable) {
			TuxGuitar.getInstance().newSong();
			MessageDialog.errorMessage(new TGFileFormatException(TuxGuitar.getProperty("file.import.error"),throwable));
		}
	}
	
	public static void importSong(final TGLocalFileImporter importer, String path){
		try {
			InputStream stream = new BufferedInputStream(new FileInputStream(new File(path)));
			importer.init(TuxGuitar.getInstance().getSongManager().getFactory(),stream);
			TGSong song = importer.importSong();
			TuxGuitar.getInstance().fireNewSong(song,null);
		}catch (Throwable throwable) {
			TuxGuitar.getInstance().newSong();
			MessageDialog.errorMessage(new TGFileFormatException(TuxGuitar.getProperty("file.import.error", new String[]{path}),throwable));
		}
	}
	
	private static boolean isLocalFile(URL url){
		try {
			if(url.getProtocol().equals( new File(url.getFile()).toURI().toURL().getProtocol() ) ){
				return true;
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return false;
	}
	
	private static InputStream getInputStream(InputStream in)throws Throwable {
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
