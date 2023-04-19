package com.example.project;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LandingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandingFragment extends Fragment {

    private TextView welcomeText;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonSignUp;
    private String email, password;
    private FirebaseAuth mAuth;

    private IFragmentCommunication mListener;


    public LandingFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LandingFragment newInstance() {
        LandingFragment fragment = new LandingFragment();
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
        if (context instanceof IFragmentCommunication){
            this.mListener = (IFragmentCommunication) context;
        }else{
            throw new RuntimeException(context.toString()+ "must implement LandingFragment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_landing, container, false);

        welcomeText = view.findViewById(R.id.welcomeText);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonSignUp = view.findViewById(R.id.buttonSignUp);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();

                if(email.equals("")){
                    editTextEmail.setError("Must input email!");
                }
                if(password.equals("")){
                    editTextPassword.setError("Password must not be empty!");
                }

                if(email.equals("")){
                    editTextEmail.setError("Must input email!");
                }
                if(password.equals("")){
                    editTextPassword.setError("Password must not be empty!");
                }
                if(!email.equals("") && !password.equals("")){
//                    Sign in to the account....
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(getContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Login Failed!"+e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        mListener.populateHomeFragment(mAuth.getCurrentUser());
                                    }
                                }
                            })
                    ;
                }
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("demo", "sign up button clicked");
                mListener.populateSignUpFragment();
            }
        });

        return view;
    }
}