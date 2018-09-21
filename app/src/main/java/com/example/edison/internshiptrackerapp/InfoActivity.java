package com.example.edison.internshiptrackerapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vstechlab.easyfonts.EasyFonts;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import devlight.io.library.ArcProgressStackView;
import me.drakeet.materialdialog.MaterialDialog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InfoActivity.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InfoActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoActivity extends Fragment implements Animator.AnimatorListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public InfoActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoActivity newInstance(String param1, String param2) {
        InfoActivity fragment = new InfoActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private MaterialDialog materialDialog;
    private TextView totalDuration, totalLabel, goalHoursText, goalHoursLabel;
    private ArrayList<ArcProgressStackView.Model> models = new ArrayList<>();
    private ArcProgressStackView arcProgressStackView;
    private int mCounter = 0;
    private LinearLayout goalHourButton;
    private int goalHour;
    ArrayList<String[]> data = new ArrayList();
    ArrayList<long[]> timeLists = new ArrayList<>();
    ArrayList<Integer> maxHour = new ArrayList<>(), minHour = new ArrayList<>();
    private   DataHolder dataHolder = new DataHolder();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        totalDuration= view.findViewById(R.id.totalHoursText);
        totalLabel = view.findViewById(R.id.totalHoursLabel);
        goalHoursLabel = view.findViewById(R.id.goalHoursLabel);
        goalHoursText = view.findViewById(R.id.goalHoursText);
        goalHourButton = view.findViewById(R.id.goalHourButton);
        goalHoursLabel.setTypeface(EasyFonts.ostrichBlack(getContext()));
        totalLabel.setTypeface(EasyFonts.ostrichBlack(getContext()));
        goalHoursText.setTypeface(EasyFonts.captureIt(getContext()));
        totalDuration.setTypeface(EasyFonts.captureIt(getContext()));


        arcProgressStackView = view.findViewById(R.id.apsv);
        arcProgressStackView.setTypeface(EasyFonts.ostrichBlack(getContext()));
        arcProgressStackView.setAnimationDuration(1000);
        arcProgressStackView.setSweepAngle(270);


        final View goalLayout = LayoutInflater.from(getActivity()).inflate(R.layout.goal_hour_layout, null);
        final EditText goalEditText = goalLayout.findViewById(R.id.goalEditText);
        goalEditText.setText(String.valueOf(DataHolder.getGoalHours()));
        goalHourButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                materialDialog = new MaterialDialog(getActivity())
                        .setTitle("Set hours goal")
                        .setContentView(goalLayout)

                        .setPositiveButton("Set", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Map map = new HashMap();
                                map.put("goal_hour", Integer.valueOf(goalEditText.getText().toString()));
                              databaseReference.child("Users").child("Edison").updateChildren(map);
                              materialDialog.dismiss();


                            }
                        })
                        .setNegativeButton("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                materialDialog.dismiss();
                            }
                        });

                if (goalLayout != null) {
                    ViewGroup parent = (ViewGroup) goalLayout.getParent();
                    if (parent != null) {
                        parent.removeAllViews();
                    }
                }
                materialDialog.show();

            }
        });



        databaseReference.child("Users").child("Edison").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataHolder.setGoalHours(dataSnapshot.child("goal_hour").getValue(Integer.class));
                goalHoursText.setText(DataHolder.getGoalHours() + " hours");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("Users").child("Edison").child("logs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String key = dataSnapshot.getKey();


                databaseReference.child("Users").child("Edison").child("logs").child(key).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                        Toast.makeText(getContext(),
//                                String.valueOf(dataSnapshot.child("time_in")), Toast.LENGTH_SHORT).show();
                        String timeIn = dataSnapshot.child("time_in").getValue(String.class);
                        String timeOut = dataSnapshot.child("time_out").getValue(String.class);
                        String[] timeData = {timeIn, timeOut};
                        data.add(timeData);
                        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");

                        Date in = null, out = null;

                        try{
                            in = formatTime.parse(timeIn);
                            out = formatTime.parse(timeOut);


                            long diff = in.getTime() - out.getTime();
                            long diffHours = diff / (60 * 60 * 1000) % 24;
                            long diffMinutes = diff / (60 * 1000) % 60;

                            long[] time = {Math.abs(diffHours), Math.abs(diffMinutes)};
                            timeLists.add(time);

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        long totalHours = 0, totalMinutes = 0;

                        dataHolder.setHoursSize(timeLists.size());
                        for(int i = 0; i < timeLists.size(); i++){
                            totalHours += timeLists.get(i)[0];
                            totalMinutes += timeLists.get(i)[1];

                            maxHour.add(Integer.valueOf(String.valueOf(timeLists.get(i)[0])));

//                            Toast.makeText(getContext(), String.valueOf( timeLists.get(i)[1]), Toast.LENGTH_SHORT).show();
//                            totalDuration.setText(String.valueOf(totalHours + " hrs " + totalMinutes + " minutes"));
                        }

//
                        if(totalMinutes >= 60 ) {
                            //FUCK
                            long minutesToHours = Math.abs(totalMinutes / 60);
                            totalHours += minutesToHours;
                            totalMinutes -= 60;
//                            Toast.makeText(getContext(), String.valueOf(minutesToHours + "\n" + totalHours
//                            + "\n" + totalMinutes)  , Toast.LENGTH_SHORT).show();
//                            totalDuration.setText(String.valueOf(totalHours + " hrs " + totalMinutes + " minutes"));


                        }

//                        Toast.makeText(getContext(), "max :" + Collections.max(maxHour) + "\n"
//                                + "min :" + Collections.min(maxHour)
//                                , Toast.LENGTH_SHORT).show();
                        dataHolder.setMaxHours( Collections.max(maxHour));
                        dataHolder.setMinHours(Collections.min(maxHour));

                        dataHolder.setTotalHours(totalHours);
                        totalDuration.setText(String.valueOf(totalHours + " hrs " + totalMinutes + " minutes"));

//                        Collections.sort(DataHolder.getHours());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                models.add(new ArcProgressStackView.Model( (float)DataHolder.getTotalHours() / DataHolder.getHoursSize()  +" Hours", (float) DataHolder.getTotalHours() / DataHolder.getHoursSize() , Color.parseColor("#e8eaf6"), Color.parseColor("#00695c")));
                models.add(new ArcProgressStackView.Model( (float)DataHolder.getMinHours() + " Hours", (float) DataHolder.getMinHours() / 24 * 100, Color.parseColor("#e8eaf6"), Color.parseColor("#005662")));
                models.add(new ArcProgressStackView.Model((float)DataHolder.getMaxHours()  + " Hours", (float) DataHolder.getMaxHours() / 24 * 100,Color.parseColor("#e8eaf6"), Color.parseColor("#006db3")));
                models.add(new ArcProgressStackView.Model(String.valueOf(DataHolder.getTotalHours() + " /" + DataHolder.getGoalHours() ),
                        Float.valueOf(String.valueOf((float)DataHolder.getTotalHours()/ DataHolder.getGoalHours()  * 100))
                ,Color.parseColor("#e8eaf6"), Color.parseColor("#000a12")));
//                Toast.makeText(getContext(), "max : " + DataHolder.getMaxHours() , Toast.LENGTH_SHORT).show();
                final Float[] modelsData = {

                        (float) DataHolder.getTotalHours() / DataHolder.getHoursSize(),
                        (float) DataHolder.getMinHours() / 24 * 100,
                        (float) DataHolder.getMaxHours() / 24 * 100,

                        Float.valueOf(String.valueOf((float)DataHolder.getTotalHours()/DataHolder.getGoalHours()  * 100))

                };

                Toast.makeText(getContext(), DataHolder.getGoalHours() + " HERSH", Toast.LENGTH_SHORT).show();
                arcProgressStackView.setModels(models);
                final ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0F, 105.0F);
                valueAnimator.setDuration(800);
                valueAnimator.setStartDelay(200);
                valueAnimator.setRepeatMode(ValueAnimator.RESTART);
                valueAnimator.setRepeatCount(models.size() -1);
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                        animation.removeListener(this);
                        animation.addListener(this);
                        mCounter = 0;
//                        for(ArcProgressStackView.Model model : arcProgressStackView.getModels()) model.setProgress(50);

                        for(int i = 0; i < models.size(); i++){

                            models.get(i).setProgress(modelsData[i]);
                        }
                        arcProgressStackView.animateProgress();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        mCounter++;
                    }
                });
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {

                        arcProgressStackView.getModels().get(Math.min(mCounter, models.size() -1))
                                .setProgress((Float) animation.getAnimatedValue());

//                        models.get(0).setProgress(a);

                        arcProgressStackView.postInvalidate();
                    }
                });


                valueAnimator.start();



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return  view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public static String round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.toPlainString();
    }

    @Override
    public void onAnimationStart(Animator animation, boolean isReverse) {

    }

    @Override
    public void onAnimationEnd(Animator animation, boolean isReverse) {

    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
