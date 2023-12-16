package kiu.business.registerboxapp.db.table;

import android.provider.BaseColumns;

public interface ProductListTable {
    String TABLE_NAME = "product_list";

    interface Columns extends BaseColumns {
        String PRODUCT_NAME = "product_name";
        String CATEGORIES = "category_list";
        String PRICE = "price";
        String OWN_PRODUCT = "own_product";
        String IMAGE_PATH = "image_path";
        String COUNT_PRODUCT = "count_product";
    }
}
