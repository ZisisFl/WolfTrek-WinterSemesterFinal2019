package logismikou.texnologia.wintersemesterfinal2019;


import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

import static android.support.constraint.Constraints.TAG;

public class CheckOutFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    LinearLayout order_list_layout;
    Button place_order;
    ImageView close_order_list;

    double COMMISION = 0.06;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_check_out, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        order_list_layout = v.findViewById(R.id.order_list_layout);
        close_order_list = v.findViewById(R.id.close_order_list);
        place_order = v.findViewById(R.id.place_order);

        checkout_list();

        close_order_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new CartFragment()).commit();
                onDestroyView();
            }
        });

        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new FinalizeOrderFragment()).commit();
            }
        });

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void checkout_list(){
        String userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .child("Cart");

        ValueEventListener get_itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float subtotal_sum = 0;

                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    CartProductInfo cartProductInfo = postSnapshot.getValue(CartProductInfo.class);

                    String product_name = cartProductInfo.name;
                    int product_quantity = cartProductInfo.quantity;
                    float product_price = cartProductInfo.price;
                    float product_total = product_quantity * product_price;

                    create_product_text(product_name, product_quantity, product_price, product_total);

                    subtotal_sum = product_total + subtotal_sum;
                }
                checkout_sums(subtotal_sum);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(get_itemListener);
    }

    public void create_product_text(String product_name, int product_quantity, float product_price,
                                    float product_total){

        // product name text
        design_text_view("Product name: "+product_name, 14, 0,
                0, 20, 0, 20);

        // product quantity
        design_text_view("Product quantity: "+product_quantity, 14, 0,
                0, 0, 0, 20);

        // product price
        design_text_view("Product price: "+product_price+" €", 14, 0,
                0, 0, 0, 20);

        // product total
        design_text_view("Product total: "+product_total+" €", 14, 0,
                0, 0, 0, 20);
    }

    public void checkout_sums(float subtotal_sum){
        draw_black_line();

        // subtotal text
        String subtotal_string = new DecimalFormat("#.##").format(subtotal_sum);
        design_text_view("Subtotal: "+subtotal_string+" €", 16, 1,
                0, 20, 0, 20);

        draw_black_line();

        // commission text
        design_text_view("Commission: "+(COMMISION*100)+" %", 18, 1,
                0, 20, 0, 0);

        // total text
        String total_string = new DecimalFormat("#.##").format(((COMMISION*subtotal_sum)+subtotal_sum));
        design_text_view("Total: "+ total_string +" €", 18, 1,
                0, 0, 0, 0);
    }

    public void design_text_view(String text, int text_size, int color, int pad_left,
                                 int pad_top, int pad_right, int pad_bottom){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                LinearLayout.LayoutParams.WRAP_CONTENT);// Height of TextView

        TextView textView = new TextView(getContext());
        textView.setLayoutParams(lp);
        if(color == 1){
            textView.setTextColor(Color.BLACK);
        }
        textView.setPadding(pad_left, pad_top, pad_right, pad_bottom);
        textView.setTextSize(text_size);
        textView.setText(text);
        order_list_layout.addView(textView);

    }

    public void draw_black_line(){
        // draw a black line
        View v = new View(getActivity());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                5
        ));
        v.setBackgroundColor(Color.BLACK);
        order_list_layout.addView(v);
    }
}
