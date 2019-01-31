package logismikou.texnologia.wintersemesterfinal2019;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class FeedbackFragment extends Fragment {

    Button submit_btn;
    EditText store_sug_text, gen_sug_text;
    ImageView close7;

    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedback, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(getActivity());

        submit_btn = v.findViewById(R.id.submit_btn);

        store_sug_text = v.findViewById(R.id.store_sug_text);
        gen_sug_text = v.findViewById(R.id.gen_sug_text);

        close7 = v.findViewById(R.id.close7);

        close7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new ContactFragment()).commit();
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_feedback();
            }
        });

        return v;
    }

    public void send_feedback(){
        String store_suggestion = store_sug_text.getText().toString();
        String general_suggestion = gen_sug_text.getText().toString();

        if (!store_suggestion.equals("") && general_suggestion.equals("")){
            //send store suggestion
            create_feedback_message(store_suggestion, "-");

            progressDialog.setMessage("Sending feedback...");
            progressDialog.show();

        }
        else if (store_suggestion.equals("") && !general_suggestion.equals("")){
            //send general suggestion
            create_feedback_message("-", general_suggestion);

            progressDialog.setMessage("Sending feedback...");
            progressDialog.show();
        }
        else if (!store_suggestion.equals("") && !general_suggestion.equals("")){
            //send both
            create_feedback_message(store_suggestion, general_suggestion);

            progressDialog.setMessage("Sending feedback...");
            progressDialog.show();
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void create_feedback_message(String store_suggestion, String general_suggestion){

        // get current time
        Date currentTime = Calendar.getInstance().getTime();
        String c_time = currentTime.toString();
        // get key
        String id = mDatabase.push().getKey();

        if(firebaseAuth.getCurrentUser() != null){
            String userId = firebaseAuth.getCurrentUser().getUid();

            FeedbackMessage feedbackMessage = new FeedbackMessage(userId, c_time,
                    store_suggestion, general_suggestion);

            mDatabase.child("Feedback").child(id).setValue(feedbackMessage)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),"An error occurred",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            String user = "guest";

            FeedbackMessage feedbackMessage = new FeedbackMessage(user, c_time,
                    store_suggestion, general_suggestion);

            mDatabase.child("Feedback").child(id).setValue(feedbackMessage)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),"An error occurred",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
