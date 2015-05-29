package org.herac.tuxguitar.io.gpx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GPXFileSystem {
	
	private static final int HEADER_BCFS = 1397113666;
	private static final int HEADER_BCFZ = 1514554178;
	
	private List<GPXFile> fileSystem;
	
	public GPXFileSystem(){
		this.fileSystem = new ArrayList<GPXFile>();
	}
	
	public List<String> getFileNames(){
		List<String> gpxFileNames = new ArrayList<String>();
		
		Iterator<GPXFile> it = this.fileSystem.iterator();
		while( it.hasNext() ){
			GPXFile file = (GPXFile)it.next();
			gpxFileNames.add( file.getFileName() );
		}
		
		return gpxFileNames;
	}
	
	public byte[] getFileContents( String fileName ){
		Iterator<GPXFile> it = this.fileSystem.iterator();
		while( it.hasNext() ){
			GPXFile file = (GPXFile)it.next();
			if( file.getFileName().equals( fileName ) ){
				return file.getFileContents();
			}
		}
		return null;
	}
	
	public String getFileContentsAsString( String fileName ){
		byte[] fileContents = this.getFileContents(fileName);
		if( fileContents != null ){
			return getString(fileContents, 0, fileContents.length);
		}
		return null;
	}
	
	public InputStream getFileContentsAsStream( String fileName ){
		byte[] fileContents = this.getFileContents(fileName);
		if( fileContents != null ){
			return new ByteArrayInputStream( fileContents );
		}
		return null;
	}
	
	public boolean isSupportedHeader( int header ){
		return (header == HEADER_BCFS || header == HEADER_BCFZ);
	}
	
	public int getHeader(InputStream in) throws Throwable{
		return getInteger(in);
	}
	
	public void load(InputStream in) throws Throwable {
		this.load(getHeader(in), in);
	}
	
	public void load(int header, InputStream in) throws Throwable {
		this.load(header, new GPXByteBuffer( getBytes(in) ) );
	}
	
	public void load(int header, GPXByteBuffer srcBuffer) throws Throwable {
		if (header == HEADER_BCFS ) {
			byte[] bcfsBytes = srcBuffer.readBytes( srcBuffer.length() );
			
			int sectorSize = 0x1000;
			int offset = 0;
			while ( (offset = (offset + sectorSize)) + 3 < bcfsBytes.length ) {
				if (getInteger(bcfsBytes, offset) == 2) {					
					int indexFileName = (offset + 4);
					int indexFileSize = (offset + 0x8C);
					int indexOfBlock  = (offset + 0x94);
					
					int block = 0;
					int blockCount = 0;
					ByteArrayOutputStream fileBytesStream = new ByteArrayOutputStream();
					while( (block = (getInteger(bcfsBytes, (indexOfBlock + (4 * (blockCount ++)))))) != 0 ){
						fileBytesStream.write( getBytes(bcfsBytes, (offset = (block * sectorSize)), sectorSize) );
					}
					
					int fileSize = getInteger(bcfsBytes , indexFileSize);
					byte[] fileBytes = fileBytesStream.toByteArray();
					if ( fileBytes.length >= fileSize ){
						this.fileSystem.add(new GPXFile(getString(bcfsBytes, indexFileName, 127), getBytes(fileBytes, 0, fileSize)));
					}
				}
			}
		} else if ( header == HEADER_BCFZ ) {
			ByteArrayOutputStream bcfsBuffer = new ByteArrayOutputStream();
			
			int expectLength = getInteger(srcBuffer.readBytes(4),0);
			while ( !srcBuffer.end() && srcBuffer.offset() < expectLength ){
				int flag = srcBuffer.readBits(1);
				if (flag == 1) {
					int bits = srcBuffer.readBits(4);
					int offs = srcBuffer.readBitsReversed(bits);
					int size = srcBuffer.readBitsReversed(bits);
					
					byte[] bcfsBytes = bcfsBuffer.toByteArray();
					int pos = ( bcfsBytes.length - offs );
					for( int i = 0; i < (size > offs ? offs : size) ; i ++ ){
						bcfsBuffer.write( bcfsBytes[pos + i] ) ;
					}
				} else {
					int size = srcBuffer.readBitsReversed(2);
					for(int i = 0; i < size; i ++ ){
						bcfsBuffer.write( srcBuffer.readBits(8) );
					}
				}
			}
			this.load( new ByteArrayInputStream( bcfsBuffer.toByteArray() ) );
		} else {
			throw new Exception("This is not a GPX file");
		}
	}
	
	private int getInteger(InputStream in) throws Throwable{
		byte[] bytes = new byte[4];
		in.read( bytes );
		return getInteger(bytes, 0);
	}
	
	private byte[] getBytes(InputStream in) throws Throwable {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read = 0;
		while((read = in.read()) != -1){
			out.write(read);
		}
		byte[] bytes = out.toByteArray();
		in.close();
		out.close();
		out.flush();
		return bytes;
	}
	
	private int getInteger(byte[] source, int offset)  {
		byte[] bytes = new byte[4];
		bytes[0] = source[ offset + 0];
		bytes[1] = source[ offset + 1];
		bytes[2] = source[ offset + 2];
		bytes[3] = source[ offset + 3];
		return ((bytes[3] & 0xff) << 24) | ((bytes[2] & 0xff) << 16) | ((bytes[1] & 0xff) << 8) | (bytes[0] & 0xff);
	}
	
	private byte[] getBytes(byte[] source, int offset, int length ){
		byte[] bytes = new byte[length];
		for( int i = 0 ; i < length ; i ++ ){
			if( source.length > offset + i ){
				bytes[i] = source[ offset + i ];
			}
		}
		return bytes;
	}
	
	private String getString(byte[] source, int offset, int length ){
		int charsLength = 0;
		
		char[] chars = new char[length];
		for( int i = 0 ; i < length ; i ++ ){
			int charValue = ((source[ offset + i ]) & 0xff);
			if( charValue == 0 ){
				break;
			}
			chars[i] = (char)charValue;
			charsLength = (i + 1);
		}
		
		char[] string = new char[ charsLength ];
		for( int i = 0 ; i < charsLength ; i ++ ){
			string[i] = chars[i];
		}
		
		return new String( string );
	}
	
	private class GPXFile {
		private String fileName;
		private byte[] fileContents;
		
		public GPXFile(String fileName, byte[] fileContents){
			this.fileName = fileName;
			this.fileContents = fileContents;
		}
		
		public String getFileName() {
			return this.fileName;
		}
		
		public byte[] getFileContents() {
			return this.fileContents;
		}
	}
}
