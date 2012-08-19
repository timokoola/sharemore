package com.moarub.kipptapi;

public class KipptClipItem {
	private boolean fReadLater;
	private boolean fStarred;
	private String fUrl;
	private String fTitle;
	private String fListResourceUri;
	private String fNotes;
	
	
	
	public KipptClipItem(boolean fReadLater, boolean fStarred, String fUrl,
			String fTitle, String fListResourceUri, String fNotes) {
		super();
		this.fReadLater = fReadLater;
		this.fStarred = fStarred;
		this.fUrl = fUrl;
		this.fTitle = fTitle;
		this.fListResourceUri = fListResourceUri;
		this.fNotes = fNotes;
	}
	
	
	public boolean getfReadLater() {
		return fReadLater;
	}
	public boolean getfStarred() {
		return fStarred;
	}
	public String getfUrl() {
		return fUrl;
	}
	public String getfTitle() {
		return fTitle;
	}
	public String getfListResourceUri() {
		return fListResourceUri;
	}
	public String getfNotes() {
		return fNotes;
	}
	
	
	
}
