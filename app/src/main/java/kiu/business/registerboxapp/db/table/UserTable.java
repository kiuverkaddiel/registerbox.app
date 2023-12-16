package kiu.business.registerboxapp.db.table;

import android.provider.BaseColumns;

public interface UserTable {
    String TABLE_NAME = "user";

    interface Columns extends BaseColumns {
        String FULL_NAME = "full_name";
        String PASSWORD = "password";
        String ROLE = "role";
        String ENABLE = "enable";
    }
}
