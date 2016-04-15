package com.shariq.torres.mydiary;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class DeletePhotoAlert extends DialogFragment implements DialogInterface.OnClickListener {

    private String selectedSrc;

    private OnFragmentInteractionListener mListener;

    public DeletePhotoAlert() {
    }


    public static DeletePhotoAlert newInstance(String src){
        Bundle args = new Bundle();
        args.putString("src", src);
        DeletePhotoAlert frag =  new DeletePhotoAlert();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        selectedSrc = getArguments().getString("src");
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_delete_photo_alert, null);
       return new AlertDialog.Builder(getActivity())
               .setTitle("Delete This Photo")
               .setPositiveButton("Delete Photo", this)
               .setView(v)
               .create();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String type);
    }

    public void onClick(DialogInterface dialog, int which){
        if(which == DialogInterface.BUTTON_POSITIVE){
            mListener.onFragmentInteraction(selectedSrc);
        }
    }
}
