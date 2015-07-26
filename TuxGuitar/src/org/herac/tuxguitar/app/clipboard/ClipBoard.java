/*
 * Created on 09-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.clipboard;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClipBoard {
	private Transferable transferable;
	
	public ClipBoard(){
		this.transferable = null;
	}
	
	public void addTransferable(Transferable transferable){
		this.transferable = transferable;
	}
	
	public Transferable getTransferable(){
		return this.transferable;
	}
	
	public void insertTransfer() throws CannotInsertTransferException{
		if(this.isEmpty()){
			throw new CannotInsertTransferException();
		}
		this.transferable.insertTransfer();
	}
	
	public boolean isEmpty(){
		return (this.transferable == null);
	}
}