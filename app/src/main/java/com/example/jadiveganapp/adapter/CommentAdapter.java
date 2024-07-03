package com.example.jadiveganapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jadiveganapp.ProfileActivity;
import com.example.jadiveganapp.R;
import com.example.jadiveganapp.databinding.RowCommentBinding;
import com.example.jadiveganapp.model.CommentModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.HolderComment>{

    // View binding
    private RowCommentBinding binding;

    // Context
    private Context context;

    // Firebase
    FirebaseAuth firebaseAuth;

    // Arraylist to hold comment
    private ArrayList<CommentModel> commentModelArrayList;

    public CommentAdapter(Context context, ArrayList<CommentModel> commentModelArrayList) {
        this.context = context;
        this.commentModelArrayList = commentModelArrayList;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCommentBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderComment(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {
        CommentModel commentModel = commentModelArrayList.get(position);

        String id = commentModel.getId();
        String recipeId = commentModel.getRecipeId();
        String comment = commentModel.getComment();
        String uid = commentModel.getUid();
        String timestamp = commentModel.getTimestamp();

        // format date
        String date = ProfileActivity.formatTimestamp(Long.parseLong(timestamp));

        // set data
        holder.dateTv.setText(date);
        holder.commentTv.setText(comment);

        loadUserDetails(commentModel, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null && uid.equals(firebaseAuth.getUid())) {
                    deleteComment(commentModel, holder);
                }
            }
        });
    }

    private void deleteComment(CommentModel commentModel, HolderComment holder) {
        String recipeUid = commentModel.getRecipeId();
        String commentId = commentModel.getId();

        if (recipeUid == null || commentId == null) {
            Toast.makeText(context, "Error: Comment data is incomplete", Toast.LENGTH_SHORT).show();
            return;
        }

        // Display confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Comment")
                .setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform delete operation
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Recipes");
                        ref.child(recipeUid)
                                .child("Comments")
                                .child(commentId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Comment deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed deleting comment", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void loadUserDetails(CommentModel commentModel, HolderComment holder) {
        String uid = commentModel.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = "" + snapshot.child("name").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();

                        // set data
                        holder.nameTV.setText(name);

                        try {
                            Glide.with(context)
                                    .load(profileImage)
                                    .placeholder(R.drawable.user)
                                    .into(holder.profileIv);
                        }
                        catch (Exception e) {
                            holder.profileIv.setImageResource(R.drawable.user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return commentModelArrayList.size();
    }

    class HolderComment extends RecyclerView.ViewHolder {

        // UI view
        ImageView profileIv;
        TextView nameTV, dateTv, commentTv;

        public HolderComment(@NonNull View itemView) {
            super(itemView);

            profileIv = binding.profileIv;
            nameTV = binding.nameTv;
            dateTv = binding.dateTv;
            commentTv = binding.commentTv;
        }
    }
}
