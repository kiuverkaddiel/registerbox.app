package kiu.business.registerboxapp.view.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import core.model.product.IProduct;
import ips.model.Ips;
import kiu.business.registerboxapp.R;
import kiu.business.registerboxapp.view.activity.NotifierCurrentTicketChange;
import ticket.controller.TicketManager;

public class PutCountProductInTicketDialog extends DialogFragment {

    public static final String TAG = "put count product in ticket dialog";

    private NotifierCurrentTicketChange notifierCurrentTicketChange;

    private final IProduct product;
    private final int position;
    private final int ticketProductCount;

    public PutCountProductInTicketDialog(IProduct product, int position, int ticketProductCount) {
        this.product = product;
        this.position = position;
        this.ticketProductCount = ticketProductCount;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = getLayoutInflater().inflate(R.layout.ticket_product_add_dialog, null, false);

        Fragment parent = getParentFragment();
        if (parent instanceof NotifierCurrentTicketChange)
            notifierCurrentTicketChange = (NotifierCurrentTicketChange) parent;

        TextView tvProductName = view.findViewById(R.id.textViewProductName);
        TextView tvProductPriceAndCount = view.findViewById(R.id.textViewProductPriceAndCount);
        TextView tvProductId = view.findViewById(R.id.textViewProductId);

        tvProductName.setText(product.getName());
        tvProductId.setText(product.getProductId());
        int ipsProductCount = Ips.getInstance().getCurrentProductList().getProducts().get(product);
        tvProductPriceAndCount.setText(product.getPrice() + "$x" + ipsProductCount);
        if (ipsProductCount == 0)
            view.findViewById(R.id.linearLayoutProductItem).setBackground(AppCompatResources.getDrawable(requireContext()
                    , R.drawable.item_disable));

        EditText etProductCount = view.findViewById(R.id.editTextCountProduct);
        etProductCount.setText(String.valueOf(ticketProductCount));

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setTitle(R.string.ticket_product_add)
                .setNegativeButton(R.string.cancel_text, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.ok_text, (dialog, which) -> {

                    String strNewCount = etProductCount.getText().toString();
                    if (strNewCount.isEmpty())
                        strNewCount = "0";
                    int newCount = Integer.parseInt(strNewCount);

                    if (newCount == ticketProductCount) {
                        dialog.dismiss();
                    } else {
                        int less = ipsProductCount + (ticketProductCount - newCount);

                        if (less < 0) {
                            // TODO extract the text
                            Toast.makeText(getContext(), "producto insuficieste", Toast.LENGTH_LONG).show();
                        } else {
                            TicketManager.getInstance().getCurrentTicket().updateProductCount(product, newCount);
                            Ips.getInstance().getCurrentProductList().updateProductCount(product, less);

                            if (notifierCurrentTicketChange != null) {
                                if (newCount == 0)
                                    notifierCurrentTicketChange.ticketProductRemove(product, position);
                                else
                                    notifierCurrentTicketChange.ticketProductAdded(product, position);
                            }

                        }

                    }
                }).create();
    }
}
