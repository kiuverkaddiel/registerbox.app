package kiu.business.registerboxapp.db.table;

import android.provider.BaseColumns;

public interface TicketTable {
    String TABLE_NAME = "ticket";

    interface Columns extends BaseColumns {
        String USER_NAME = "user_name";
        String PRODUCT_LIST = "product_list";
        String STATUS = "status";
        String INFO = "info";
        String CASH = "cash";
    }
}
