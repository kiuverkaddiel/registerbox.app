package kiu.business.registerboxapp.view.dialog;

import core.model.product.IProduct;

public interface DialogNotifierProductChange {
    void notifyProductChangeAt(int pos);
    void notifyProductInsertedAt(IProduct product, int pos);
}
