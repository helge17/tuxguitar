package org.herac.tuxguitar.io.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.io.tg.TGInputStream;
import org.herac.tuxguitar.io.tg.TGOutputStream;
import org.herac.tuxguitar.io.tg.TGStream;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGFileFormatManager {
	
	public static final String DEFAULT_EXTENSION = ("." + TGStream.TG_FORMAT_CODE);
	
	private TGContext context;
	private TGSongLoaderHelper loader;
	private TGSongWriterHelper writer;
	private List<TGInputStreamBase> inputStreams;
	private List<TGOutputStreamBase> outputStreams;
	private List<TGRawExporter> exporters;
	private List<TGRawImporter> importers;
	
	private TGFileFormatManager(TGContext context){
		this.context = context;
		this.loader = new TGSongLoaderHelper(this.context);
		this.writer = new TGSongWriterHelper(this.context);
		this.inputStreams = new ArrayList<TGInputStreamBase>();
		this.outputStreams = new ArrayList<TGOutputStreamBase>();
		this.exporters = new ArrayList<TGRawExporter>();
		this.importers = new ArrayList<TGRawImporter>();
		this.addDefaultStreams();
	}

	public TGSongLoaderHelper getLoader(){
		return this.loader;
	}
	
	public TGSongWriterHelper getWriter(){
		return this.writer;
	}
	
	public void addInputStream(TGInputStreamBase stream){
		if(!this.inputStreams.contains(stream)){
			this.inputStreams.add(stream);
			this.fireFileFormatAvailabilityEvent();
		}
	}
	
	public void removeInputStream(TGInputStreamBase stream){
		if( this.inputStreams.contains(stream)){
			this.inputStreams.remove(stream);
			this.fireFileFormatAvailabilityEvent();
		}
	}
	
	public int countInputStreams(){
		return this.inputStreams.size();
	}
	
	public void addOutputStream(TGOutputStreamBase stream){
		if(!this.outputStreams.contains(stream)){
			this.outputStreams.add(stream);
			this.fireFileFormatAvailabilityEvent();
		}
	}
	
	public void removeOutputStream(TGOutputStreamBase stream){
		if( this.outputStreams.contains(stream)){
			this.outputStreams.remove(stream);
			this.fireFileFormatAvailabilityEvent();
		}
	}
	
	public int countOutputStreams(){
		return this.outputStreams.size();
	}
	
	public void addImporter(TGRawImporter importer){
		if(!this.importers.contains(importer)){
			this.importers.add(importer);
			this.fireFileFormatAvailabilityEvent();
		}
	}
	
	public void removeImporter(TGRawImporter importer){
		if( this.importers.contains(importer)){
			this.importers.remove(importer);
			this.fireFileFormatAvailabilityEvent();
		}
	}
	
	public int countImporters(){
		return this.importers.size();
	}
	
	public void addExporter(TGRawExporter exporter){
		if(!this.exporters.contains(exporter)){
			this.exporters.add(exporter);
			this.fireFileFormatAvailabilityEvent();
		}
	}
	
	public void removeExporter(TGRawExporter exporter){
		if( this.exporters.contains(exporter)){
			this.exporters.remove(exporter);
			this.fireFileFormatAvailabilityEvent();
		}
	}
	
	public int countExporters(){
		return this.exporters.size();
	}
	
	public Iterator<TGInputStreamBase> getInputStreams(){
		return this.inputStreams.iterator();
	}
	
	public Iterator<TGOutputStreamBase> getOutputStreams(){
		return this.outputStreams.iterator();
	}
	
	public Iterator<TGRawImporter> getImporters(){
		return this.importers.iterator();
	}
	
	public Iterator<TGRawExporter> getExporters(){
		return this.exporters.iterator();
	}
	
	public List<TGFileFormat> getInputFormats(){
		List<TGFileFormat> formats = new ArrayList<TGFileFormat>();
		Iterator<TGInputStreamBase> it = getInputStreams();
		while(it.hasNext()){
			TGInputStreamBase stream = (TGInputStreamBase)it.next();
			TGFileFormat format = stream.getFileFormat();
			if(!existsFormat(format, formats)){
				formats.add(format);
			}
		}
		return formats;
	}
	
	public List<TGFileFormat> getOutputFormats(){
		List<TGFileFormat> formats = new ArrayList<TGFileFormat>();
		Iterator<TGOutputStreamBase> it = getOutputStreams();
		while(it.hasNext()){
			TGOutputStreamBase stream = (TGOutputStreamBase)it.next();
			TGFileFormat format = stream.getFileFormat();
			if(!existsFormat(format, formats)){
				formats.add(format);
			}
		}
		return formats;
	}
	
	private boolean existsFormat(TGFileFormat format, List<TGFileFormat> formats){
		Iterator<TGFileFormat> it = formats.iterator();
		while(it.hasNext()){
			TGFileFormat comparator = (TGFileFormat)it.next();
			if(comparator.getName().equals(format.getName()) || comparator.getSupportedFormats().equals(format.getSupportedFormats())){
				return true;
			}
		}
		return false;
	}
	
	private void addDefaultStreams(){
		this.addInputStream(new TGInputStream());
		this.addOutputStream(new TGOutputStream());
	}
	
	public void fireFileFormatAvailabilityEvent(){
		TGEventManager.getInstance(this.context).fireEvent(new TGFileFormatAvailabilityEvent());
	}
	
	public void addFileFormatAvailabilityListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(TGFileFormatAvailabilityEvent.EVENT_TYPE, listener);
	}
	
	public void removeFileFormatAvailabilityListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(TGFileFormatAvailabilityEvent.EVENT_TYPE, listener);
	}
	
	public static TGFileFormatManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGFileFormatManager.class.getName(), new TGSingletonFactory<TGFileFormatManager>() {
			public TGFileFormatManager createInstance(TGContext context) {
				return new TGFileFormatManager(context);
			}
		});
	}
}
