package pt.apps.leandro.portfolio.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pt.apps.leandro.portfolio.Model.Product;
import pt.apps.leandro.portfolio.R;


public class ProductsAdapter extends BaseAdapter {


    private Context context;
    private List<Product> mProducts;
    private LayoutInflater mInflater;
    private String profitString;
    private float profit;

    public ProductsAdapter(Context context, List<Product> products) {
        this.context = context;
        mProducts = products;
        mInflater = LayoutInflater.from(context);
    }

    public void addProduct(Product product) {
        mProducts.add(product);
        notifyDataSetChanged();
    }

    public void updateProduct(List<Product> products) {
        this.mProducts = products;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mProducts.size();
    }
    @Override
    public Product getItem(int position) {
        return mProducts.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_product, parent, false);
        }

        Product product = getItem(position);

        TextView nameProduct = (TextView) convertView.findViewById(R.id.tv_item_product_name);
        nameProduct.setText(product.getName());

        TextView priceProductBuy = (TextView) convertView.findViewById(R.id.tv_item_product_price_buy);
        priceProductBuy.setText(product.getPrice_buy() + " €");


        TextView priceProductSell = (TextView) convertView.findViewById(R.id.tv_item_product_price_sell);
        priceProductSell.setText(product.getPrice_sell() + " €");


        String ConvertPriceSell = product.getPrice_sell().toString();
        float SellPrice = Float.parseFloat(ConvertPriceSell);

        String ConvertPriceBuy = product.getPrice_buy().toString();
        float BuyPrice = Float.parseFloat(ConvertPriceBuy);

        profit = SellPrice - BuyPrice;


        profitString = String.format("%.2f", profit);




        TextView priceProductProfit = (TextView) convertView.findViewById(R.id.tv_item_product_profit);
        priceProductProfit.setText(profitString + " €");

       /* SharedPreferences settings = context.getSharedPreferences("settings", 0);
        if (settings.getBoolean("load_remote_images", true)) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_item_person_image);
            Picasso.with(context)
                    .load(person.getPhoto())
                    .placeholder(R.drawable.avatar)
                    .into(imageView);
        }*/

        return convertView;
    }

}
