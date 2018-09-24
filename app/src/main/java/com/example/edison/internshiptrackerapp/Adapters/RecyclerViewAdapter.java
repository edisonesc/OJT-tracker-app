package com.example.edison.internshiptrackerapp.Adapters;

import android.content.Context;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.edison.internshiptrackerapp.DataHolder;
import com.example.edison.internshiptrackerapp.HomeActivity;
import com.example.edison.internshiptrackerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.support.v4.app.Fragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        private TextView textViewData, textViewDescription;
        private Button buttonDelete, buttonEdit;

        public View button;


        public SimpleViewHolder (View itemView){
            super(itemView);
            swipeLayout =  itemView.findViewById(R.id.swipe);
            textViewPos = itemView.findViewById(R.id.position);
            textViewData = itemView.findViewById(R.id.text_data);
            buttonDelete = itemView.findViewById(R.id.delete);
            textViewDescription = itemView.findViewById(R.id.text_description);
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
    private Button mTimeIn, mTimeOut, selectCustomDate;
    private ArrayList<String[]> mDataset;
    private ArrayList<String> mKeys, mDesc;
    public RecyclerViewAdapter(Context context, ArrayList<String[]> objects, ArrayList<String> keys, ArrayList<String> mDesc){

        this.mContext = context;
        this.mDataset = objects;
        this.mKeys = keys;
        this.mDesc = mDesc;
    }
    @Override
    public  SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new SimpleViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position){
        final String timeIn = mDataset.get(position)[0], timeOut = mDataset.get(position)[1];
        String desc = mDesc.get(position);
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


                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

//                FirebaseUser mUser = mAuth.getCurrentUser();
//                String uid = mUser.getUid();
                final String selectedForDel = mKeys.get(position);

                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                databaseReference.child("Users").child("Edison").child("logs").child(DataHolder.getDate()).child(selectedForDel).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                            mDataset.remove(position);
                            mKeys.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position , mDataset.size());
                        Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
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
                final RadioGroup mDescription = timeLayout.findViewById(R.id.radioGroup);
                selectCustomDate = timeLayout.findViewById(R.id.dateButton);
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
                                String[] times = {mTimeIn.getText().toString(), mTimeOut.getText().toString()};
                                int radioButtonId = mDescription.getCheckedRadioButtonId();
                                String desc = null;
                                switch (radioButtonId){
                                    case R.id.radioButtonMorning:
                                        desc = "M";
                                        break;
                                    case R.id.radioButtonAfternoon:
                                        desc = "A";
                                        break;
                                    case R.id.radioButtonOvertime:
                                        desc = "OT";
                                        break;

                                    default:
                                        desc = "";
                                }
                                Map map = new HashMap<>();
                                map.put("time_in", times[0]);
                                map.put("time_out", times[1]);
                                map.put("desc", desc);
                                materialDialog.dismiss();
                                mItemManger.closeAllItems();
                                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Users").child("Edison").child("logs").child(DataHolder.getDate())
                                        .child(mKeys.get(position)).updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(mContext, "Successfully Changed", Toast.LENGTH_SHORT).show();
                                    }
                                });



                            }
                        })
                        .setNegativeButton("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mItemManger.closeAllItems();
                                materialDialog.dismiss();
                            }
                        });
                materialDialog.show();

            }
        });
        viewHolder.textViewPos.setText((position + 1) + ".");
        viewHolder.textViewData.setText(timeIn + " | " + timeOut);
        int descColor;
        switch (desc) {

        case "OT":
            descColor = Color.parseColor("#FFA63712");
        break;

        default:
            descColor = Color.parseColor("#0e7846");
    }
        viewHolder.textViewDescription.setTextColor(descColor);
        viewHolder.textViewDescription.setText(desc);

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
