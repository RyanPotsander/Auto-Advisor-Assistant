package com.bearcubdev.autoadvisorassistant;



import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


public class WorkOrderList extends Fragment implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "WorkOrderList";
    CustomAdapter adapter;

    public WorkOrderList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_3, container, false);

        ListView list = (ListView) v.findViewById(R.id.listView3);
        adapter = new CustomAdapter(getActivity().getApplicationContext(), null, 0);

        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);

        getLoaderManager().initLoader(0, null, this);

        return v;
    }

    public static WorkOrderList newInstance(int position) {

        WorkOrderList f = new WorkOrderList();

        Bundle args = new Bundle();
        args.putInt("position", position);

        f.setArguments(args);

        return f;
    }

    public int getPosition(){
        return getArguments().getInt("position");
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection;
        String statusColumn = DataProvider.C_STATUS;
        int position = getPosition();

        switch(position) {

            case 0:
                selection = statusColumn + "='statusCalls'";
                break;
            case 1:
                selection = statusColumn + "='estimates'";
                break;
            case 2:
                selection = statusColumn + "='extendedWarranty'";
                break;
            case 3:
                selection = statusColumn + "='readyForPickup'";
                break;
            default:
                throw new IllegalArgumentException("unknown position" + position);
        }

        return new android.support.v4.content.CursorLoader(getActivity(), DataProvider.CONTENT_URI, null, selection, null, DataProvider.C_CREATED_AT + " ASC");


    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ((ContactManager)getActivity()).moveRecordDialog(id);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = adapter.getCursor();
        String lastStatus = cursor.getString(cursor.getColumnIndex(DataProvider.C_STATUS));

        Uri uri = ContentUris.withAppendedId(DataProvider.CONTENT_URI, id);
        ContentValues updateValues = new ContentValues();
        updateValues.put(DataProvider.C_LAST_STATUS, lastStatus);

        getActivity().getContentResolver().update(uri, updateValues, null, null);

        ((ContactManager)getActivity()).dismissRowDialog(id);
    }

}
