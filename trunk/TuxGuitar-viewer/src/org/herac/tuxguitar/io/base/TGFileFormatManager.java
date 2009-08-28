package org.herac.tuxguitar.io.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.io.tg.TGStream;

public class TGFileFormatManager {
	
	public static final String DEFAULT_EXTENSION = TGStream.TG_FORMAT_EXTENSION;
	
	private static TGFileFormatManager instance;
	
	private TGSongLoader loader;
	private List inputStreams;
	
	private TGFileFormatManager(){
		this.loader = new TGSongLoader();
		this.inputStreams = new ArrayList();
		this.addDefatultStreams();
	}
	
	public static TGFileFormatManager instance(){
		if(instance == null){
			instance = new TGFileFormatManager();
		}
		return instance;
	}
	
	public TGSongLoader getLoader(){
		return this.loader;
	}
	
	public void addInputStream(TGInputStreamBase stream){
		this.inputStreams.add(stream);
	}
	
	public void removeInputStream(TGInputStreamBase stream){
		this.inputStreams.remove(stream);
	}
	
	public Iterator getInputStreams(){
		return this.inputStreams.iterator();
	}
	
	public List getInputFormats(){
		List formats = new ArrayList();
		Iterator it = getInputStreams();
		while(it.hasNext()){
			TGInputStreamBase stream = (TGInputStreamBase)it.next();
			TGFileFormat format = stream.getFileFormat();
			if(!existsFormat(format, formats)){
				formats.add(format);
			}
		}
		return formats;
	}
	
	private boolean existsFormat(TGFileFormat format,List formats){
		Iterator it = formats.iterator();
		while(it.hasNext()){
			TGFileFormat comparator = (TGFileFormat)it.next();
			if(comparator.getName().equals(format.getName()) || comparator.getSupportedFormats().equals(format.getSupportedFormats())){
				return true;
			}
		}
		return false;
	}
	
	private void addDefatultStreams(){
		this.addInputStream(new org.herac.tuxguitar.io.tg.TGInputStream());
		this.addInputStream(new org.herac.tuxguitar.io.tg11.TGInputStream());
		this.addInputStream(new org.herac.tuxguitar.io.tg10.TGInputStream());
	}
}
