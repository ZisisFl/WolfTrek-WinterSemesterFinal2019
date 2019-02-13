package logismikou.texnologia.wintersemesterfinal2019;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.support.constraint.Constraints.TAG;

public class CartFragment extends Fragment {

    RecyclerView cart_list;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<CartProductInfo> options;
    FirebaseRecyclerAdapter<CartProductInfo, CartFragment.ProductViewHolder> adapter;
    FirebaseAuth firebaseAuth;

    Button start_shopping_btn, check_out_btn;
    TextView empty_cart_text;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cart, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        start_shopping_btn = v.findViewById(R.id.start_shopping_btn);
        empty_cart_text = v.findViewById(R.id.empty_cart_text);
        check_out_btn = v.findViewById(R.id.check_out_btn);

        //recycle view init
        cart_list = v.findViewById(R.id.cart_list);
        cart_list.setHasFixedSize(true);


        check_status();

        start_shopping_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new ShopFragment()).commit();
            }
        });

        check_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new CheckOutFragment()).commit();
            }
        });

        return v;
    }

    public void check_status(){
        // if user is logged then load his cart
        if(firebaseAuth.getCurrentUser() != null) {

            String userId = firebaseAuth.getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                    .child("Cart");

            ValueEventListener get_itemListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long count = dataSnapshot.getChildrenCount();
                    count = (int) count;

                    //if his cart is empty show empty cart message
                    if (count == 0){
                        start_shopping_btn.setVisibility(View.VISIBLE);
                        empty_cart_text.setVisibility(View.VISIBLE);
                        check_out_btn.setVisibility(View.INVISIBLE);
                    }
                    // else create recycle view
                    else{
                        init_recycle_view();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            databaseReference.addValueEventListener(get_itemListener);
        }
        else {
            start_shopping_btn.setVisibility(View.VISIBLE);
            empty_cart_text.setVisibility(View.VISIBLE);
            check_out_btn.setVisibility(View.INVISIBLE);
        }

    }


    public void init_recycle_view(){
        options = new FirebaseRecyclerOptions.Builder<CartProductInfo>()
                .setQuery(databaseReference, CartProductInfo.class).build();

        adapter = new FirebaseRecyclerAdapter<CartProductInfo, CartFragment.ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CartFragment.ProductViewHolder holder, final int position, @NonNull CartProductInfo model) {

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
                // make quantity and delete visible
                holder.quantity_view.setVisibility(View.VISIBLE);
                holder.delete_from_cart.setVisibility(View.VISIBLE);

                holder.quantity_view.setText("Quantity: "+String.valueOf(model.getQuantity()));

                holder.delete_from_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.getRef(holder.getAdapterPosition()).removeValue();

                        int number_of_products= adapter.getItemCount();
                        if(number_of_products-1 == 0){
                            start_shopping_btn.setVisibility(View.VISIBLE);
                            empty_cart_text.setVisibility(View.VISIBLE);
                            check_out_btn.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                // add click listener for the items

                // click of whole item
                //holder.setItemClickListener(new ItemClickListener() {
                //    @Override
                //    public void onClick(View view, int position) {
                //        // open product with the specific reference
                //        //goto_product_preview(reference_of_product[position]);
                //    }
                //});
            }

            @NonNull
            @Override
            public CartFragment.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate
                        (R.layout.product_cardview, parent, false);

                return new CartFragment.ProductViewHolder(view);
            }
        };
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        cart_list.setLayoutManager(mLayoutManager);
        adapter.startListening();
        cart_list.setAdapter(adapter);
    }



    public class ProductViewHolder extends RecyclerView.ViewHolder //implements View.OnClickListener
    {
        TextView description_view, title_view, price_view, quantity_view;
        ImageView product_image_view, delete_from_cart;

        //private ItemClickListener itemClickListener;

        public ProductViewHolder(View itemView)
        {
            super(itemView);

            description_view = itemView.findViewById(R.id.description_view);
            title_view = itemView.findViewById(R.id.title_view);
            price_view = itemView.findViewById(R.id.price_view);
            product_image_view = itemView.findViewById(R.id.product_image_view);
            delete_from_cart = itemView.findViewById(R.id.delete_from_cart);
            quantity_view = itemView.findViewById(R.id.quantity_view);

            //itemView.setOnClickListener(this);
        }

        //public void setItemClickListener(ItemClickListener itemClickListener){
        //    this.itemClickListener = itemClickListener;
        //}

        //@Override
        //public void onClick(View v) {
        //    itemClickListener.onClick(v, getAdapterPosition());
        //}
    }
}
