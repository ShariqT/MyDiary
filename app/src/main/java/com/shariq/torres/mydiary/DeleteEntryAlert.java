package com.shariq.torres.mydiary;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Shariq on 4/19/2016.
 */
public class DeleteEntryAlert extends DialogFragment implements DialogInterface.OnClickListener {
    private int selectedId;

    private OnEntryInteractionListener mListener;

    public DeleteEntryAlert() {
    }


    public static DeleteEntryAlert newInstance(int id){
        Bundle args = new Bundle();
        args.putInt("id", id);
        DeleteEntryAlert frag =  new DeleteEntryAlert();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        selectedId = getArguments().getInt("id");
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_delete_entry_alert, null);
        return new AlertDialog.Builder(getActivity())
                .setTitle("Delete This Entry")
                .setPositiveButton("Delete Entry", this)
                .setView(v)
                .create();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEntryInteractionListener) {
            mListener = (OnEntryInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnEntryInteractionListener {
        void onEntryDialogInteraction(int id);
    }

    public void onClick(DialogInterface dialog, int which){
        if(which == DialogInterface.BUTTON_POSITIVE){
            mListener.onEntryDialogInteraction(selectedId);
        }
    }

}
