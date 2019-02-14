package logismikou.texnologia.wintersemesterfinal2019;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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

public class MyOrdersFragment extends Fragment {

    RecyclerView order_list;
    FirebaseAuth firebaseAuth;
    DatabaseReference orderReference;

    FirebaseRecyclerOptions<OrderInfo> options;
    FirebaseRecyclerAdapter<OrderInfo, MyOrdersFragment.ProductViewHolder> adapter;

    String order_qr_code[];

    Button start_shopping_btn2;
    ConstraintLayout layout1, layout2;
    ImageView qr_code_imageview, close_qrcode;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_orders, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        start_shopping_btn2 = v.findViewById(R.id.start_shopping_btn2);

        close_qrcode = v.findViewById(R.id.close_qrcode);
        qr_code_imageview = v.findViewById(R.id.qr_code_imageview);

        layout1 = v.findViewById(R.id.layout1);
        layout2 = v.findViewById(R.id.layout2);

        order_list = v.findViewById(R.id.order_list);
        order_list.setHasFixedSize(true);

        start_shopping_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,
                        new ShopFragment()).commit();
            }
        });

        close_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout2.setVisibility(View.GONE);
                order_list.setVisibility(View.VISIBLE);
            }
        });

        if(firebaseAuth.getCurrentUser() != null) {
            init_recycle_view();
            get_order_qr_code();
        }
        else{
            layout1.setVisibility(View.VISIBLE);
            order_list.setVisibility(View.GONE);
        }

        return v;
    }

    public void init_recycle_view(){
        // get user id
        String userId = firebaseAuth.getCurrentUser().getUid();
        // create reference for orders
        orderReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .child("Orders");

        options = new FirebaseRecyclerOptions.Builder<OrderInfo>()
                .setQuery(orderReference, OrderInfo.class).build();

        adapter = new FirebaseRecyclerAdapter<OrderInfo, MyOrdersFragment.ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyOrdersFragment.ProductViewHolder holder, int position, @NonNull OrderInfo model) {

                String image_url = "https://camo.githubusercontent.com/d4f8564ac8814286de6fe9868e" +
                        "d51c653923eb89/687474703a2f2f74686568756e74696e67746" +
                        "f6e69616e2e636f6d2f77702d636f6e74656e742f75706c6f6164732f32303134" +
                        "2f31322f7061636b6167655f7069632e6a7067";
                Picasso.get().load(image_url).into(holder.product_image_view, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                holder.description_view.setText(model.getTime_stamp());
                holder.title_view.setText(model.getOrder_id());
                holder.price_view.setVisibility(View.GONE);
                // add click listener for the items

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        // decode qr_code String to Bitmap
                        byte [] encodeByte = Base64.decode(order_qr_code[position],Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);


                        qr_code_imageview.setImageBitmap(bitmap);
                        layout2.setVisibility(View.VISIBLE);
                        order_list.setVisibility(View.GONE);
                    }
                });
            }

            @NonNull
            @Override
            public MyOrdersFragment.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate
                        (R.layout.product_cardview, parent, false);

                return new MyOrdersFragment.ProductViewHolder(view);
            }
        };
        LinearLayoutManager  mLayoutManager = new LinearLayoutManager (getContext());
        // bring newest first
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        order_list.setLayoutManager(mLayoutManager);
        adapter.startListening();
        order_list.setAdapter(adapter);
    }


    public void get_order_qr_code(){

        ValueEventListener get_popularListener = new ValueEventListener() {
            //initialize counter
            int count = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get number of products to init array
                long number_of_orders = dataSnapshot.getChildrenCount();
                // if there are no orders
                if (number_of_orders == 0){
                    layout1.setVisibility(View.VISIBLE);
                    order_list.setVisibility(View.GONE);
                }
                //int array_size = (int)number_of_orders;
                order_qr_code = new String[10000];


                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    // Get PopularProducts object and use the values to update the UI
                    OrderInfo orderInfo = postSnapshot.getValue(OrderInfo.class);

                    //save reference of the product
                    order_qr_code[count] = orderInfo.qr_code;
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
        orderReference.addValueEventListener(get_popularListener);
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
