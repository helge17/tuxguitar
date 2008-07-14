package org.herac.tuxguitar.gui.tools.browser.ftp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.gui.tools.browser.TGBrowserException;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserElement;

public class TGBrowserImpl extends TGBrowser{
	
	private TGBrowserDataImpl data;
	private String root; 
	private String path; 
	private TGBrowserFTPClient client;
	
	public TGBrowserImpl(TGBrowserDataImpl data){
		this.data = data;
	}
	
	private String getRoot(){
		if(this.root == null){
			this.root = "/";
			if(this.data.getPath() != null && this.data.getPath().length() > 0){
				this.root = this.data.getPath();
				if(this.root.indexOf("/") != 0){
					this.root = ("/" + this.root);
				}
			}
		}
		return this.root;
	}
	
	public void open() throws TGBrowserException{
		try {
			this.client = new TGBrowserFTPClient();
			this.client.open(this.data.getHost(), TGBrowserFTPClient.DEFAULT_PORT);
			this.client.login(this.data.getUsername(),this.data.getPassword());
			this.cdRoot();
		} catch (Throwable throwable) {
			throw new TGBrowserException(throwable);
		}
	}
	
	public void close() throws TGBrowserException{
		try {
			this.client.close();
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
			String[] names = this.client.listNames();
			String[] infos = this.client.listDetails();
			
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
			
			byte[] bytes = this.client.get(element.getName());
			
			this.client.ascii();
			
			return new ByteArrayInputStream( bytes );
		} catch (Throwable throwable) {
			throw new TGBrowserException(throwable);
		}
	}
}
