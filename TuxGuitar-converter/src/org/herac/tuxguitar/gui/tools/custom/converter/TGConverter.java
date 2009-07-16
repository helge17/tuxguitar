package org.herac.tuxguitar.gui.tools.custom.converter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.io.base.TGRawImporter;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;

public class TGConverter {
	// This value will delay the process something like 1 minute for 3000 files.
	public static final int SLEEP_TIME = 20;
	
	public static final int FILE_OK = 250;
	public static final int FILE_BAD = 403;
	public static final int FILE_COULDNT_WRITE = 401;
	public static final int FILE_NOT_FOUND = 404;
	public static final int OUT_OF_MEMORY = 500;
	public static final int EXPORTER_NOT_FOUND = 590;
	public static final int UNKNOWN_ERROR = 666;
	
	private String sourceFolder;
	private String destinationFolder;
	private TGConverterFormat format;
	private TGConverterListener listener;
	private boolean cancelled;
	
	public TGConverter(String sourceFolder,String destinationFolder){
		this.sourceFolder = sourceFolder;
		this.destinationFolder = destinationFolder;
	}
	
	public void convert(String fileName, String convertFileName) {
		try {
			this.getListener().notifyFileProcess(convertFileName);
			
			TGSongManager manager = new TGSongManager();
			TGSong song = null;
			try {
				song = TGFileFormatManager.instance().getLoader().load(manager.getFactory(),new FileInputStream(fileName));
			} catch (TGFileFormatException e) {
				song = importSong(manager.getFactory(), fileName);
			}
			
			if (song != null){
				manager.setSong(song);
				manager.autoCompleteSilences();
				manager.orderBeats();
				
				new File(new File(convertFileName).getParent()).mkdirs();
				
				if( this.format != null && this.format.getExporter() instanceof TGOutputStreamBase ){
					TGOutputStreamBase exporter = (TGOutputStreamBase) this.format.getExporter();
					exporter.init(manager.getFactory(), new BufferedOutputStream(new FileOutputStream(convertFileName)));
					exporter.writeSong(song);
				}
				else if( this.format != null && this.format.getExporter() instanceof TGLocalFileExporter ){
					TGLocalFileExporter exporter = (TGLocalFileExporter) this.format.getExporter();
					exporter.configure(true);
					exporter.init(manager.getFactory(), new BufferedOutputStream(new FileOutputStream(convertFileName)));
					exporter.exportSong(manager.getSong());
				}
				this.getListener().notifyFileResult(convertFileName,FILE_OK);
			}
			else{
				this.getListener().notifyFileResult(fileName,FILE_BAD);
			}
		} catch (TGFileFormatException e) {
			this.getListener().notifyFileResult(fileName,FILE_COULDNT_WRITE); 
		} catch (FileNotFoundException ex) {
			this.getListener().notifyFileResult(fileName,FILE_NOT_FOUND);
		} catch (OutOfMemoryError e) {
			this.getListener().notifyFileResult(convertFileName,OUT_OF_MEMORY);
		} catch (Throwable throwable) {
			this.getListener().notifyFileResult(convertFileName,UNKNOWN_ERROR);
		}
	}
	
	private String checkIfExists(String convertFileName, int level) {
		if (new File(convertFileName).exists()) {
			String tmpName = convertFileName;
			String tmpExtension = "";
			String tmpLevel = "(" + (level + 1) + ")";
			String lastLevel = "(" + (level ) + ")";
			
			int index = convertFileName.lastIndexOf( (level == 0 ? "." : lastLevel + ".") );
			if (index!=-1) {
				tmpExtension = tmpName.substring(index + ( level == 0 ? 0 : lastLevel.length() ), tmpName.length());
				tmpName = tmpName.substring(0, index);
			}
			return checkIfExists( (tmpName + tmpLevel + tmpExtension)  , (level + 1) );
		}
		return convertFileName;
	}
	
	public void process() {
		this.getListener().notifyStart();
		this.process(new File(this.sourceFolder));
		this.getListener().notifyFinish();
	}
	
	private void process(File folder) {
		if(!isCancelled()){
			String[] fileNames = folder.list();
			if(fileNames != null){
				for (int i = 0; i < fileNames.length; i++) {
					File file = new File(folder.getPath() + "/" + fileNames[i]);
					if (file.isDirectory()) {
						process(file);
					} else if(!isCancelled()){
						String fileName = file.getAbsolutePath();
						String convertFileName = getConvertFileName(fileName);
						convert(fileName, convertFileName);
						
						// Just release the thread some milliseconds
						sleep();
					}
					fileNames[i] = null;
				}
			}
		}
	}
	
	private String getConvertFileName(String path) {
		String convertPath = (this.destinationFolder + File.separator +path.substring(this.sourceFolder.length()));
		int lastDot = convertPath.lastIndexOf(".");
		if (lastDot!=-1) {
			convertPath = convertPath.substring(0, lastDot) + this.format.getExtension();
		}
		return checkIfExists( new File(convertPath).getAbsolutePath() , 0 );
	}
	
	private TGSong importSong(TGFactory factory, String filename) {
		Iterator importers = TGFileFormatManager.instance().getImporters();
		while (importers.hasNext() ) {
			try {
				TGRawImporter rawImporter = (TGRawImporter)importers.next();
				if( rawImporter instanceof TGLocalFileImporter ){
					TGLocalFileImporter currentImporter = (TGLocalFileImporter)rawImporter;
					currentImporter.configure(true);
					if (isSupportedExtension(filename,currentImporter)) {
						InputStream input = new BufferedInputStream(new FileInputStream(filename));
						currentImporter.init(factory, input);
						return currentImporter.importSong();
					}
				}
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
		}
		return null;
	}
	
	private boolean isSupportedExtension(String filename, TGLocalFileImporter currentImporter) {
		try {
			String extension = filename.substring(filename.lastIndexOf("."),filename.length());
			extension="*"+extension.toLowerCase();
			String[] formats = currentImporter.getFileFormat().getSupportedFormats().split(";");
			for (int i=0; i<formats.length; i++)
				if (formats[i].toLowerCase().equals(extension))
					return true;
		} catch (Exception ex) {
			return false;
		}
		
		return false;
	}
	
	private void sleep(){
		try {
			Thread.sleep( SLEEP_TIME );
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public void setFormat( TGConverterFormat format ) {
		this.format = format;
	}
	
	public TGConverterListener getListener() {
		return this.listener;
	}
	
	public void setListener(TGConverterListener listener) {
		this.listener = listener;
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
