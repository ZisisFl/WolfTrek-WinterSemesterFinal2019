package logismikou.texnologia.wintersemesterfinal2019;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import static android.support.constraint.Constraints.TAG;

public class ProductPreviewFragment extends Fragment {

    TextView description_text, price_text, quantity_text, product_name_text, shop_name_text;
    Button add_to_cart_btn;
    ImageView product_preview_img, quantity_up, quantity_down;

    DatabaseReference mDatabase;

    int quantity = 1;
    float product_price;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_preview, container, false);

        description_text = v.findViewById(R.id.description_text);
        price_text = v.findViewById(R.id.price_text);
        quantity_text = v.findViewById(R.id.quantity_text);
        product_name_text = v.findViewById(R.id.product_name_text);
        shop_name_text = v.findViewById(R.id.shop_name_text);

        add_to_cart_btn = v.findViewById(R.id.add_to_cart_btn);

        product_preview_img = v.findViewById(R.id.product_preview_img);
        quantity_up = v.findViewById(R.id.quantity_up);
        quantity_down = v.findViewById(R.id.quantity_down);

        change_quantity();
        load_product_info();

        return v;
    }

    public void change_quantity(){
        quantity_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity < 99){
                    // update quantity
                    quantity ++;
                    quantity_text.setText(quantity+"");

                    // update price
                    float price_value = quantity * product_price;

                    // reformat decimal value
                    String price_string = new DecimalFormat("#.#").format(price_value);
                    price_text.setText(price_string);
                }
            }
        });

        quantity_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity > 1){
                    quantity --;
                    quantity_text.setText(quantity+"");

                    // update price
                    float price_value = quantity * product_price;

                    // reformat decimal value
                    String price_string = new DecimalFormat("#.#").format(price_value);
                    price_text.setText(price_string);
                }
            }
        });
    }

    public void load_product_info(){
        // open bundle
        Bundle arguments = getArguments();
        final String product_reference = arguments.getString("123");

        mDatabase = FirebaseDatabase.getInstance().getReference(product_reference);

        ValueEventListener get_popularListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get PopularProducts object and use the values to update the UI
                ProductInfo productInfo = dataSnapshot.getValue(ProductInfo.class);

                // load image using library Picasso
                Picasso.get().load(productInfo.image_url).into(product_preview_img);
                // load the rest of the informations
                shop_name_text.setText(productInfo.shop_name);
                product_name_text.setText(productInfo.name);
                description_text.setText(productInfo.description);
                price_text.setText(productInfo.price+"");

                product_price = productInfo.price;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(get_popularListener);
    }
}
