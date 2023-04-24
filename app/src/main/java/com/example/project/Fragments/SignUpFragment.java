package com.example.project.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.project.Interfaces.IFragmentCommunication;
import com.example.project.Model.User;
import com.example.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private EditText editTextName, editTextEmailSignUp, editTextPasswordSignUp,
            editTextConfirmPassword;
    private Button signUpButton;
    private String name, email, password, confirmPassword;
    private IFragmentCommunication mListener;
    private ImageView avatar;


    public SignUpFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFragmentCommunication) {
            this.mListener = (IFragmentCommunication) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement SignUpFragment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextEmailSignUp = view.findViewById(R.id.editTextEmailSignUp);
        editTextPasswordSignUp = view.findViewById(R.id.editTextPasswordSignUp);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        signUpButton = view.findViewById(R.id.signUpButton);
        avatar = view.findViewById(R.id.avatar);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = String.valueOf(editTextName.getText()).trim();
                email = String.valueOf(editTextEmailSignUp.getText()).trim();
                password = String.valueOf(editTextPasswordSignUp.getText()).trim();
                confirmPassword = String.valueOf(editTextConfirmPassword.getText()).trim();

                if (name.equals("")) {
                    editTextName.setError("Must input name!");
                }
                if (email.equals("")) {
                    editTextEmailSignUp.setError("Must input email!");
                }
                if (password.equals("")) {
                    editTextPasswordSignUp.setError("Password must not be empty!");
                }
                if (!confirmPassword.equals(password)) {
                    editTextConfirmPassword.setError("Passwords must match!");
                }

                if (!name.equals("") && !email.equals("")
                        && !password.equals("")
                        && confirmPassword.equals(password)) {

                    User user = new User(name, email);

                    //              Firebase authentication: Create user.......
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mUser = task.getResult().getUser();

//                                    Adding name to the FirebaseUser...
                                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .build();

                                        mUser.updateProfile(profileChangeRequest)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            mListener.registerDone(mUser, user);
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        //  Log.e(Tags.TAG, "onFailure: "+e.getMessage());
                                                    }
                                                })
                                        ;

                                    }
                                }
                            });
                }

            }


        });

        return view;
    }


}


