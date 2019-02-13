package logismikou.texnologia.wintersemesterfinal2019;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class ProductsHolderFragment extends Fragment {

    Button food_btn, drinks_btn;
    ImageView close_product_holder;

    RecyclerView product_list;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<ProductInfo> options;
    FirebaseRecyclerAdapter<ProductInfo, ProductViewHolder> adapter;

    // array of product references
    String[] reference_of_product;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_products_holder, container, false);

        food_btn = v.findViewById(R.id.food_btn);
        drinks_btn = v.findViewById(R.id.drinks_btn);
        close_product_holder = v.findViewById(R.id.close_product_holder);

        //recycle view init
        product_list = v.findViewById(R.id.product_list);
        product_list.setHasFixedSize(true);

        // open bundle with shop reference
        Bundle arguments = getArguments();
        final String product_reference = arguments.getString("1234");

        load_components(product_reference);


        food_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(product_reference)
                        .child("Food");
                food_btn.setBackgroundColor(Color.WHITE);
                food_btn.setTextColor(Color.BLACK);
                drinks_btn.setBackgroundColor(Color.BLACK);
                drinks_btn.setTextColor(Color.WHITE);
                init_recycle_view();
                get_products_reference();
            }
        });

        drinks_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(product_reference)
                        .child("Drinks");
                food_btn.setBackgroundColor(Color.BLACK);
                food_btn.setTextColor(Color.WHITE);
                drinks_btn.setBackgroundColor(Color.WHITE);
                drinks_btn.setTextColor(Color.BLACK);
                init_recycle_view();
                get_products_reference();
            }
        });

        close_product_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new ShopsHolderFragment()).commit();
            }
        });

        return v;
    }

    public void load_components(String product_reference){
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(product_reference)
                .child("Drinks");
        food_btn.setBackgroundColor(Color.BLACK);
        food_btn.setTextColor(Color.WHITE);
        drinks_btn.setBackgroundColor(Color.WHITE);
        drinks_btn.setTextColor(Color.BLACK);

        init_recycle_view();
        get_products_reference();
    }

    public void init_recycle_view(){
        options = new FirebaseRecyclerOptions.Builder<ProductInfo>()
                .setQuery(databaseReference, ProductInfo.class).build();

        adapter = new FirebaseRecyclerAdapter<ProductInfo, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull ProductInfo model) {

                Picasso.get().load(model.getImage_url()).into(holder.product_image_view, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                holder.description_view.setText(model.getDescription());
                holder.title_view.setText(model.getName());
                holder.price_view.setText(String.valueOf(model.getPrice())+" €");
                // add click listener for the items

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        // open product with the specific reference
                        goto_product_preview(reference_of_product[position]);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate
                        (R.layout.product_cardview, parent, false);

                return new ProductViewHolder(view);
            }
        };
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        product_list.setLayoutManager(mLayoutManager);
        adapter.startListening();
        product_list.setAdapter(adapter);
    }

    public void goto_product_preview(String reference){
        ProductPreviewFragment fragment = new ProductPreviewFragment();
        //create an bundle to send reference string of a specific product
        Bundle arguments = new Bundle();
        arguments.putString("123", reference);
        fragment.setArguments(arguments);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, fragment);
        ft.commit();
    }

    public void get_products_reference(){

        ValueEventListener get_popularListener = new ValueEventListener() {
            //initialize counter
            int count = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get number of products to init array
                long number_of_products = dataSnapshot.getChildrenCount();
                int array_size = (int)number_of_products;
                reference_of_product = new String[array_size];


                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    // Get PopularProducts object and use the values to update the UI
                    ProductInfo productInfo = postSnapshot.getValue(ProductInfo.class);

                    //save reference of the product
                    reference_of_product[count] = productInfo.reference;
                    //update counter
                    count ++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(get_popularListener);
    }




    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView description_view, title_view, price_view;
        ImageView product_image_view;

        private ItemClickListener itemClickListener;

        public ProductViewHolder(View itemView)
        {
            super(itemView);

            description_view = itemView.findViewById(R.id.description_view);
            title_view = itemView.findViewById(R.id.title_view);
            price_view = itemView.findViewById(R.id.price_view);
            product_image_view = itemView.findViewById(R.id.product_image_view);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());
        }
    }
}
