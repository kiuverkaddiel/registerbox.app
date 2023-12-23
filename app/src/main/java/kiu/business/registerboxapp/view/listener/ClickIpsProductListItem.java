package kiu.business.registerboxapp.view.listener;

import android.content.Context;
import android.widget.Toast;

import core.model.product.IProduct;
import core.model.product.IProductList;
import ips.model.Ips;
import kiu.business.registerboxapp.view.activity.NotifierCurrentTicketChange;
import kiu.business.registerboxapp.view.activity.NotifierIpsProductListChange;
import ticket.controller.TicketManager;

public class ClickIpsProductListItem {
    final private IProduct product;
    final private int listPosition;

    private Context context;

    private final NotifierCurrentTicketChange notifierCurrentTicketChange;
    private final NotifierIpsProductListChange notifierIpsProductListChange;

    public ClickIpsProductListItem(IProduct product, int listPosition,
                                   NotifierCurrentTicketChange notifierCurrentTicketChange, NotifierIpsProductListChange notifierIpsProductListChange) {
        this.product = product;
        this.listPosition = listPosition;
        this.notifierCurrentTicketChange = notifierCurrentTicketChange;
        this.notifierIpsProductListChange = notifierIpsProductListChange;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void click() {
        IProductList productList = Ips.getInstance().getCurrentProductList();

        if (productList.getProducts().getOrDefault(product, 0) == 0) {
            if (context != null)
                // TODO extract text
                Toast.makeText(context, "Producto Insuficiente", Toast.LENGTH_LONG).show();
        } else {
            productList.removeProductFromList(product);
            TicketManager.getInstance().addProduct(product);

            if (notifierCurrentTicketChange != null)
                notifierCurrentTicketChange.ticketProductAdded(product, -1);

            if (notifierIpsProductListChange != null)
                notifierIpsProductListChange.ipsProductRemoved(product, listPosition);
        }
    }
}
