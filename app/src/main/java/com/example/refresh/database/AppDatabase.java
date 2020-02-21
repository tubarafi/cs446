package com.example.refresh.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.refresh.database.dao.ShopItemDAO;
import com.example.refresh.database.dao.FoodItemDAO;
import com.example.refresh.database.model.ShopItem;
import com.example.refresh.database.model.FoodItem;

@Database(entities = {ShopItem.class, FoodItem.class},version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static  AppDatabase INSTANCE;

    public abstract ShopItemDAO shopItemDAO();
    public abstract FoodItemDAO foodItemDAO();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "refresh-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void cleanUp() {
        INSTANCE = null;
    }
}

