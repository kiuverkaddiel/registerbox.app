package kiu.business.registerboxapp.view.activity;

import core.model.product.IProduct;

public interface NotifierIpsProductListChange {
    void ipsProductRemoved(IProduct product, int position);
}
