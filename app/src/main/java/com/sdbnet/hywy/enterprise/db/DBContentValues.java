package com.sdbnet.hywy.enterprise.db;

public class DBContentValues {
	// news
	public final static String TABLE_NEWS = "news";
	public final static String TABLE_NEWS_CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NEWS
			+ " (_id integer primary key autoincrement, update_time text timestamp, json_path text not null );";

	// detailRecord;
	public final static String TABLE_DETAIL_RECORD = "detailRecord";
	public final static String TABLE_DETAIL_RECORD_CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_DETAIL_RECORD
			+ " (_id integer primary key autoincrement, url text ,key text ,good integer ,bad integer collect integer);";
	// imageCache;
	public final static String TABLE_IMAGE_CACHE = "imageCache";
	public final static String TABLE_IMAGE_CACHE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_IMAGE_CACHE
			+ " (_id integer primary key autoincrement, timestamp TimeStamp,url text ,past_time TimeStamp );";

	// request cache;
	public final static String TABLE_TEQUEST_CACHE = "request_cache";
	public final static String TABLE_TEQUEST_CACHE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_TEQUEST_CACHE
			+ " (_id integer primary key autoincrement,url text ,source_type text, content_type text,timestamp integer );";

}
