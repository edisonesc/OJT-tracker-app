package com.example.edison.internshiptrackerapp.Adapters;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.edison.internshiptrackerapp.HomeActivity;
import com.example.edison.internshiptrackerapp.R;
import android.support.v4.app.Fragment;
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.View.Y;

public class RecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerViewAdapter.SimpleViewHolder> implements RadialTimePickerDialogFragment.OnTimeSetListener {


    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {


        String AM_PM;
        if(hourOfDay < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }


        Toast.makeText(mContext, (getString(R.string.radial_time_picker_result_value, hourOfDay, minute)), Toast.LENGTH_SHORT).show();
        switch (currFocused){
            case 0:
                mTimeIn.setText(getString(R.string.radial_time_picker_result_value, hourOfDay, minute) + " " + AM_PM);
                break;
            case 1:
                mTimeOut.setText(getString(R.string.radial_time_picker_result_value, hourOfDay, minute) + " " + AM_PM);
                break;
        }
    }

    public final String getString(@StringRes int resId, Object... formatArgs) {
        return mContext.getResources().getString(resId, formatArgs);
    }

    public static class  SimpleViewHolder extends RecyclerView.ViewHolder{
        public SwipeLayout swipeLayout;
        private TextView textViewPos;
        private TextView textViewData;
        private Button buttonDelete, buttonEdit;

        public View button;


        public SimpleViewHolder (View itemView){
            super(itemView);
            swipeLayout =  itemView.findViewById(R.id.swipe);
            textViewPos = itemView.findViewById(R.id.position);
            textViewData = itemView.findViewById(R.id.text_data);
            buttonDelete = itemView.findViewById(R.id.delete);
//            button = itemView.findViewById(R.id.trash);
            buttonEdit =itemView.findViewById(R.id.edit);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(getClass().getSimpleName(), "onItemSelected: " + textViewData.getText().toString());
                    Toast.makeText(view.getContext(), "onItemSelected: " + textViewData.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private Context mContext;
    int currFocused;
    private  MaterialDialog materialDialog;
    private Button mTimeIn, mTimeOut;
    private ArrayList<String[]> mDataset;
    public RecyclerViewAdapter(Context context, ArrayList<String[]> objects){
        this.mContext = context;
        this.mDataset = objects;
    }
    @Override
    public  SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new SimpleViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position){
        final String timeIn = mDataset.get(position)[0], timeOut = mDataset.get(position)[1];
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        mItemManger.bindView(viewHolder.itemView, position);
        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                Toast.makeText(mContext, "ON START OPEN", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
                Toast.makeText(mContext, "Released", Toast.LENGTH_SHORT).show();

            }
        });

        viewHolder.swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "clicked.", Toast.LENGTH_SHORT).show();

            }
        });
        viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//                FirebaseAuth mAuth = FirebaseAuth.getInstance();
//                FirebaseUser mUser = mAuth.getCurrentUser();
//                String uid = mUser.getUid();
                String selectedForDel = mDataset.get(position)[0];

                mItemManger.removeShownLayouts(viewHolder.swipeLayout);

//                databaseReference.child("Users").child(uid).child("Workouts").child(selectedForDel).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                            mDataset.remove(position);
//                            notifyItemRemoved(position);
//
//                            notifyItemRangeChanged(position , mDataset.size());
////                            Toast.makeText(view.getContext(), "Deleted " + viewHolder.textViewData.getText().toString() + "!", Toast.LENGTH_SHORT).show();
//                        }
//                        else {
////                            Toast.makeText(view.getContext(), "Problem Deleting", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

                mItemManger.closeAllItems();

            }
        });
        viewHolder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View timeLayout = LayoutInflater.from(mContext).inflate(R.layout.time_layout, null);
                mTimeIn = timeLayout.findViewById(R.id.timeInButton);
                mTimeOut = timeLayout.findViewById(R.id.timeOutButton);
                final String selectedTimeIn = mDataset.get(position)[0], selectedTimeOut = mDataset.get(position)[1];
                mTimeIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                                .setOnTimeSetListener(RecyclerViewAdapter.this)
                                .setStartTime(Integer.valueOf(selectedTimeIn.substring(0, selectedTimeIn.indexOf(':'))),
                                        Integer.valueOf(selectedTimeIn.substring(selectedTimeIn.indexOf(':') + 1, selectedTimeIn.length() -3 )))
                                .setDoneText("Yay")
                                .setCancelText("Nop");

                        currFocused = 0;
                        rtpd.show(((FragmentActivity)mContext).getSupportFragmentManager(), "TimeIn");
//                        rtpd.show(((FragmentActivity)mContext).getSupportFragmentManager() , "TimeIn");
                    }
                });
                mTimeOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                                .setOnTimeSetListener(RecyclerViewAdapter.this)
                                .setStartTime(Integer.valueOf(selectedTimeOut.substring(0, selectedTimeOut.indexOf(':'))),
                                        Integer.valueOf(selectedTimeOut.substring(selectedTimeOut.indexOf(':') + 1, selectedTimeOut.length() - 3)))
                                .setDoneText("Yay")
                                .setCancelText("Nop");
                        currFocused = 1;
                        rtpd.show(((FragmentActivity)mContext).getSupportFragmentManager() , "TimeIn");
                    }
                });
                mTimeIn.setText(timeIn);
                mTimeOut.setText(timeOut);
                materialDialog = new MaterialDialog(mContext)
                        .setTitle("Edit time " + String.valueOf(position))
                        .setContentView(timeLayout)
                        .setMessage("Change")
                        .setPositiveButton("Change", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                mTimes.add(mTimeIn.getText() + " | " + mTimeOut.getText());
//                                adapter.notifyDataSetChanged();
                                materialDialog.dismiss();
                                Toast.makeText(mContext, "Changed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                materialDialog.dismiss();
                            }
                        });
                materialDialog.show();

            }
        });
        viewHolder.textViewPos.setText((position + 1) + ".");
        viewHolder.textViewData.setText(timeIn + " | " + timeOut);


    }
    @Override
    public int getItemCount(){
        return mDataset.size();
    }
    @Override
    public int getSwipeLayoutResourceId(int position){
        return R.id.swipe;
    }



}
