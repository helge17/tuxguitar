package org.herac.tuxguitar.io.gtp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.herac.tuxguitar.gm.GMChannelRoute;
import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.gm.GMChannelRouterConfigurator;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.base.TGSongWriterHandle;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGSong;

public abstract class GTPOutputStream extends GTPFileFormat implements TGSongWriter{
	
	private GMChannelRouter channelRouter;
	private OutputStream outputStream;
	
	public GTPOutputStream(GTPSettings settings){
		super(settings);
	}
	
	public abstract void writeSong(TGSong song) throws TGFileFormatException;
	
	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try {
			this.outputStream = handle.getOutputStream();
			this.init(handle.getFactory());
			this.writeSong(handle.getSong());
		} catch (TGFileFormatException tgFormatException) {
			throw tgFormatException;
		} catch (Throwable throwable) {
			throw new TGFileFormatException(throwable);
		}
	}
	
	protected void skipBytes(int count) throws IOException {
		for(int i = 0;i < count;i++){
			this.outputStream.write(0);
		}
	}
	
	protected void writeByte(byte v) throws IOException {
		this.outputStream.write(v);
	}
	
	protected void writeUnsignedByte(int v) throws IOException {
		this.outputStream.write(v);
	}
	
	protected void writeBytes(byte[] v) throws IOException {
		this.outputStream.write(v);
	}
	
	protected void writeBoolean(boolean v) throws IOException {
		this.outputStream.write(v ? 1 : 0);
	}
	
	protected void writeInt(int v) throws IOException {
		byte[] bytes = { (byte)(v & 0x00FF),(byte)((v >> 8) & 0x000000FF),(byte) ((v >> 16) & 0x000000FF),(byte)((v >> 24) & 0x000000FF) };
		this.outputStream.write(bytes);
	}
	
	protected void writeString(byte[] bytes, int maximumLength) throws IOException {
		int length = (maximumLength == 0 || maximumLength > bytes.length ? bytes.length : maximumLength );
		for(int i = 0 ; i < length; i ++){
			this.outputStream.write( bytes[ i ] );
		}
	}
	
	protected void writeStringInteger(String string, String charset) throws IOException {
		byte[] bytes = string.getBytes(charset);
		this.writeInt( bytes.length );
		this.writeString( bytes , 0 );
	}
	
	protected void writeStringInteger(String string) throws IOException {
		this.writeStringInteger(string, getSettings().getCharset());
	}
	
	protected void writeStringByte(String string, int size, String charset) throws IOException {
		byte[] bytes = string.getBytes(charset);
		this.writeByte( (byte)( size == 0 || size > bytes.length ? bytes.length : size ));
		this.writeString( bytes , size );
		this.skipBytes( size - bytes.length );
	}
	
	protected void writeStringByte(String string, int size) throws IOException {
		this.writeStringByte(string, size, getSettings().getCharset());
	}
	
	protected void writeStringByteSizeOfInteger(String string, String charset) throws IOException {
		byte[] bytes = string.getBytes(charset);
		this.writeInt( (bytes.length + 1) );
		this.writeStringByte(string, bytes.length, charset);
	}
	
	protected void writeStringByteSizeOfInteger(String string) throws IOException {
		writeStringByteSizeOfInteger(string, getSettings().getCharset());
	}
	
	protected void close() throws IOException{
		this.outputStream.flush();
		this.outputStream.close();
	}
	
	protected void configureChannelRouter( TGSong song ){
		this.channelRouter = new GMChannelRouter();
		
		GMChannelRouterConfigurator gmChannelRouterConfigurator = new GMChannelRouterConfigurator(this.channelRouter);
		gmChannelRouterConfigurator.configureRouter(song.getChannels());
	}
	
	protected GMChannelRoute getChannelRoute(int channelId){
		GMChannelRoute gmChannelRoute = this.channelRouter.getRoute(channelId);
		if( gmChannelRoute == null || gmChannelRoute.getChannel1() < 0  || gmChannelRoute.getChannel2() < 0 ){
			Integer defaultChannel = (gmChannelRoute != null && gmChannelRoute.getChannel1() >= 0 ? gmChannelRoute.getChannel1() : 15);
			
			gmChannelRoute = new GMChannelRoute(channelId);
			gmChannelRoute.setChannel1(defaultChannel);
			gmChannelRoute.setChannel2(defaultChannel);
		}
		
		return gmChannelRoute;
	}
	
	protected boolean isPercussionChannel( TGSong song, int channelId ){
		Iterator<TGChannel> it = song.getChannels();
		while( it.hasNext() ){
			TGChannel channel = (TGChannel)it.next();
			if( channel.getChannelId() == channelId ){
				return channel.isPercussionChannel();
			}
		}
		return false;
	}
}
