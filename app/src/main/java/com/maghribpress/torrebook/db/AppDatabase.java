package com.maghribpress.torrebook.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.maghribpress.torrebook.db.converter.BitmapConverter;
import com.maghribpress.torrebook.db.entity.Author;
import com.maghribpress.torrebook.db.entity.Book;
import com.maghribpress.torrebook.db.entity.BookAuthor;
import com.maghribpress.torrebook.db.entity.BookCategory;
import com.maghribpress.torrebook.db.dao.BookDao;
import com.maghribpress.torrebook.db.entity.Category;
import com.maghribpress.torrebook.db.entity.Tracking;


@Database(entities = {Book.class, Author.class, Category.class, BookAuthor.class, BookCategory.class, Tracking.class}, version = 3, exportSchema = false)
@TypeConverters(BitmapConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookDao BookDao();
    private static volatile  AppDatabase INSTANCE;
    public static synchronized  AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = create(context);
        }
        return INSTANCE;
    }
    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                "torrebookdb").fallbackToDestructiveMigration().build();
    }
    /*static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users "
                    + " ADD COLUMN last_update INTEGER");
        }
    };*/
}
