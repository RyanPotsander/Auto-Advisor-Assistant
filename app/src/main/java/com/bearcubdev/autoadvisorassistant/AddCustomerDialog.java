package com.bearcubdev.autoadvisorassistant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static android.app.PendingIntent.getActivity;

/**
 * Created by Home on 9/6/2014.
 */
public class AddCustomerDialog extends DialogFragment {

    private static final String TAG = "workOrderList";
    String cxName;
    String cxPhone;
    String roNumber;
    EditText etName;
    EditText etPhone;
    EditText etRO;

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //get layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //inflate and set layout for dialog
        final View view = inflater.inflate(R.layout.input_ticket, null);
        builder.setView(view);
        builder.setTitle("Add a New Customer");

        //get text fields
        etName = (EditText) view.findViewById(R.id.edit_Text2);
        etPhone = (EditText) view.findViewById(R.id.edit_Text3);
        etRO = (EditText) view.findViewById(R.id.edit_Text);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       ContentValues values;

                       Long time = System.currentTimeMillis();

                       cxName = etName.getText().toString();
                       cxPhone = etPhone.getText().toString();
                       roNumber = etRO.getText().toString();

                       String status = "statusCalls";
                       String color = ((ContactManager) getActivity()).statusBlue;
                       long dismissedAt = 0;

                       values = new ContentValues();

                       values.put(DataProvider.C_NAME, cxName);
                       values.put(DataProvider.C_PHONE_NUMBER, cxPhone);
                       values.put(DataProvider.C_RO_NUMBER, roNumber);
                       values.put(DataProvider.C_STATUS, status);
                       values.put(DataProvider.C_CREATED_AT, time);
                       values.put(DataProvider.C_DISMISSED_AT, dismissedAt);
                       values.put(DataProvider.C_COLOR, color);


                       ((ContactManager)getActivity()).pager.setCurrentItem(0, true);

                       Uri newUri = getActivity().getContentResolver().insert(DataProvider.CONTENT_URI, values);

                       long id = ContentUris.parseId(newUri);
                       Log.d(TAG, "id is " + id);
                   }
               })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

        return builder.create();
    }
}
