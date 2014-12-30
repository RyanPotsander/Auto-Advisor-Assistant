package com.bearcubdev.autoadvisorassistant;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import static android.os.Debug.startMethodTracing;


public class ContactManager extends ActionBarActivity {
    private static final String TAG = "ContactManager";
    public ViewPager pager;
    FragmentManager fm;
    Context context;
    int updateTime;
    String statusBlue, statusYellow, statusRed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_manager);

        context = getApplicationContext();

        updateTime = 10; //change to make configurable by user todo

        statusBlue = "#ff0b4f80";
        statusYellow = "#fffbff35";
        statusRed = "#fe413c";

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(R.drawable.steering_wheel_white); //todo switch to nine patch
        setSupportActionBar(toolbar);

        fm = getSupportFragmentManager();
        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(fm));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return WorkOrderList.newInstance(position);
        }

        @Override
        public CharSequence getPageTitle (int position) {
            switch(position) {
                case 0:
                    return "Status Calls";
                case 1:
                    return "Estimate Hold";
                case 2:
                    return "Warranty Hold";
                case 3:
                    return "Deliveries";
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {
            return 4;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add_Button:
                showAddCustomerDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showAddCustomerDialog() {
        DialogFragment newDialog = new AddCustomerDialog();
        newDialog.show(fm, "new_customer");
    }

    public void moveRecordDialog(long id) {
        CharSequence options[] = new CharSequence[] {"Work in Progress", "Awaiting Estimate Approval",
                "Awaiting Extended Warranty Approval", "Work Complete, Awaiting Delivery", "Delete this Work Order"};

        final int rowId = (int) id;
        final AlertDialog.Builder builder = new AlertDialog.Builder(ContactManager.this);
        builder.setTitle("Choose an Action");
        builder.setItems(options, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        changeStatus(rowId, "statusCalls");
                        break;
                    case 1:
                        changeStatus(rowId, "estimates");
                        break;
                    case 2:
                        changeStatus(rowId, "extendedWarranty");
                        break;
                    case 3:
                        changeStatus(rowId, "readyForPickup");
                        break;
                    case 4:
                        confirmDeleteDialog(rowId);
                        break;
                }
            }
        });
        builder.show();

    }

    public void dismissRowDialog(final long id){
        final AlertDialog.Builder builder = new AlertDialog.Builder(ContactManager.this);
        builder.setTitle("Daily contact complete?")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dismissRow(id);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public void confirmDeleteDialog(final long id){
        AlertDialog.Builder builder = new AlertDialog.Builder(ContactManager.this);
        builder.setTitle("Delete Work Order?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        deleteRecord(id);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public void deleteRecord(long id) {
        Uri uri = ContentUris.withAppendedId(DataProvider.CONTENT_URI, id);;

        context.getContentResolver().delete(uri, null, null);

        Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
    }

    public void changeStatus(int id, String newStatus){
        Uri uri = ContentUris.withAppendedId(DataProvider.CONTENT_URI, id);

        ContentValues updateValues = new ContentValues();
        updateValues.put(DataProvider.C_STATUS, newStatus);

        context.getContentResolver().update(uri, updateValues, null, null);

        Toast.makeText(context, "Record moved", Toast.LENGTH_SHORT).show();
    }

    public void setStatusImage(long id, String color){
        Uri uri = ContentUris.withAppendedId(DataProvider.CONTENT_URI, id);

        ContentValues updateValues = new ContentValues();
        updateValues.put(DataProvider.C_COLOR, color);

        context.getContentResolver().update(uri, updateValues, null, null);
        Log.d(TAG, "color changed for id #" + id);
    }

    public void dismissRow(long id){
        Uri uri = ContentUris.withAppendedId(DataProvider.CONTENT_URI, id);

        ContentValues updateValues = new ContentValues();
        updateValues.put(DataProvider.C_DISMISSED_AT, System.currentTimeMillis());
        updateValues.put(DataProvider.C_STATUS, "dismissed");

        context.getContentResolver().update(uri, updateValues, null, null);

        Toast.makeText(context, "Repair Order Dismissed!", Toast.LENGTH_SHORT).show();
    }

    public void restoreItem(long id, String status){
        Uri uri = ContentUris.withAppendedId(DataProvider.CONTENT_URI, id);

        ContentValues updateValues = new ContentValues();
        updateValues.put(DataProvider.C_DISMISSED_AT, 0);
        updateValues.put(DataProvider.C_STATUS, status);

        context.getContentResolver().update(uri, updateValues, null, null);

        Toast.makeText(context, "Repair Order Restored!", Toast.LENGTH_SHORT).show();
    }

    public void updateDismissedItems(){
        Cursor cursor =  context.getContentResolver().query(DataProvider.CONTENT_URI, null,
                DataProvider.C_STATUS + "='dismissed'", null, null);

        while(cursor.moveToNext()) {
            Long elapsedMinutes = (System.currentTimeMillis() - cursor.getLong(cursor.getColumnIndex(DataProvider.C_DISMISSED_AT))) / (1000 * 60);
            if(elapsedMinutes > 1){
                restoreItem(cursor.getLong(cursor.getColumnIndex(DataProvider.C_ID)), cursor.getString(cursor.getColumnIndex(DataProvider.C_LAST_STATUS)));
            }
        }
    }

    public void updateStatusColors() {
        Cursor cursor = context.getContentResolver().query(DataProvider.CONTENT_URI, null,
                DataProvider.C_COLOR + "='" + statusBlue + "' OR " + DataProvider.C_COLOR + "='" + statusYellow + "'", null, null);

        while (cursor.moveToNext()) {
            long elapsedMinutes = ((System.currentTimeMillis()) - cursor.getLong(cursor.getColumnIndex(DataProvider.C_CREATED_AT))) / (1000 * 60);
            Log.d(TAG, "elapsedMinutes =" + elapsedMinutes);
            if (elapsedMinutes >= 1) {
                if (elapsedMinutes < 2) {
                    setStatusImage(cursor.getLong(cursor.getColumnIndex(DataProvider.C_ID)), statusYellow);
                } else {
                    setStatusImage(cursor.getLong(cursor.getColumnIndex(DataProvider.C_ID)), statusRed);
                }
            }
        }
    }

    public void refreshData(){
        updateDismissedItems();
        updateStatusColors();

        final Handler handler = new Handler();
        Runnable refreshRunnable = new Runnable() {
            @Override
            public void run() {
                updateDismissedItems();
                updateStatusColors();

                handler.postDelayed(this, 1000 * 60);
                Log.d(TAG, "data refreshed");
            }
        };
        handler.postDelayed(refreshRunnable, 1000 * 60);
    }
}
