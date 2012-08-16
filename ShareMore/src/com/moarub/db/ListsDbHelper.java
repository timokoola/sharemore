/*******************************************************************************
 * Copyright (c) 2012 Moarub Oy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Moarub Oy - initial API and implementation
 ******************************************************************************/
package com.moarub.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ListsDbHelper extends SQLiteOpenHelper {
	public static final String TABLE_LISTS = "lists";
	public static final String LIST_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_URI = "resource_uri";

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + TABLE_LISTS
			+ "(" + LIST_ID + " integer primary key autoincrement, "
			+ COLUMN_TITLE + " text not null unique, " + COLUMN_URI
			+ " text not null unique);";

	private static final String DATABASE_NAME = "lists.db";
	private static final int DATABASE_VERSION = 1;

	public ListsDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
		Log.d("ListsDbHelper", "Upgrading " +  TABLE_LISTS + "from (" + oldV + ") to (" + newV + ")");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
		onCreate(db);
	}

}
