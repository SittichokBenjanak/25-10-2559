package drucc.sittichok.heyheybread;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by mosza_000 on 19/1/2559.
 */
public class MyOrderAdapter extends BaseAdapter{
    // Explicit
    private Context objContext;
    private String[] noStrings, nameorderStrings , itemStrings ,
            priceStrings , sumpriceStrings;

    public MyOrderAdapter(Context objContext,
                          String[] noStrings,
                          String[] nameorderStrings,
                          String[] itemStrings,
                          String[] priceStrings,
                          String[] sumpriceStrings) {
        this.objContext = objContext;
        this.noStrings = noStrings;
        this.nameorderStrings = nameorderStrings;
        this.itemStrings = itemStrings;
        this.priceStrings = priceStrings;
        this.sumpriceStrings = sumpriceStrings;
    }   // Constructor

    @Override
    public int getCount() {
        return nameorderStrings.length;
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
        View objView1 = objLayoutInflater.inflate(R.layout.my_order_listview, viewGroup, false);

        TextView noTextView = (TextView) objView1.findViewById(R.id.textView24);
        noTextView.setText(noStrings[i]);

        TextView nameOrderTextView = (TextView) objView1.findViewById(R.id.textView25);
        nameOrderTextView.setText(nameorderStrings[i]);

        TextView itemTextView = (TextView) objView1.findViewById(R.id.textView26);
        itemTextView.setText(itemStrings[i]);

        TextView priceTextView = (TextView) objView1.findViewById(R.id.textView27);
        priceTextView.setText(priceStrings[i]);

        TextView sumpriceTextView = (TextView) objView1.findViewById(R.id.textView28);
        sumpriceTextView.setText(sumpriceStrings[i]);



        return objView1;
    }
}   // Main Class
