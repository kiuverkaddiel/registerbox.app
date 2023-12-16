package kiu.business.registerboxapp.db.table;

import android.provider.BaseColumns;

public interface IpsTable {
    String TABLE_NAME = "ips";

    interface Columns extends BaseColumns {
        String CASH = "cash";
        String CREATED_BY = "created_by";
        String OPEN_BY = "open_by";
        String INFO = "info";
        String DATE_TIMESTAMP = "date_timestamp";
    }
}
