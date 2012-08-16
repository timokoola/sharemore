package com.moarub.db;

public class ListItem {
	private String fName;
	private String fResourceUri;
	private long fId;
	
	public ListItem(String name, long l) {
		fName = name;
		fId = l;
	}
	
	public void setResourceUri(String rUri) {
		fResourceUri = rUri;
	}
	
	public long getId() {
		return fId;
	}
	
	@Override
	public String toString() {
		return fName;
	}

	public String getUri() {
		return fResourceUri;
	}
	
}
