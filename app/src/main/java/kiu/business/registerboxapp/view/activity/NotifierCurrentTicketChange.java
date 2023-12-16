package kiu.business.registerboxapp.view.activity;

import core.model.product.IProduct;
import ticket.model.Ticket;

public interface NotifierCurrentTicketChange {
    void ticketProductAdded(IProduct product, int position);
    void ticketProductRemove(IProduct product, int position);
    void notifyAllTicketChange();
    void ticketPutCash();
}
