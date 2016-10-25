package drucc.sittichok.heyheybread;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by sittichok on 27/4/2559.
 */
public class OrderUserAdapter extends BaseAdapter {

    private Context objContext;
    private String[] ordernumberString,orderdateString,priceorderString, statusorderString;

    public OrderUserAdapter(Context objContext, String[] ordernumberString,
                            String[] orderdateString, String[] priceorderString,
                            String[] statusorderString) {
        this.objContext = objContext;
        this.ordernumberString = ordernumberString;
        this.orderdateString = orderdateString;
        this.priceorderString = priceorderString;
        this.statusorderString = statusorderString;
    }   // Constructor

    @Override
    public int getCount() {
        return ordernumberString.length;
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
        View objView1 = objLayoutInflater.inflate(R.layout.my_userorder_listview, viewGroup, false);

        TextView numberOrder = (TextView) objView1.findViewById(R.id.textViewOrder1);
        numberOrder.setText(ordernumberString[i]);

        TextView dateOrder = (TextView) objView1.findViewById(R.id.textViewOrder2);
        dateOrder.setText(orderdateString[i]);

        TextView priceOrder = (TextView) objView1.findViewById(R.id.textViewOrder3);
        priceOrder.setText(priceorderString[i]);

        TextView statusOrder = (TextView) objView1.findViewById(R.id.textViewOrder4);
        statusOrder.setText(statusorderString[i]);




        return objView1;
    }
}   // Main class
