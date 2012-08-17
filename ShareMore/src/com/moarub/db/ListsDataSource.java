package com.moarub.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ListsDataSource {

	// Database fields
	private SQLiteDatabase fDatabase;
	private ListsDbHelper fDBHelper;
	private String[] allColumns = { ListsDbHelper.LIST_ID,
			ListsDbHelper.COLUMN_TITLE, ListsDbHelper.COLUMN_URI };

	public ListsDataSource(Context context) {
		fDBHelper = new ListsDbHelper(context);
	}

	public void open() throws SQLException {
		fDatabase = fDBHelper.getWritableDatabase();
	}

	public void close() {
		fDBHelper.close();
	}

	public ListItem createList(String l, String uri) {
		Cursor query = fDatabase.rawQuery(
				"select * from lists where title = ?", new String[] { l });
		if (query.moveToFirst()) {
			return null;
		}

		ContentValues values = new ContentValues();
		values.put(ListsDbHelper.COLUMN_TITLE, l);
		values.put(ListsDbHelper.COLUMN_URI, uri);
		long insertId = fDatabase.insert(ListsDbHelper.TABLE_LISTS, null,
				values);
		Cursor cursor = fDatabase.query(ListsDbHelper.TABLE_LISTS, allColumns,
				ListsDbHelper.LIST_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		ListItem newList = cursorToList(cursor);
		cursor.close();
		return newList;
	}

	public void deleteList(ListItem l) {
		long id = l.getId();
		System.out.println("List deleted with id: " + id);
		fDatabase.delete(ListsDbHelper.TABLE_LISTS, ListsDbHelper.LIST_ID
				+ " = " + id, null);
	}

	public ArrayList<ListItem> getAllLists() {
		ArrayList<ListItem> Lists = new ArrayList<ListItem>();

		Cursor cursor = fDatabase.query(ListsDbHelper.TABLE_LISTS, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ListItem l = cursorToList(cursor);
			Lists.add(l);
			cursor.moveToNext();
		}
		cursor.close();
		return Lists;
	}

	private ListItem cursorToList(Cursor cursor) {
		ListItem l = new ListItem(cursor.getString(1), cursor.getLong(0));
		l.setResourceUri(cursor.getString(2));
		return l;
	}
}