package kiu.business.registerboxapp.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import core.model.product.IProduct;
import ips.model.Ips;
import kiu.business.registerboxapp.R;
import product.model.Product;

public class EditProductListDialog extends DialogFragment {

    public static final String TAG = "edit dialog product";

    private final IProduct product;
    private final int productCount;
    private final int productListPosition;

    private int action = 1;

    private DialogNotifierProductChange notifierProductChange;

    public EditProductListDialog(IProduct product, int productCount, int productListPosition) {
        this.product = product;
        this.productCount = productCount;
        this.productListPosition = productListPosition;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = getLayoutInflater().inflate(R.layout.edit_productlist_dialog, null, false);

        Fragment parent = getParentFragment();
        if (parent instanceof DialogNotifierProductChange)
            notifierProductChange = (DialogNotifierProductChange) parent;

        assert view != null;
        EditText etProductId = view.findViewById(R.id.editTextPutProductId);
        EditText etProductName = view.findViewById(R.id.editTextProductName);
        EditText etProductPrice = view.findViewById(R.id.editTextTextProductPrice);
        EditText etProductCount = view.findViewById(R.id.editTextTextProductCount);
        EditText etProductNewCount = view.findViewById(R.id.editTextTextNewCount);
        CheckBox cbOwnProduct = view.findViewById(R.id.checkBoxOwnProduct);

        RadioButton rbAddProduct = view.findViewById(R.id.radioButtonAddProduct);
        RadioButton rbDelProduct = view.findViewById(R.id.radioButtonDelProduct);

        rbAddProduct.setOnClickListener(v -> {
            action = 1;
            etProductNewCount.setEnabled(true);
            etProductCount.setEnabled(false);
        });

        rbDelProduct.setOnClickListener(v -> {
            action = -1;
            etProductNewCount.setEnabled(true);
            etProductCount.setEnabled(false);
        });

        String title = getString(R.string.add_product_title);

        if (product != null) {

            title = getString(R.string.edit_product_title);

            etProductId.setText(product.getProductId());
            etProductId.setEnabled(false);
            etProductName.setText(product.getName());
            etProductPrice.setText(String.valueOf(product.getPrice()));
            etProductCount.setText(String.valueOf(productCount));
            cbOwnProduct.setChecked(product.isOwnProduct());
        }

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle(title)
                .setNegativeButton(R.string.cancel_text, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.ok_text, (dialog, which) -> {

                    String productId = etProductId.getText().toString();
                    String productName = etProductName.getText().toString();
                    String strPrice = etProductPrice.getText().toString();
                    float price = Float.parseFloat(strPrice);
                    boolean ownProduct = cbOwnProduct.isChecked();

                    String strCount = etProductCount.getText().toString();
                    if (strCount.isEmpty())
                        strCount = "0";
                    int count = Integer.parseInt(strCount);

                    String strNewCount = etProductNewCount.getText().toString();
                    if (strNewCount.isEmpty())
                        strNewCount = "0";
                    int newCount = Integer.parseInt(strNewCount) * action;

                    count += newCount;

                    IProduct aux = new Product(productName, productId, price, ownProduct, "");

                    if (Ips.getInstance().updateProduct(aux, count)) {

                        if (product != null) {
                            ((Product) product).update(aux);
                            if (notifierProductChange != null)
                                notifierProductChange.notifyProductChangeAt(productListPosition);
                        } else {
                            if (notifierProductChange != null)
                                notifierProductChange.notifyProductInsertedAt(aux, productListPosition);
                        }

                        Ips.getInstance().save();
                    }

                    dialog.dismiss();

                }).create();
    }
}
