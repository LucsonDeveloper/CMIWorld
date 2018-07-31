package com.lucsoninfotech.cmi.cmiworld.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.lucsoninfotech.cmi.cmiworld.R;

public class QueAnsFragment extends Fragment {

    Activity mActivity;
    private int pos;
    private TextView txt_no;
    private TextView txt_que;
    private EditText edt_ans;

    public QueAnsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_que_ans, container, false);
        txt_no = view.findViewById(R.id.txt_no);
        txt_que = view.findViewById(R.id.txt_question);
        edt_ans = view.findViewById(R.id.edt_ans);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            pos = bundle.getInt("id");
            System.out.println("position " + pos);
        }

        switch (pos) {

            case 0:
                txt_no.setText(pos + 1 + " )");
                txt_que.setText("Have you ever done any social services? if yes then what have you done ? if no then why not?");
                break;
            case 1:
                txt_no.setText(pos + 1 + " )");
                txt_que.setText("Why do you think social services are necessary?");
                break;
            case 2:
                txt_no.setText(pos + 1 + " )");
                txt_que.setText("What types/type of social services you want to do?");
                break;
            case 3:
                txt_no.setText(pos + 1 + " )");
                txt_que.setText("if you are given a chance to change one aspect of society , what would you wannt to change ? and why?");
                break;
            case 4:
                txt_no.setText(pos + 1 + " )");
                txt_que.setText("what you want to achieve via our app?");
                break;

        }


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            OnGetFromUserClickListener mListener = (OnGetFromUserClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnGetFromUserClickListener");
        }
    }

    public String getFromUser() {

        return edt_ans.getText().toString();
    }

    public interface OnGetFromUserClickListener {
        String getFromUser(String message);


    }
}
