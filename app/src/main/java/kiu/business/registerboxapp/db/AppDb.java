package kiu.business.registerboxapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import core.controller.UserManager;
import core.model.Role;
import core.model.Session;
import core.model.TicketStatus;
import core.model.User;
import core.model.product.IProduct;
import core.model.product.IProductList;
import core.model.ticket.ITicket;
import core.storage.ProductListStorage;
import core.storage.SessionStorage;
import core.storage.UserStorage;
import ips.model.Ips;
import ips.storage.IpsStorage;
import kiu.business.registerboxapp.db.table.IpsTable;
import kiu.business.registerboxapp.db.table.ProductListTable;
import kiu.business.registerboxapp.db.table.SessionTable;
import kiu.business.registerboxapp.db.table.TicketTable;
import kiu.business.registerboxapp.db.table.UserTable;
import product.model.Product;
import product.model.ProductList;
import ticket.model.Ticket;
import ticket.storage.TicketStorage;

public class AppDb extends SQLiteOpenHelper implements UserStorage,
        SessionStorage, IpsStorage, TicketStorage, ProductListStorage, CreateTable {

    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 1;

    public AppDb(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        createTableUser(db);
        try {
            insertUser(
                    new User(
                            "alfre",
                            "Alfredo del Valle",
                            Role.ADMIN,
                            true,
                            User.MD5Encrypt("alfre")
                    ),
                    db
            );
            insertUser(
                    new User(
                            "cajero",
                            "Cajero",
                            Role.EMPLOYEE,
                            true,
                            User.MD5Encrypt("cajero")
                    ),
                    db
            );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        createTableSession(db);
        createTableProductList(db);
        createTableIps(db);
        insertEmptyIps(db);

        createTableTicket(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void insertEmptyIps(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(IpsTable.Columns.CASH, "0.0");
        cv.put(IpsTable.Columns.CREATED_BY, "admin");
        cv.put(IpsTable.Columns.OPEN_BY, "");
        cv.put(IpsTable.Columns.INFO, "");
        cv.put(IpsTable.Columns.DATE_TIMESTAMP, String.valueOf(System.currentTimeMillis()));

        db.insert(IpsTable.TABLE_NAME, null, cv);
    }

    @Override
    public HashMap<String, User> getAllUsers() {
        SQLiteDatabase db = getReadableDatabase();

        String[] select = {
                UserTable.Columns._ID,
                UserTable.Columns.FULL_NAME,
                UserTable.Columns.PASSWORD,
                UserTable.Columns.ROLE,
                UserTable.Columns.ENABLE,
        };

        Cursor cursor = db.query(UserTable.TABLE_NAME, select, null, null, null, null, null);

        HashMap<String, User> users = new HashMap<>();

        if (cursor == null || cursor.getCount() == 0) {
            db.close();
            return users;
        }

        while (cursor.moveToNext()) {
            String userName = cursor.getString(0);
            String userFullName = cursor.getString(1);
            String userPassword = cursor.getString(2);
            String userRole = cursor.getString(3);
            String userEnable = cursor.getString(4);

            try {
                User aux = new User(userName, userFullName, Role.valueOf(userRole), Boolean.parseBoolean(userEnable),
                        userPassword);
                users.put(aux.getUserName(), aux);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        db.close();

        return users;
    }

    @Override
    public HashMap<String, User> getAllEnableUsers() {
        return null;
    }

    @Override
    public boolean save(User user) {
        if (exitsUser(user))
            return updateUser(user);
        return insertUser(user);
    }

    private boolean exitsUser(User user) {
        SQLiteDatabase db = getReadableDatabase();

        String[] select = {"*"};
        String where = UserTable.Columns._ID + " = ?";

        Cursor cursor = db.query(UserTable.TABLE_NAME, select, where, new String[]{user.getUserName()}, null,
                null, null);

        if (cursor == null || cursor.getCount() == 0) {
            db.close();
            return false;
        }

        cursor.close();
        db.close();

        return true;
    }

    private boolean updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(UserTable.Columns.FULL_NAME, user.getFullName());
        cv.put(UserTable.Columns.PASSWORD, user.getPassword());
        cv.put(UserTable.Columns.ROLE, user.getRole().toString());
        cv.put(UserTable.Columns.ENABLE, String.valueOf(user.isEnable()));

        boolean inserted = db.update(UserTable.TABLE_NAME, cv, UserTable.Columns._ID + " = ?",
                new String[]{user.getUserName()}) > 0;

        db.close();

        return inserted;
    }

    private boolean insertUser(User user, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(UserTable.Columns._ID, user.getUserName());
        cv.put(UserTable.Columns.FULL_NAME, user.getFullName());
        cv.put(UserTable.Columns.PASSWORD, user.getPassword());
        cv.put(UserTable.Columns.ROLE, user.getRole().toString());
        cv.put(UserTable.Columns.ENABLE, String.valueOf(user.isEnable()));

        return db.insert(UserTable.TABLE_NAME, null, cv) > 0;
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        boolean inserted = insertUser(user, db);
        db.close();

        return inserted;
    }

    @Override
    public boolean delete(User user) {
        SQLiteDatabase db = getWritableDatabase();

        boolean deleted = db.delete(
                UserTable.TABLE_NAME,
                UserTable.Columns._ID + " = ?",
                new String[]{user.getUserName()}
        ) > 0;

        db.close();

        return deleted;
    }

    @Override
    public String getSessionUserName() {
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {SessionTable.Columns._ID};

        Cursor cursor = db.query(UserTable.TABLE_NAME, columns, null, null, null,
                null, null);

        if (cursor == null || cursor.getCount() == 0) {
            db.close();
            return null;
        }

        cursor.moveToFirst();
        String userName = cursor.getString(0);

        cursor.close();
        db.close();

        return userName;
    }

    @Override
    public void openSession(User user) {
        if (user != null) {
            SQLiteDatabase db = getWritableDatabase();
            openSession(user, db);
            db.close();
        }
    }

    public void openSession(User user, SQLiteDatabase db) {
        if (user != null) {
            ContentValues cv = new ContentValues();
            cv.put(SessionTable.Columns._ID, user.getUserName());

            db.update(SessionTable.TABLE_NAME, cv, null, null);
        }
    }

    @Override
    public void closeSession() {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(SessionTable.Columns._ID, "");

        db.update(SessionTable.TABLE_NAME, cv, null, null);
    }

    @Override
    public void load() {

        SQLiteDatabase db = getReadableDatabase();

        String[] ipsColumns = {
                IpsTable.Columns.CASH,
                IpsTable.Columns.CREATED_BY,
                IpsTable.Columns.OPEN_BY,
                IpsTable.Columns.INFO,
                IpsTable.Columns.DATE_TIMESTAMP
        };

        Cursor ipsCursor = db.query(IpsTable.TABLE_NAME, ipsColumns, null, null, null, null, null);

        ipsCursor.moveToFirst();

        float cash = ipsCursor.getFloat(0);
        String createdByUserName = ipsCursor.getString(1);
        String openedByUserName = ipsCursor.getString(2);
        String info = ipsCursor.getString(3);
        long timestamp = ipsCursor.getLong(4);

        User createdBy = UserManager.getInstance().getUsers().get(createdByUserName);
        User openedBy = UserManager.getInstance().getUsers().get(openedByUserName);

        ipsCursor.close();
        db.close();

        Ips.getInstance().init(
                cash,
                createdBy,
                openedBy,
                info,
                new Date(timestamp)
        );
    }

    @Override
    public boolean saveIps() {

        SQLiteDatabase db = getWritableDatabase();
        Ips ips = Ips.getInstance();

        ips.setDate(Date.from(Instant.now()));
        ips.setCreatedBy(Session.getInstance().getUser());

        ContentValues cv = new ContentValues();
        cv.put(IpsTable.Columns.DATE_TIMESTAMP, ips.getDate().getTime());
        cv.put(IpsTable.Columns.INFO, ips.getInfo());
        cv.put(IpsTable.Columns.CASH, ips.getCash());
        cv.put(IpsTable.Columns.OPEN_BY, ips.getOpenBy().getUserName());
        cv.put(IpsTable.Columns.CREATED_BY, ips.getCreatedBy().getUserName());

        boolean saved = db.update(IpsTable.TABLE_NAME, cv, null, null) > 0;

        db.close();

        return saved;
    }

    private List<ITicket> getTickets(String select, String[] selectArg) {
        List<ITicket> tickets = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {
                TicketTable.Columns._ID,
                TicketTable.Columns.PRODUCT_LIST,
                TicketTable.Columns.USER_NAME,
                TicketTable.Columns.STATUS,
                TicketTable.Columns.INFO,
                TicketTable.Columns.CASH
        };

        Cursor cursor = db.query(TicketTable.TABLE_NAME, columns, select, selectArg,
                null, null, TicketTable.Columns._ID + " DESC");

        if (cursor == null) {
            db.close();
            return tickets;
        }

        while (cursor.moveToNext()) {
            Date date = new Date(cursor.getLong(0));
            String productListString = cursor.getString(1);
            IProductList productList = Ips.getInstance().getOriginProductList().reverseString(productListString);
            String userName = cursor.getString(2);
            User user = UserManager.getInstance().getUsers().get(userName);
            TicketStatus status = TicketStatus.valueOf(cursor.getString(3));
            String info = cursor.getString(4);
            float cash = cursor.getFloat(5);

            Ticket ticket = new Ticket(date, user, productList);
            ticket.setCash(cash);
            ticket.setInfo(info);
            ticket.setStatus(status);

            tickets.add(ticket);
        }

        cursor.close();
        db.close();

        return tickets;
    }

    @Override
    public List<ITicket> getAllTickets() {
        return getTickets(null, null);
    }

    @Override
    public List<ITicket> getAllTicketsByUserName(String userName) {
        String select = (TicketTable.Columns.USER_NAME + " = ?");
        String[] selectArg = {userName};

        return getTickets(select, selectArg);
    }

    @Override
    public List<ITicket> getAllOpenTicketsByUserName(String userName) {
        String select = (
                TicketTable.Columns.USER_NAME + " = ? and " +
                        TicketTable.Columns.STATUS + " = ?"
        );
        String[] selectArg = {userName, TicketStatus.OPEN.toString()};

        return getTickets(select, selectArg);
    }

    @Override
    public List<ITicket> getAllOpenTickets() {
        String select = (TicketTable.Columns.STATUS + " = ?");
        String[] selectArg = {TicketStatus.OPEN.toString()};

        return getTickets(select, selectArg);
    }

    private boolean existTicket(ITicket ticket, SQLiteDatabase db) {
        Cursor cursor = db.query(TicketTable.TABLE_NAME, new String[]{"*"}, TicketTable.Columns._ID + " = ?",
                new String[]{String.valueOf(ticket.getCreatedDate().getTime())}, null, null, null);

        if (cursor == null) {
            return false;
        }

        boolean found = cursor.getCount() > 0;

        cursor.close();

        return found;
    }

    private ContentValues getTicketContentValue(ITicket ticket) {
        ContentValues cv = new ContentValues();
        cv.put(TicketTable.Columns.CASH, String.valueOf(ticket.getCash()));
        cv.put(TicketTable.Columns.PRODUCT_LIST, ticket.getProductList().convertToString());
        cv.put(TicketTable.Columns.USER_NAME, ticket.getUser().getUserName());
        cv.put(TicketTable.Columns.INFO, ticket.getInfo());
        cv.put(TicketTable.Columns.STATUS, ticket.getStatus().toString());

        return cv;
    }

    private boolean insertTicket(ITicket ticket, SQLiteDatabase db) {
        ContentValues cv = getTicketContentValue(ticket);
        cv.put(TicketTable.Columns._ID, ticket.getCreatedDate().getTime());
        return  db.insert(TicketTable.TABLE_NAME, null, cv) > 0;
    }

    private boolean updateTicket(ITicket ticket, SQLiteDatabase db) {
        ContentValues cv = getTicketContentValue(ticket);
        return db.update(TicketTable.TABLE_NAME, cv, TicketTable.Columns._ID + " = ?",
                new String[]{String.valueOf(ticket.getCreatedDate().getTime())}) > 0;
    }

    @Override
    public boolean save(ITicket ticket) {
        SQLiteDatabase db = getWritableDatabase();

        boolean saved;

        if (existTicket(ticket, db)) {
            saved = updateTicket(ticket, db);
        } else {
            saved = insertTicket(ticket, db);
        }

        db.close();

        return saved;
    }

    @Override
    public boolean delete(ITicket ticket) {
        SQLiteDatabase db = getWritableDatabase();

        boolean deleted = db.delete(TicketTable.TABLE_NAME, TicketTable.Columns._ID + " = ?",
                new String[]{String.valueOf(ticket.getCreatedDate().getTime())}) > 0;

        db.close();

        return deleted;
    }

    @Override
    public IProductList getProductList() {
        SQLiteDatabase db = getReadableDatabase();
        ProductList productList = new ProductList();

        String[] columns = {
                ProductListTable.Columns._ID,
                ProductListTable.Columns.PRODUCT_NAME,
                ProductListTable.Columns.PRICE,
                ProductListTable.Columns.OWN_PRODUCT,
                ProductListTable.Columns.COUNT_PRODUCT
        };

        Cursor cursor = db.query(ProductListTable.TABLE_NAME, columns,
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            String productId = cursor.getString(0);
            String productName = cursor.getString(1);
            float price = cursor.getFloat(2);
            boolean ownProduct = Boolean.parseBoolean(cursor.getString(3));
            int count = cursor.getInt(4);

            Product product = new Product(productName, productId, price, ownProduct, "");
            productList.updateProductCount(product, count);
        }

        cursor.close();
        db.close();

        return productList;
    }

    @Override
    public boolean saveProduct(IProduct product, int count) {
        SQLiteDatabase db = getWritableDatabase();

        boolean saved = saveProduct(product, count, db);

        db.close();

        return saved;
    }

    public boolean saveProduct(IProduct product, int count, SQLiteDatabase db) {
        boolean saved;

        if (existProduct(product, db)) {
            saved = updateProduct(product, count, db);
        } else {
            saved = insertProduct(product, count, db);
        }

        return saved;
    }

    @Override
    public boolean saveProductList(IProductList productList) {
        SQLiteDatabase db = getWritableDatabase();

        HashMap<IProduct, Integer> products = productList.getProducts();
        for (IProduct product : products.keySet()) {
            if (!saveProduct(product, products.getOrDefault(product, 0), db)) {
                db.close();
                return false;
            }
        }

        return true;
    }

    private boolean existProduct(IProduct product, SQLiteDatabase db) {
        Cursor cursor = db.query(ProductListTable.TABLE_NAME, new String[]{"*"}, ProductListTable.Columns._ID + " = ?",
                new String[]{product.getProductId()}, null, null, null);

        if (cursor == null) {
            return false;
        }

        boolean found = cursor.getCount() > 0;

        cursor.close();

        return found;
    }

    private ContentValues getProductContentValue(IProduct product) {
        ContentValues cv = new ContentValues();
        cv.put(ProductListTable.Columns.IMAGE_PATH, "");
        cv.put(ProductListTable.Columns.CATEGORIES, "");
        cv.put(ProductListTable.Columns.PRODUCT_NAME, product.getName());
        cv.put(ProductListTable.Columns.PRICE, product.getPrice());
        cv.put(ProductListTable.Columns.OWN_PRODUCT, String.valueOf(product.isOwnProduct()));

        return cv;
    }

    private boolean insertProduct(IProduct product, int count, SQLiteDatabase db) {
        ContentValues cv = getProductContentValue(product);
        cv.put(ProductListTable.Columns._ID, product.getProductId());
        cv.put(ProductListTable.Columns.COUNT_PRODUCT, count);

        return db.insert(ProductListTable.TABLE_NAME, null, cv) > 0;
    }

    private boolean updateProduct(IProduct product, int count, SQLiteDatabase db) {
        ContentValues cv = getProductContentValue(product);
        cv.put(ProductListTable.Columns.COUNT_PRODUCT, count);

        return db.update(ProductListTable.TABLE_NAME, cv, ProductListTable.Columns._ID + " = ?",
                new String[]{product.getProductId()}) > 0;
    }

    @Override
    public boolean deleteProduct(IProduct iProduct) {
        SQLiteDatabase db = getWritableDatabase();

        String where = ProductListTable.Columns._ID + " = ?";
        String[] whereArg = {iProduct.getProductId()};

        boolean deleted = db.delete(ProductListTable.TABLE_NAME, where, whereArg) > 0;

        db.close();

        return deleted;
    }
}
