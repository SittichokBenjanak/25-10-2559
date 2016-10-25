package drucc.sittichok.heyheybread;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by sittichok on 29/4/2559.
 */
public class DetailAdapter extends BaseAdapter {
    private Context objContext;
    private String[] OrderdetailStrings,ProductdetailStrings,AmountdetailStrings,PricedetailStrings,SumpricedetailStrings;

    public DetailAdapter(Context objContext, String[] orderdetailStrings,
                         String[] productdetailStrings, String[] amountdetailStrings,
                         String[] pricedetailStrings, String[] sumpricedetailStrings) {
        this.objContext = objContext;
        OrderdetailStrings = orderdetailStrings;
        ProductdetailStrings = productdetailStrings;
        AmountdetailStrings = amountdetailStrings;
        PricedetailStrings = pricedetailStrings;
        SumpricedetailStrings = sumpricedetailStrings;
    }

    @Override
    public int getCount() {
        return OrderdetailStrings.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater objLayoutInflater = (LayoutInflater) objContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View objView1 = objLayoutInflater.inflate(R.layout.my_detailorder_listview, viewGroup, false);

        TextView OrderdetailTextView = (TextView) objView1.findViewById(R.id.detailListview1);
        OrderdetailTextView.setText(OrderdetailStrings[i]);

        TextView ProductTextView = (TextView) objView1.findViewById(R.id.detailListview2);
        ProductTextView.setText(ProductdetailStrings[i]);

        TextView AmountTextView = (TextView) objView1.findViewById(R.id.detailListview3);
        AmountTextView.setText(AmountdetailStrings[i]);

        TextView PriceTextView = (TextView) objView1.findViewById(R.id.detailListview4);
        PriceTextView.setText(PricedetailStrings[i]);

        TextView SumpriceTextView = (TextView) objView1.findViewById(R.id.detailListview5);
        SumpriceTextView.setText(SumpricedetailStrings[i]);




        return objView1;
    }
}
