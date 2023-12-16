package kiu.business.registerboxapp.db;

import android.database.sqlite.SQLiteDatabase;

import kiu.business.registerboxapp.db.table.IpsTable;
import kiu.business.registerboxapp.db.table.ProductListTable;
import kiu.business.registerboxapp.db.table.SessionTable;
import kiu.business.registerboxapp.db.table.TicketTable;
import kiu.business.registerboxapp.db.table.UserTable;

public interface CreateTable {
    public final String TEXT_PRIMARY_KEY = " TEXT PRIMARY KEY ";
    public final String TEXT_NOT_NULL = " TEXT NOT NULL ";
    public final String INTEGER_NOT_NULL = " INTEGER NOT NULL ";
    public final String INTEGER_PRIMARY_KEY_AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT ";
    public final String INTEGER_PRIMARY_KEY = " INTEGER PRIMARY KEY AUTOINCREMENT ";
    public final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    public final String CREATE_TABLE = "CREATE TABLE ";

    default void createTableUser(SQLiteDatabase db) {

        String query = CREATE_TABLE + UserTable.TABLE_NAME +
                " (" +
                    UserTable.Columns._ID + TEXT_PRIMARY_KEY + ", " +
                    UserTable.Columns.FULL_NAME + TEXT_NOT_NULL + ", " +
                    UserTable.Columns.PASSWORD + TEXT_NOT_NULL + ", " +
                    UserTable.Columns.ROLE + TEXT_NOT_NULL + ", " +
                    UserTable.Columns.ENABLE + TEXT_NOT_NULL +
                ");";
        db.execSQL(query);
    }

    default void createTableSession(SQLiteDatabase db) {

        String query = CREATE_TABLE + SessionTable.TABLE_NAME +
                " (" +
                    SessionTable.Columns._ID + TEXT_PRIMARY_KEY +
                ");";
        db.execSQL(query);
    }

    default void createTableProductList(SQLiteDatabase db) {

        String query = CREATE_TABLE + ProductListTable.TABLE_NAME +
                " (" +
                    ProductListTable.Columns._ID + TEXT_PRIMARY_KEY + ", " +
                    ProductListTable.Columns.PRODUCT_NAME + TEXT_NOT_NULL + "," +
                    ProductListTable.Columns.CATEGORIES + TEXT_NOT_NULL + "," +
                    ProductListTable.Columns.PRICE + TEXT_NOT_NULL + "," +
                    ProductListTable.Columns.OWN_PRODUCT + TEXT_NOT_NULL + "," +
                    ProductListTable.Columns.IMAGE_PATH + TEXT_NOT_NULL + "," +
                    ProductListTable.Columns.COUNT_PRODUCT + INTEGER_NOT_NULL +
                ");";
        db.execSQL(query);
    }

    default void createTableIps(SQLiteDatabase db) {

        String query = CREATE_TABLE + IpsTable.TABLE_NAME +
                " (" +
                    IpsTable.Columns._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + ", " +
                    IpsTable.Columns.CASH + TEXT_NOT_NULL + ", " +
                    IpsTable.Columns.CREATED_BY + TEXT_NOT_NULL + ", " +
                    IpsTable.Columns.OPEN_BY + TEXT_NOT_NULL + ", " +
                    IpsTable.Columns.INFO + TEXT_NOT_NULL + ", " +
                    IpsTable.Columns.DATE_TIMESTAMP + TEXT_NOT_NULL +
                ");";
        db.execSQL(query);
    }

    default void createTableTicket(SQLiteDatabase db) {
        String query = CREATE_TABLE + TicketTable.TABLE_NAME +
                " (" +
                    TicketTable.Columns._ID + INTEGER_PRIMARY_KEY + ", " +
                    TicketTable.Columns.USER_NAME + TEXT_NOT_NULL + ", " +
                    TicketTable.Columns.PRODUCT_LIST + TEXT_NOT_NULL + ", " +
                    TicketTable.Columns.STATUS + TEXT_NOT_NULL + ", " +
                    TicketTable.Columns.INFO + TEXT_NOT_NULL + ", " +
                    TicketTable.Columns.CASH + TEXT_NOT_NULL +
                ");";
        db.execSQL(query);
    }
}
