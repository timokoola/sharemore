package com.moarub.util;

public interface UrlDeshortenerListener {

	public abstract void onURLDeshortened(String resolution, int responseCode);
	public abstract void onTitleUpdate(String newTitle);
}
