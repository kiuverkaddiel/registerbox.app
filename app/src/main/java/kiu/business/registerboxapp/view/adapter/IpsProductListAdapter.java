package kiu.business.registerboxapp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import core.model.product.IProduct;
import ips.model.Ips;
import kiu.business.registerboxapp.R;
import kiu.business.registerboxapp.databinding.ProductItemLayoutBinding;
import kiu.business.registerboxapp.view.activity.NotifierCurrentTicketChange;
import kiu.business.registerboxapp.view.activity.NotifierIpsProductListChange;
import kiu.business.registerboxapp.view.holder.IpsProductListItemHolder;
import kiu.business.registerboxapp.view.listener.OnclickIpsProductListItem;

public class IpsProductListAdapter extends RecyclerView.Adapter<IpsProductListItemHolder> {

    public static final int ENABLE = 1;
    public static final int DISABLE = 0;

    private final List<IProduct> products;

    private final NotifierIpsProductListChange notifierIpsProductListChange;
    private final NotifierCurrentTicketChange notifierCurrentTicketChange;

    public IpsProductListAdapter(NotifierIpsProductListChange notifierIpsProductListChange, NotifierCurrentTicketChange notifierCurrentTicketChange, List<IProduct> products) {
        this.notifierIpsProductListChange = notifierIpsProductListChange;
        this.notifierCurrentTicketChange = notifierCurrentTicketChange;
        this.products = products;
    }

    private int getProductCount(int position) {
        IProduct item = products.get(position);
        return Ips.getInstance().getCurrentProductList().getProducts().getOrDefault(item, 0);
    }

    @Override
    public int getItemViewType(int position) {
        return getProductCount(position) == 0 ? DISABLE : ENABLE;
    }

    @NonNull
    @Override
    public IpsProductListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
        if (viewType == DISABLE) {
            view.setBackgroundResource(R.drawable.item_disable);
            view.setEnabled(false);
        }
        return new IpsProductListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IpsProductListItemHolder holder, int position) {
        IProduct item = products.get(position);
        int count = getProductCount(position);
        holder.fillHolder(new OnclickIpsProductListItem(notifierCurrentTicketChange, notifierIpsProductListChange, item,
                count, position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
