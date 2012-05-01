package com.jaeckel.amenoid;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author biafra
 * @date 5/1/12 5:16 PM
 */
public class Fragment2 extends Fragment {

  private final static String TAG = "Fragment2";


  @Override
  public void onStart() {
    super.onStart();
    //---Button view---
    Button btnGetText = (Button) getActivity().findViewById(R.id.btnGetText1);

    btnGetText.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        TextView lbl = (TextView) getActivity().findViewById(R.id.labelFragment1);
        Toast.makeText(getActivity(), lbl.getText(), Toast.LENGTH_SHORT).show();
      }
    });
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment2, container, false);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Log.d(TAG, "onAttach. activity: " + activity);

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Log.d(TAG, "onActivityCreated");
  }

//  @Override
//  public void onStart() {
//    super.onStart();
//    Log.d(TAG, "onStart");
//  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.d(TAG, "onPause");
  }

  @Override
  public void onStop() {
    super.onStop();
    Log.d(TAG, "onStop");

  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    Log.d(TAG, "onDestroyView");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy");
  }

  @Override
  public void onDetach() {
    super.onDetach();
    Log.d(TAG, "onDetach");
  }
}
