package kiu.business.registerboxapp.view.listener;

import android.view.View;

import core.model.product.IProduct;
import ips.model.Ips;
import kiu.business.registerboxapp.view.activity.NotifierCurrentTicketChange;
import kiu.business.registerboxapp.view.activity.NotifierIpsProductListChange;
import ticket.controller.TicketManager;

public class OnclickIpsProductListItem implements View.OnClickListener {

    private final IProduct product;
    private final int listPosition;
    private final int productCount;

    private final NotifierCurrentTicketChange notifierCurrentTicketChange;
    private final NotifierIpsProductListChange notifierIpsProductListChange;

    public OnclickIpsProductListItem(NotifierCurrentTicketChange notifierCurrentTicketChange, NotifierIpsProductListChange notifierIpsProductListChange, IProduct product, int productCount, int listPosition) {
        this.notifierCurrentTicketChange = notifierCurrentTicketChange;
        this.notifierIpsProductListChange = notifierIpsProductListChange;
        this.product = product;
        this.productCount = productCount;
        this.listPosition = listPosition;
    }

    public IProduct getProduct() {
        return product;
    }

    public int getProductCount() {
        return productCount;
    }

    @Override
    public void onClick(View v) {

        Ips.getInstance().getCurrentProductList().removeProductFromList(product);
        TicketManager.getInstance().addProduct(product);

        if (notifierCurrentTicketChange != null)
            notifierCurrentTicketChange.ticketProductAdded(product, -1);

        if (notifierIpsProductListChange != null)
            notifierIpsProductListChange.ipsProductRemoved(product, listPosition);
    }
}
