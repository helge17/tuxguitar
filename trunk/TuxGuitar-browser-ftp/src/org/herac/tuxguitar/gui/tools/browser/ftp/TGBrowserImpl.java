package org.herac.tuxguitar.gui.tools.browser.ftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.gui.tools.browser.TGBrowserException;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserElement;

import sun.net.ftp.FtpClient;

public class TGBrowserImpl extends TGBrowser{
	
	private TGBrowserDataImpl data;
	private String root; 
	private String path; 
	private FtpClient client;
	
	public TGBrowserImpl(TGBrowserDataImpl data){
		this.data = data;
	}
	
	private String getRoot(){
		if(this.root == null){
			this.root = "/";
			if(this.data.getPath() != null && this.data.getPath().length() > 0){
				this.root = this.data.getPath();
			}
		}
		return this.root;
	}
	
	public void open() throws TGBrowserException{
		try {
			this.client = new FtpClient();
			this.client.openServer(this.data.getHost());
			this.client.login(this.data.getUsername(),this.data.getPassword());
			this.cdRoot();
		} catch (Throwable throwable) {
			throw new TGBrowserException(throwable);
		}
	}
	
	public void close() throws TGBrowserException{
		try {
			this.client.closeServer();
		} catch (Throwable throwable) {
			throw new TGBrowserException(throwable);
		}
	}
	
	public void cdElement(TGBrowserElement element) throws TGBrowserException {
		try {
			this.client.cd(element.getName());
			this.path = this.client.pwd();
		} catch (Throwable throwable) {
			throw new TGBrowserException(throwable);
		}
	}
	
	public void cdRoot() throws TGBrowserException {
		try {
			this.client.cd(getRoot());
			this.path = this.client.pwd();
		} catch (Throwable throwable) {
			throw new TGBrowserException(throwable);
		}
	}
	
	public void cdUp() throws TGBrowserException {
		try {
			this.client.cdUp();
			this.path = this.client.pwd();
		} catch (Throwable throwable) {
			throw new TGBrowserException(throwable);
		}
	}
	
	public List listElements() throws TGBrowserException {
		List elements = new ArrayList();
		try {
			this.client.ascii();
			String[] names = parseString(this.client.nameList(this.path)).split("\n");
			String[] infos = parseString(this.client.list()).split("\n");
			if(names.length > 0 && infos.length > 0){
				for(int i = 0;i < names.length;i++){
					String name = names[i].trim();
					
					if(name.indexOf(this.path) == 0 && name.length() > (this.path.length() + 2)){
						name = name.substring(this.path.length() + 1);
					}
					
					for(int j = 0;j < infos.length;j++){
						String info = infos[j].trim();
						if(info.indexOf(name) > 0){
							elements.add(new TGBrowserElementImpl(this,name,info,this.path));
							break;
						}
					}
				}
			}
		} catch (Throwable throwable) {
			throw new TGBrowserException(throwable);
		}
		return elements;
	}
	
	public InputStream getInputStream(String path,TGBrowserElement element)throws TGBrowserException {
		try {
			this.client.cd(path);
			this.client.binary();
			
			byte[] bytes = getByteBuffer( this.client.get(element.getName()) , false );
			
			this.client.ascii();
			
			return new ByteArrayInputStream( bytes );
		} catch (Throwable throwable) {
			throw new TGBrowserException(throwable);
		}
	}
	
	private String parseString(InputStream in) throws TGBrowserException{
		try {
			byte[] bytes = getByteBuffer(in, true);
			
			return new String( bytes );
		} catch (Throwable throwable) {
			throw new TGBrowserException(throwable);
		}
	}
	
	private byte[] getByteBuffer(InputStream in, boolean checkAvailable) throws IOException{
		if(!checkAvailable || in.available() > 0){
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
		in.close();
		
		return new byte[0];
	}
}
