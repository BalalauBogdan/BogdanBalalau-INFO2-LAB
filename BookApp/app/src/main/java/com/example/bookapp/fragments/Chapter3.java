package com.example.bookapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Chapter3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Chapter3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Chapter3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Chapter3.
     */
    // TODO: Rename and change types and number of parameters
    public static Chapter3 newInstance(String param1, String param2) {
        Chapter3 fragment = new Chapter3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter3, container, false);
        Button toCh3Button=view.findViewById(R.id.toCh4Button);
        toCh3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment Chapter4 = new Chapter4();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_layout, Chapter4);
                transaction.commit();
            }
        });
        Button backToCh2Button=view.findViewById(R.id.backToCh2Button);
        backToCh2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment Chapter2 = new Chapter2();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_layout, Chapter2);
                transaction.commit();
            }
        });
        return view;
    }
}