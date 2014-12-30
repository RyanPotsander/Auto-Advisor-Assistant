package com.bearcubdev.autoadvisorassistant;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Home on 11/22/2014.
 */
public class CustomAdapter extends CursorAdapter {
    private static final String TAG = "CustomAdapter";
    Context context;

    public CustomAdapter(Context context, Cursor c, int flags) {
        super(context, c , flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        //inflate list item resource
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = inflater.inflate(R.layout.list_item, null, false);

        //instantiate holder object for views and set as tag for list item
        ViewHolder holder = new ViewHolder();
        holder.textView = (TextView) listItem.findViewById(R.id.textView);
        holder.textView2 = (TextView) listItem.findViewById(R.id.textView2);
        holder.textView3 = (TextView) listItem.findViewById(R.id.textView3);
        holder.imageView = (ImageView) listItem.findViewById(R.id.imageView2);
        listItem.setTag(holder);

        return listItem;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //retrieve holder object from listItem
        ViewHolder holder = (ViewHolder) view.getTag();

        //retrieve data to display
        String name = cursor.getString(cursor.getColumnIndex(DataProvider.C_NAME));
        String roNumber = cursor.getString(cursor.getColumnIndex(DataProvider.C_RO_NUMBER));
        String phoneNumber = cursor.getString(cursor.getColumnIndex(DataProvider.C_PHONE_NUMBER));
        String formattedNumber = PhoneNumberUtils.formatNumber(phoneNumber);
        String colorCode = cursor.getString(cursor.getColumnIndex(DataProvider.C_COLOR));
        int color = Color.parseColor(colorCode);

        //bind the data to...
        holder.textView.setText(name);
        holder.textView2.setText(roNumber);
        holder.textView3.setText(formattedNumber);
        holder.imageView.setBackgroundColor(color);
    }

    static class ViewHolder {
        public TextView textView, textView2, textView3;
        public ImageView imageView;
    }
}

