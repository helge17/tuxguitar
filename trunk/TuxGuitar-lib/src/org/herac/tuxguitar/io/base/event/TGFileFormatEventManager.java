package org.herac.tuxguitar.io.base.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.base.TGRawImporter;

public class TGFileFormatEventManager {
	
	private List inputStreamAddedListeners;
	private List inputStreamRemovedListeners;
	private List outputStreamAddedListeners;
	private List outputStreamRemovedListeners;	
	private List rawImporterAddedListeners;
	private List rawImporterRemovedListeners;
	private List rawExporterAddedListeners;
	private List rawExporterRemovedListeners;
	
	public TGFileFormatEventManager(){
		this.inputStreamAddedListeners = new ArrayList();
		this.inputStreamRemovedListeners = new ArrayList();
		this.outputStreamAddedListeners = new ArrayList();
		this.outputStreamRemovedListeners = new ArrayList();
		this.rawImporterAddedListeners = new ArrayList();
		this.rawImporterRemovedListeners = new ArrayList();
		this.rawExporterAddedListeners = new ArrayList();
		this.rawExporterRemovedListeners = new ArrayList();
	}
	
	public void addInputStreamAddedListener(TGInputStreamAddedListener listener){
		if(!this.inputStreamAddedListeners.contains(listener)){
			this.inputStreamAddedListeners.add(listener);
		}
	}
	
	public void removeInputStreamAddedListener(TGInputStreamAddedListener listener){
		if( this.inputStreamAddedListeners.contains(listener) ){
			this.inputStreamAddedListeners.remove(listener);
		}
	}
	
	public void addInputStreamRemovedListener(TGInputStreamRemovedListener listener){
		if(!this.inputStreamRemovedListeners.contains(listener)){
			this.inputStreamRemovedListeners.add(listener);
		}
	}
	
	public void removeInputStreamRemovedListener(TGInputStreamRemovedListener listener){
		if( this.inputStreamRemovedListeners.contains(listener) ){
			this.inputStreamRemovedListeners.remove(listener);
		}
	}
	
	public void addOutputStreamAddedListener(TGOutputStreamAddedListener listener){
		if(!this.outputStreamAddedListeners.contains(listener)){
			this.outputStreamAddedListeners.add(listener);
		}
	}
	
	public void removeOutputStreamAddedListener(TGOutputStreamAddedListener listener){
		if( this.outputStreamAddedListeners.contains(listener) ){
			this.outputStreamAddedListeners.remove(listener);
		}
	}
	
	public void addOutputStreamRemovedListener(TGOutputStreamRemovedListener listener){
		if(!this.outputStreamRemovedListeners.contains(listener)){
			this.outputStreamRemovedListeners.add(listener);
		}
	}
	
	public void removeOutputStreamRemovedListener(TGOutputStreamRemovedListener listener){
		if( this.outputStreamRemovedListeners.contains(listener) ){
			this.outputStreamRemovedListeners.remove(listener);
		}
	}
	
	public void addRawImporterAddedListener(TGRawImporterAddedListener listener){
		if(!this.rawImporterAddedListeners.contains(listener)){
			this.rawImporterAddedListeners.add(listener);
		}
	}
	
	public void removeRawImporterAddedListener(TGRawImporterAddedListener listener){
		if( this.rawImporterAddedListeners.contains(listener) ){
			this.rawImporterAddedListeners.remove(listener);
		}
	}
	
	public void addRawImporterRemovedListener(TGRawImporterRemovedListener listener){
		if(!this.rawImporterRemovedListeners.contains(listener)){
			this.rawImporterRemovedListeners.add(listener);
		}
	}
	
	public void removeRawImporterRemovedListener(TGRawImporterRemovedListener listener){
		if( this.rawImporterRemovedListeners.contains(listener) ){
			this.rawImporterRemovedListeners.remove(listener);
		}
	}
	
	public void addRawExporterAddedListener(TGRawExporterAddedListener listener){
		if(!this.rawExporterAddedListeners.contains(listener)){
			this.rawExporterAddedListeners.add(listener);
		}
	}
	
	public void removeRawExporterAddedListener(TGRawExporterAddedListener listener){
		if( this.rawExporterAddedListeners.contains(listener) ){
			this.rawExporterAddedListeners.remove(listener);
		}
	}
	
	public void addRawExporterRemovedListener(TGRawExporterRemovedListener listener){
		if(!this.rawExporterRemovedListeners.contains(listener)){
			this.rawExporterRemovedListeners.add(listener);
		}
	}
	
	public void removeRawExporterRemovedListener(TGRawExporterRemovedListener listener){
		if( this.rawExporterRemovedListeners.contains(listener) ){
			this.rawExporterRemovedListeners.remove(listener);
		}
	}
	
	public void onInputStreamAdded(TGInputStreamBase stream) {
		Iterator it = this.inputStreamAddedListeners.iterator();
		while( it.hasNext() ){
			TGInputStreamAddedListener listener = (TGInputStreamAddedListener) it.next();
			listener.onInputStreamAdded(stream);
		}
	}
	
	public void onInputStreamRemoved(TGInputStreamBase stream) {
		Iterator it = this.inputStreamRemovedListeners.iterator();
		while( it.hasNext() ){
			TGInputStreamRemovedListener listener = (TGInputStreamRemovedListener) it.next();
			listener.onInputStreamRemoved(stream);
		}
	}
	
	public void onOutputStreamAdded(TGOutputStreamBase stream) {
		Iterator it = this.outputStreamAddedListeners.iterator();
		while( it.hasNext() ){
			TGOutputStreamAddedListener listener = (TGOutputStreamAddedListener) it.next();
			listener.onOutputStreamAdded(stream);
		}
	}
	
	public void onOutputStreamRemoved(TGOutputStreamBase stream) {
		Iterator it = this.outputStreamRemovedListeners.iterator();
		while( it.hasNext() ){
			TGOutputStreamRemovedListener listener = (TGOutputStreamRemovedListener) it.next();
			listener.onOutputStreamRemoved(stream);
		}
	}
	
	public void onRawImporterAdded(TGRawImporter importer) {
		Iterator it = this.rawImporterAddedListeners.iterator();
		while( it.hasNext() ){
			TGRawImporterAddedListener listener = (TGRawImporterAddedListener) it.next();
			listener.onRawImporterAdded(importer);
		}
	}
	
	public void onRawImporterRemoved(TGRawImporter importer) {
		Iterator it = this.rawImporterRemovedListeners.iterator();
		while( it.hasNext() ){
			TGRawImporterRemovedListener listener = (TGRawImporterRemovedListener) it.next();
			listener.onRawImporterRemoved(importer);
		}
	}
	
	public void onRawExporterAdded(TGRawExporter exporter) {
		Iterator it = this.rawExporterAddedListeners.iterator();
		while( it.hasNext() ){
			TGRawExporterAddedListener listener = (TGRawExporterAddedListener) it.next();
			listener.onRawExporterAdded(exporter);
		}
	}
	
	public void onRawExporterRemoved(TGRawExporter exporter) {
		Iterator it = this.rawExporterRemovedListeners.iterator();
		while( it.hasNext() ){
			TGRawExporterRemovedListener listener = (TGRawExporterRemovedListener) it.next();
			listener.onRawExporterRemoved(exporter);
		}
	}
}
