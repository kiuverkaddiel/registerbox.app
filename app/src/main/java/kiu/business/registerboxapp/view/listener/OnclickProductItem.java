package kiu.business.registerboxapp.view.listener;

import android.view.View;

import androidx.fragment.app.FragmentManager;

import core.model.product.IProduct;
import kiu.business.registerboxapp.view.dialog.EditProductListDialog;

public class OnclickProductItem implements View.OnClickListener {
    private final FragmentManager fragmentManager;
    private final IProduct product;
    private final int count;
    private final int positionProductList;

    public OnclickProductItem(FragmentManager fragmentManager, IProduct product,
                              int count, int positionProductList) {
        this.fragmentManager = fragmentManager;
        this.product = product;
        this.count = count;
        this.positionProductList = positionProductList;
    }

    public IProduct getProduct() {
        return product;
    }

    public int getCount() {
        return count;
    }

    public int getPositionProductList() {
        return positionProductList;
    }

    @Override
    public void onClick(View v) {
        EditProductListDialog p = new EditProductListDialog(product, count, positionProductList);
        p.show(fragmentManager, EditProductListDialog.TAG);
    }
}
