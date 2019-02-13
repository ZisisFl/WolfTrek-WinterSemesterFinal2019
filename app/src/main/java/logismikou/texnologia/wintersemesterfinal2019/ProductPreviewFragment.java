package logismikou.texnologia.wintersemesterfinal2019;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    Button add_to_cart_btn, goto_shops;
    ImageView product_preview_img, quantity_up, quantity_down, shop_image, close_product_preview;

    DatabaseReference mDatabase, shop_reference, cart_reference;

    FirebaseAuth firebaseAuth;

    int quantity = 1;
    float product_price;
    String product_name, product_description, product_image_url;

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
        goto_shops = v.findViewById(R.id.goto_shops);

        product_preview_img = v.findViewById(R.id.product_preview_img);
        quantity_up = v.findViewById(R.id.quantity_up);
        quantity_down = v.findViewById(R.id.quantity_down);
        shop_image = v.findViewById(R.id.shop_image);
        close_product_preview = v.findViewById(R.id.close_product_preview);

        firebaseAuth = FirebaseAuth.getInstance();

        change_quantity();
        load_product_info();

        close_product_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new ShopFragment()).commit();
            }
        });

        shop_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductsHolderFragment fragment = new ProductsHolderFragment();
                //create an bundle to send reference string of the specific product
                Bundle arguments = new Bundle();
                arguments.putString("1234", String.valueOf(shop_reference));
                fragment.setArguments(arguments);
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_layout, fragment);
                ft.commit();
            }
        });

        goto_shops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new ShopsHolderFragment()).commit();
            }
        });

        add_to_cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseAuth.getCurrentUser() != null) {
                    add_product_to_cart();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("You have to sign in first in order to add products in cart")
                            .setCancelable(false)
                            .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("Take me to sign in", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                                            new SignInFragment()).commit();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

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
                    price_text.setText(price_string+" €");
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
                    price_text.setText(price_string+" €");
                }
            }
        });
    }

    public void load_product_info(){
        // open bundle
        Bundle arguments = getArguments();
        final String product_reference = arguments.getString("123");

        // reference to open shop page
        shop_reference = FirebaseDatabase.getInstance().getReference(product_reference).getParent().getParent();

        // reference for the product
        mDatabase = FirebaseDatabase.getInstance().getReference(product_reference);

        ValueEventListener get_itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get PopularProducts object and use the values to update the UI
                ProductInfo productInfo = dataSnapshot.getValue(ProductInfo.class);

                // load  preview and shop image using library Picasso
                Picasso.get().load(productInfo.image_url).into(product_preview_img);
                Picasso.get().load(productInfo.shop_image_url).into(shop_image);

                // load the rest of the informations
                shop_name_text.setText(productInfo.shop_name);
                product_name_text.setText(productInfo.name);
                description_text.setText(productInfo.description);
                price_text.setText(productInfo.price+" €");

                product_price = productInfo.price;
                product_name = productInfo.name;
                product_description = productInfo.description;
                product_image_url = productInfo.image_url;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(get_itemListener);
    }

    public void add_product_to_cart(){
        String userId = firebaseAuth.getCurrentUser().getUid();
        String id = mDatabase.push().getKey();

        CartProductInfo cartProductInfo = new CartProductInfo(product_name, product_description,
                product_image_url, product_price, quantity);


        cart_reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .child("Cart").child(id);
        cart_reference.setValue(cartProductInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(),"Added",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
