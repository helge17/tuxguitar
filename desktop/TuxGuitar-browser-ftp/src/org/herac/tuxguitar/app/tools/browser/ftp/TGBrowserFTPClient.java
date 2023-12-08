package org.herac.tuxguitar.app.tools.browser.ftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TGBrowserFTPClient {
	
	public static final int DEFAULT_PORT = 21;
	
	public static final String DEFAULT_USER_NAME = "anonymous";
	public static final String DEFAULT_USER_PASSWORD = "anonymous";
	
	private static final String REQUEST_USER_NAME = "USER";
	private static final String REQUEST_USER_PASSWORD = "PASS";
	private static final String REQUEST_EXIT = "QUIT";
	private static final String REQUEST_TYPE_ASCII = "TYPE A";
	private static final String REQUEST_TYPE_BINARY = "TYPE I";
	private static final String REQUEST_PWD = "PWD";
	private static final String REQUEST_CD = "CWD";
	private static final String REQUEST_CDUP = "CDUP";
	private static final String REQUEST_PASSIVE = "PASV";
	private static final String REQUEST_GET = "RETR";
	private static final String REQUEST_LIST_FILES = "LIST";
	private static final String REQUEST_LIST_NAMES = "NLST";
	
	private static final int RESPONSE_CODE_CONNECT = 220;
	private static final int RESPONSE_CODE_USER_NAME = 331;
	private static final int RESPONSE_CODE_USER_PASSWORD = 230;
	private static final int RESPONSE_CODE_PWD = 257;
	private static final int RESPONSE_CODE_CD = 250;
	private static final int RESPONSE_CODE_TYPE = 200;
	private static final int RESPONSE_CODE_PASV = 227;
	private static final int RESPONSE_CODE_PASV_CONNECTION = 150;
	
	private boolean open;
	private Socket socket = null;
	private BufferedWriter request = null;
	private BufferedReader response = null;
	
	public boolean isOpen() {
		return this.open;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public synchronized void open(String host, int port) throws IOException {
		this.socket = new Socket(host, port);
		this.response = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.request = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		
		String response = getResponse(RESPONSE_CODE_CONNECT);
		if (!isExpectedResponse(response, RESPONSE_CODE_CONNECT)) {
			throw new IOException(response);
		}
		this.setOpen( true );
	}
	
	public synchronized void close() throws IOException {
		if( this.isOpen() ){
			this.setRequest(REQUEST_EXIT);
			this.socket.close();
			this.socket = null;
		}
		this.setOpen( false );
	}
	
	public synchronized void login(String user, String password) throws IOException {
		String error = null;
		
		this.setRequest(REQUEST_USER_NAME + " " + user);
		if ((error = getResponseError(RESPONSE_CODE_USER_NAME)) != null ){
			throw new IOException(error);
		}
		
		this.setRequest(REQUEST_USER_PASSWORD + " " + password);
		if ((error = getResponseError(RESPONSE_CODE_USER_PASSWORD)) != null ){
			throw new IOException(error);
		}
	}
	
	public synchronized String pwd() throws IOException {
		this.setRequest(REQUEST_PWD);
		String response = getResponse(RESPONSE_CODE_PWD);
		if (isExpectedResponse(response, RESPONSE_CODE_PWD)) {
			int i1 = response.indexOf('\"');
			int i2 = response.indexOf('\"', i1 + 1);
			if (i2 > i1) {
				return response.substring(i1 + 1, i2);
			}
		}
		return null;
	}
	
	public synchronized boolean cd(String dir) throws IOException {
		this.setRequest(REQUEST_CD + " " + dir);
		return (getResponseError(RESPONSE_CODE_CD) == null);
	}
	
	public synchronized boolean cdUp() throws IOException {
		this.setRequest(REQUEST_CDUP);
		return (getResponseError(RESPONSE_CODE_CD) == null);
	}
	
	public synchronized boolean binary() throws IOException {
		this.setRequest(REQUEST_TYPE_BINARY);
		return (getResponseError(RESPONSE_CODE_TYPE) == null);
	}
	
	public synchronized boolean ascii() throws IOException {
		this.setRequest(REQUEST_TYPE_ASCII);
		return (getResponseError(RESPONSE_CODE_TYPE) == null);
	}
	
	public synchronized byte[] get(String file)  throws IOException{
		return sendPasvCMD(REQUEST_GET + " " + file);
	}
	
	public synchronized String[] listDetails() throws IOException{
		return new String( sendPasvCMD(REQUEST_LIST_FILES) ).split("\r\n");
	}
	
	public synchronized String[] listNames() throws IOException{
		return new String( sendPasvCMD(REQUEST_LIST_NAMES) ).split("\r\n");
	}
	
	private synchronized byte[] sendPasvCMD(String cmd)  throws IOException{
		byte[] buffer = new byte[0];
		
		setRequest(REQUEST_PASSIVE);
		
		String response = getResponse(RESPONSE_CODE_PASV);
		if(isExpectedResponse(response, RESPONSE_CODE_PASV)){
			setRequest(cmd);
			
			int addressIndex1 = response.indexOf("(") + 1;
			int addressIndex2 = response.indexOf(")");
			if( addressIndex1 > 0 && addressIndex2 > addressIndex1 ){
				String[] address = response.substring( addressIndex1, addressIndex2 ).split(",");
				if(address.length == 6){
					int a1 = Integer.parseInt(address[0]);
					int a2 = Integer.parseInt(address[1]);
					int a3 = Integer.parseInt(address[2]);
					int a4 = Integer.parseInt(address[3]);
					int p1 = Integer.parseInt(address[4]);
					int p2 = Integer.parseInt(address[5]);
					
					Socket socket = new Socket((a1 + "." + a2 + "." + a3 + "." + a4), ((p1 * 256) + p2));
					
					response = getResponse(RESPONSE_CODE_PASV_CONNECTION);
					if(isExpectedResponse(response, RESPONSE_CODE_PASV_CONNECTION)){
						buffer = getByteBuffer(socket.getInputStream());
					}
					socket.close();
				}
			}
			if(isExpectedResponse(response, RESPONSE_CODE_PASV_CONNECTION)){
				response = getResponse();
			}
		}
		return buffer;
	}
	
	private boolean isExpectedResponse(String response, int expectedCode){
		return ( response != null && response.indexOf(expectedCode + " ") == 0 );
	}
	
	private String getResponseError(int expectedCode) throws IOException{
		String response = getResponse(expectedCode);
		return (isExpectedResponse(response, expectedCode) ? null : response); 
	}
	
	private String getResponse(int expectedCode) throws IOException{
		String response = this.getResponse();
		// Some FTP Servers appends comments like "[CODE_NUMBER]-".
		// This while tries to skip all comments before response.
		while( response != null && response.indexOf(expectedCode + "-") == 0){
			response = this.getResponse();
		}
		return response; 
	}
	
	private String getResponse() throws IOException {
		return this.response.readLine();
	}
	
	private void setRequest(String line) throws IOException {
		this.request.write(line + "\r\n");
		this.request.flush();
	}
	
	private static byte[] getByteBuffer(InputStream in) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		int read = 0;
		while((read = in.read()) != -1){
			out.write(read);
		}
		
		byte[] bytes = out.toByteArray();
		
		out.close();
		out.flush();
		
		return bytes;
	}
}
