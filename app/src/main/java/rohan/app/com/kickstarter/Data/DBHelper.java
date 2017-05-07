package rohan.app.com.kickstarter.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

/**
 * Created by rohan on 06-05-2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE_TASKS = String.format("create table %s"
                    +" (%s integer primary key , %s integer, %s integer, %s text, %s text, %s text, %s text, " +
                    "%s text, %s text, %s integer, %s text, %s text, %s text, %s text, %s text)",
            Contract.TABLE_PROJECTS,
            Contract.ProjectsColumns._ID,
            Contract.ProjectsColumns.SERIAL_NUMBER,
            Contract.ProjectsColumns.AMT_PLEDGED,
            Contract.ProjectsColumns.BLURB,
            Contract.ProjectsColumns.BY,
            Contract.ProjectsColumns.COUNTRY,
            Contract.ProjectsColumns.CURRENCY,
            Contract.ProjectsColumns.END_TIME,
            Contract.ProjectsColumns.TITLE,
            Contract.ProjectsColumns.LOCATION,
            Contract.ProjectsColumns.PERCENTAGE_FUNDED,
            Contract.ProjectsColumns.NUM_BACKERS,
            Contract.ProjectsColumns.STATE,
            Contract.ProjectsColumns.TYPE,
            Contract.ProjectsColumns.URL
    );

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + Contract.TABLE_PROJECTS);
        onCreate(db);
    }
}
