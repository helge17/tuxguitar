package org.herac.tuxguitar.gui.actions.file;

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

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.ConfirmDialog;
import org.herac.tuxguitar.gui.util.FileChooser;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.base.TGRawImporter;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;

public class FileActionUtils {
	
	public static String getFileName(){
		if (TuxGuitar.instance().getFileHistory().isNewFile() || !TuxGuitar.instance().getFileHistory().isLocalFile()) {
			return chooseFileName();
		}
		String path = TuxGuitar.instance().getFileHistory().getCurrentFilePath();
		String file = TuxGuitar.instance().getFileHistory().getCurrentFileName(FileChooser.DEFAULT_SAVE_FILENAME);
		String fullPath = path + File.separator + file;
		return ( isSupportedFormat(fullPath) ? fullPath : chooseFileName() );
	}
	
	public static String chooseFileName(){
		String fileName = FileChooser.instance().save(TuxGuitar.instance().getShell(),TGFileFormatManager.instance().getOutputFormats());
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
		String fileName = FileChooser.instance().save(TuxGuitar.instance().getShell(),format);
		if (fileName != null && !canWrite(fileName)){
			return null;
		}
		return fileName;
	}
	
	public static boolean isSupportedFormat(String path) {
		if(path != null){
			int index = path.lastIndexOf(".");
			if(index > 0){
				Iterator it = TGFileFormatManager.instance().getOutputStreams();
				while(it.hasNext()){
					TGOutputStreamBase writer = (TGOutputStreamBase)it.next();
					if(writer.isSupportedExtension(path.substring(index))){
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
			TGSong song = TGFileFormatManager.instance().getLoader().load(TuxGuitar.instance().getSongManager().getFactory(),new FileInputStream(fileName));
			TuxGuitar.instance().fireNewSong(song,new File(fileName).toURI().toURL());
		}catch (Throwable throwable) {
			TuxGuitar.instance().newSong();
			MessageDialog.errorMessage(new TGFileFormatException(TuxGuitar.getProperty("file.open.error", new String[]{fileName}),throwable));
		}
	}
	
	public static void save(final String fileName){
		try {
			TGSongManager manager = TuxGuitar.instance().getSongManager();
			TGFileFormatManager.instance().getWriter().write(manager.getFactory(),manager.getSong(), fileName);
			TuxGuitar.instance().fireSaveSong(new File(fileName).toURI().toURL());
		} catch (Throwable throwable) {
			MessageDialog.errorMessage(new TGFileFormatException(TuxGuitar.getProperty("file.save.error", new String[]{fileName}),throwable));
		}
	}
	
	public static void open(final URL url){
		try {
			InputStream stream = (isLocalFile(url) ? url.openStream() : getInputStream(url.openStream()));
			TGSong song = TGFileFormatManager.instance().getLoader().load(TuxGuitar.instance().getSongManager().getFactory(),stream);
			TuxGuitar.instance().fireNewSong(song,url);
		}catch (Throwable throwable) {
			TuxGuitar.instance().newSong();
			MessageDialog.errorMessage(new TGFileFormatException(TuxGuitar.getProperty("file.open.error", new String[]{url.toString()}),throwable));
		}
	}
	
	public static void exportSong(TGRawExporter exporter){
		try {
			TGSongManager manager = TuxGuitar.instance().getSongManager();
			exporter.exportSong(manager.getSong());
		} catch (Throwable throwable) {
			MessageDialog.errorMessage(new TGFileFormatException(TuxGuitar.getProperty("file.export.error"),throwable));
		}
	}
	
	public static void exportSong(TGLocalFileExporter exporter, String path){
		try {
			OutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(path)));
			TGSongManager manager = TuxGuitar.instance().getSongManager();
			exporter.init( manager.getFactory() , stream );
			exporter.exportSong(manager.getSong());
		} catch (Throwable throwable) {
			MessageDialog.errorMessage(new TGFileFormatException(TuxGuitar.getProperty("file.export.error", new String[]{path}),throwable));
		}
	}
	
	public static void importSong(final TGRawImporter importer){
		try {
			TGSong song = importer.importSong();
			TuxGuitar.instance().fireNewSong(song,null);
		}catch (Throwable throwable) {
			TuxGuitar.instance().newSong();
			MessageDialog.errorMessage(new TGFileFormatException(TuxGuitar.getProperty("file.import.error"),throwable));
		}
	}
	
	public static void importSong(final TGLocalFileImporter importer, String path){
		try {
			InputStream stream = new BufferedInputStream(new FileInputStream(new File(path)));
			importer.init(TuxGuitar.instance().getSongManager().getFactory(),stream);
			TGSong song = importer.importSong();
			TuxGuitar.instance().fireNewSong(song,null);
		}catch (Throwable throwable) {
			TuxGuitar.instance().newSong();
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
