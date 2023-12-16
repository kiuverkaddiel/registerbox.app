package kiu.business.registerboxapp.view.listener;

import android.view.View;

import androidx.fragment.app.FragmentManager;

import core.model.product.IProduct;
import kiu.business.registerboxapp.view.dialog.PutCountProductInTicketDialog;

public class OnclickTicketProductListItem implements View.OnClickListener {

    private final FragmentManager fragmentManager;
    private final IProduct product;
    private final int position;
    private final int ticketProductCount;

    public OnclickTicketProductListItem(FragmentManager fragmentManager, IProduct product, int position, int ticketProductCount) {
        this.fragmentManager = fragmentManager;
        this.product = product;
        this.position = position;
        this.ticketProductCount = ticketProductCount;
    }

    public IProduct getProduct() {
        return product;
    }

    public int getProductCount() {
        return ticketProductCount;
    }

    @Override
    public void onClick(View v) {
        PutCountProductInTicketDialog putCountProductInTicketDialog = new PutCountProductInTicketDialog(product,
                position, ticketProductCount);
        putCountProductInTicketDialog.show(fragmentManager, PutCountProductInTicketDialog.TAG);
    }
}
