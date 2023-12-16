package kiu.business.registerboxapp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import core.model.product.IProduct;
import ips.model.Ips;
import kiu.business.registerboxapp.R;
import kiu.business.registerboxapp.view.holder.ProductListItemHolder;
import kiu.business.registerboxapp.view.listener.OnclickProductItem;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListItemHolder> {

    public static final int ENABLE = 1;
    public static final int DISABLE = 0;
    public static final int OWN_PRODUCT = 2;

    private final List<IProduct> products;

    private final FragmentManager fragmentManager;

    public ProductListAdapter(FragmentManager fragmentManager, List<IProduct> products) {
        this.products = products;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public int getItemViewType(int position) {
        IProduct item = products.get(position);
        int type = Ips.getInstance().getOriginProductList().getProducts().getOrDefault(item, 0) == 0 ?
                DISABLE : ENABLE;
        if (type == DISABLE)
            return type;
        return item.isOwnProduct() ? OWN_PRODUCT : ENABLE;
    }

    @NonNull
    @Override
    public ProductListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
        switch (viewType) {
            case DISABLE: {
                view.setBackgroundResource(R.drawable.item_disable);
            }
            break;
            case OWN_PRODUCT: {
                view.setBackgroundResource(R.drawable.item_own_product);
            }
            break;
        }

        return new ProductListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListItemHolder holder, int position) {
        IProduct item = products.get(position);
        int count = Ips.getInstance().getOriginProductList().getProducts().getOrDefault(item, 0);
        holder.fillHolder(new OnclickProductItem(fragmentManager, item, count, position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
