package com.moarub.util;

public class DeshorteningResult {
	private String fResValue;
	private int fStatusCode;

	public int getStatusCode() {
		return fStatusCode;
	}

	public DeshorteningResult(String res, int status) {
		super();
		setResValue(res);
		fStatusCode = status;
	}

	public String getResValue() {
		return fResValue;
	}

	public void setResValue(String res) {
		fResValue = res;
	}

}