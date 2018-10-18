package pro.rasht.museum.ar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pro.rasht.museum.ar.Model.Model_Gallery;


public class DatabaseHelpher extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "rsm";

    private static final int DATABASE_VERSION = 1;
    private static final String GALLERY_TABLE = "gallery";


    private static final String IMG_TABLE = "create table "
            + GALLERY_TABLE +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT , like_img TEXT , state TEXT)";

    Context context;

    public DatabaseHelpher(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(IMG_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + GALLERY_TABLE);

        // Create tables again
        onCreate(db);
    }


    public boolean updateDb(long id,String state){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put("state", state);
        int i =  db.update(GALLERY_TABLE, args, "id=" + id, null);
        return i > 0;
    }


    /* Insert into database*/
    public Long insertIntoDB(String like_img, String state) {


        Log.d("insert", "before insert");

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("like_img", like_img);
        values.put("state", state);


        // 3. insert
        Long id= db.insert(GALLERY_TABLE, null, values);


        // 4. close
        db.close();
        Toast.makeText(context, "insert value", Toast.LENGTH_LONG);

        Log.i("insert into DB", "After insert");

        return id;
    }


    /* Retrive  data from database */
    public List<Model_Gallery> getDataFromDB() {
        List<Model_Gallery> modelList = new ArrayList<Model_Gallery>();
        String query = "select * from " + GALLERY_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Model_Gallery model = new Model_Gallery();
                model.setImage_img(cursor.getString(1));
                model.setLike_img(cursor.getString(2));


                modelList.add(model);
            } while (cursor.moveToNext());
        }


        Log.d("student data", modelList.toString());


        return modelList;
    }


    /*delete a row from database*/
    public void deleteARow(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(GALLERY_TABLE, "email" + " = ?", new String[]{email});
        db.close();
    }



    /*delete a row from database*/
    public void deleteDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ GALLERY_TABLE);
        db.close();
    }

    public String selectDb(String like_img){


        String query = "select * from " + GALLERY_TABLE + " where like_img = " + like_img;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                return cursor.getString(0);

            } while (cursor.moveToNext());
        }


        return null;
    }



}
