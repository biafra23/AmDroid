package com.jaeckel.amenoid.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * User: biafra
 * Date: 11/29/11
 * Time: 11:11 PM
 */
public class StatementMetadata {

	private StatementMetadata() {
  }

	public static final String AUTHORITY = "com.jaeckel.amenoid.provider";
	public static final Uri CONTENT_URI = Uri.parse(
    "content://" + AUTHORITY + "/statements"
                                                 );

	public static final String DATABASE_NAME = "amen.db";
	public static final int DATABASE_VERSION = 1;

	public static final String CONTENT_TYPE_AMEN_LIST = "vnd.android.cursor.dir/vnd.amenoid.statements";
	public static final String CONTENT_TYPE_AMEN_ONE = "vnd.android.cursor.item/vnd.amenoid.statements";

	public class ArticleTable implements BaseColumns {
		private ArticleTable() { }

		public static final String TABLE_NAME = "tbl_amen";

		public static final String ID = "_id";
		public static final String TITLE = "title";
		public static final String CONTENT = "content";
	}

}


