package com.example.project1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    TextView profile_phone, profilePublicKey;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        profile_phone = view.findViewById(R.id.profile_phone);
        profilePublicKey = view.findViewById(R.id.text_view_public_key);

        if(mContext != null) {
            String p = Utils.getStringFromUserData(mContext, "phone");
            String pk = UserLogin.publicKey;
            profile_phone.setText(p);
            profilePublicKey.setText(pk);
        }

        return view;
    }

}
