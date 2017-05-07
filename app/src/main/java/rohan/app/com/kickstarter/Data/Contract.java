package rohan.app.com.kickstarter.Data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by rohan on 06-05-2017.
 */

public class Contract {
    //Database schema information
    public static final String TABLE_PROJECTS = "projects";

    public static final class ProjectsColumns implements BaseColumns {

        public static final String SERIAL_NUMBER = "s_no";
        public static final String AMT_PLEDGED = "amt_pledged";
        public static final String BLURB = "blurb";
        public static final String BY = "by";
        public static final String COUNTRY = "country";
        public static final String CURRENCY = "currency";
        public static final String END_TIME = "end_time";
        public static final String TITLE = "title";
        public static final String LOCATION = "location";
        public static final String PERCENTAGE_FUNDED = "percentage_funded";
        public static final String NUM_BACKERS = "num_backers";
        public static final String STATE = "state";
        public static final String TYPE = "type";
        public static final String URL = "url";
    }

    //Unique authority string for the content provider
    public static final String CONTENT_AUTHORITY = "rohan.app.com.kickstarter";

    /* Sort order constants */
    //Priority first, Completed last, the rest by date
    public static final String DEFAULT_SORT = String.format("%s ASC",
            ProjectsColumns.TITLE);
    //Completed last, then by date, followed by priority
    public static final String DATE_SORT = String.format("%s DESC",
            ProjectsColumns.END_TIME);

    //Base content Uri for accessing the provider
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_PROJECTS)
            .build();


    /* Helpers to retrieve column values */
    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString( cursor.getColumnIndex(columnName) );
    }
    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt( cursor.getColumnIndex(columnName) );
    }
    public static long getColumnLong(Cursor cursor, String columnName) {
        return cursor.getLong( cursor.getColumnIndex(columnName) );
    }
}
