package com.example.edison.internshiptrackerapp;

import android.app.FragmentManager;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.example.edison.internshiptrackerapp.Adapters.RecyclerViewAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.SnackBar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeActivity.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeActivity extends Fragment implements RadialTimePickerDialogFragment.OnTimeSetListener, CalendarDatePickerDialogFragment.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeActivity newInstance(String param1, String param2) {
        HomeActivity fragment = new HomeActivity();
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
    private  MaterialDialog materialDialog;
    private Button mTimeIn, mTimeOut, selectCustomDate;
    private int currFocused;
    private ArrayList<String[]> mTimes = new ArrayList<>();
    private ArrayList<String> mKeys = new ArrayList<>(), mDesc = new ArrayList<>();
    private RecyclerView recyclerView;
    private CalendarView calendarView;
    private RecyclerView.Adapter adapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private  String[] primeTime = null;
    Date currentDate;
    private  ArrayList test = new ArrayList();
    private List<EventDay> eventDays = new ArrayList<>();
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        calendarView.showCurrentMonthPage();
        currentDate = Calendar.getInstance().getTime();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        FloatingActionButton addNewButton = view.findViewById(R.id.buttonAdd);
//        recyclerView = view.findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        adapter = new RecyclerViewAdapter(getContext(), mTimes, mKeys);
//        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        final DateFormat currFormatter = new SimpleDateFormat("dd-MM-yyyy");


//        recyclerView.setAdapter(adapter);



//
//


        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                View recyclerLayoutView = LayoutInflater.from(getActivity()).inflate(R.layout.recyclerview_layout, null);
                recyclerView = recyclerLayoutView.findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new RecyclerViewAdapter(getContext(), mTimes, mKeys , mDesc);
                recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
                recyclerView.setAdapter(adapter);

                String date = currFormatter.format(eventDay.getCalendar().getTime());

                materialDialog = new MaterialDialog(getActivity())
                        .setTitle("History: "+date)
                        .setContentView(recyclerLayoutView)
                        .setMessage("hey")
                        .setNegativeButton("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                materialDialog.dismiss();
                            }
                        });
                materialDialog.show();







                Toast.makeText(getContext(), date, Toast.LENGTH_SHORT).show();
                DataHolder.setDate(date);
                mTimes.clear();
                mKeys.clear();
                mDesc.clear();
                eventDays.clear();



                databaseReference.child("Users").child("Edison").child("logs").child(date).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {



                        String timeIn = dataSnapshot.child("time_in").getValue(String.class);
                        String timeOut = dataSnapshot.child("time_out").getValue(String.class);
                        String desc = dataSnapshot.child("desc").getValue(String.class);
                        String[] times = {timeIn, timeOut};
                        primeTime = times;
                        mDesc.add(desc);
                        mTimes.add(primeTime);
                        mKeys.add(dataSnapshot.getKey());
                        adapter.notifyDataSetChanged();
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

            }
        });

        databaseReference.child("Users").child("Edison").child("logs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {




                try {

                    Date da = currFormatter.parse(dataSnapshot.getKey());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(da);
                    eventDays.add(new EventDay(cal, R.drawable.ic_adjust_black_24dp));
                    calendarView.setEvents(eventDays);


                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {



            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                try {

                    Date da = currFormatter.parse(dataSnapshot.getKey());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(da);
                    eventDays.add(new EventDay(cal, R.drawable.ic_adjust_black_24dp));
                    calendarView.setEvents(eventDays);


                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        addNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View timeLayout = LayoutInflater.from(getActivity()).inflate(R.layout.time_layout, null);
                mTimeIn = timeLayout.findViewById(R.id.timeInButton);
                mTimeOut = timeLayout.findViewById(R.id.timeOutButton);
                final RadioGroup mDescription = timeLayout.findViewById(R.id.radioGroup);
                selectCustomDate = timeLayout.findViewById(R.id.dateButton);
                DateFormat currFormatter = new SimpleDateFormat("dd/MM/yyyy");
                Date date = Calendar.getInstance().getTime();
                final String formatedDate = currFormatter.format(date).replace('/','-');
                mTimeIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Date date = new Date();
                        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                        calendar.setTime(date);   // assigns calendar to given date

                        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                                .setOnTimeSetListener(HomeActivity.this)
                                .setStartTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
                                .setDoneText("Yay")
                                .setCancelText("Nop");

                        currFocused = 0;
                        rtpd.show(getFragmentManager(), "TimeIn");
                    }
                });
                mTimeOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                                .setOnTimeSetListener(HomeActivity.this)
                                .setStartTime(15, 00)
                                .setDoneText("Yay")
                                .setCancelText("Nop");
                        currFocused = 1;
                        rtpd.show(getFragmentManager(), "TimeIn");
                    }
                });
               materialDialog = new MaterialDialog(getActivity())
                        .setTitle("Add new time")
                        .setContentView(timeLayout)
                        .setMessage("hey")
                        .setPositiveButton("Add", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] times = {mTimeIn.getText().toString(), mTimeOut.getText().toString()};
                                if(times[0].equals("+") || times[1].equals("+") || times[0].equals("+") && times[1].equals("+")){


                                }
                                else {
                                    mTimes.add(times);
//                                    adapter.notifyDataSetChanged();
                                    String idKey = databaseReference.child("Edison").child(selectCustomDate.getText().toString()).push().getKey();
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

                                    databaseReference.child("Users").child("Edison").child("logs").child(selectCustomDate.getText().toString()).child(idKey).setValue(map);
//                                mTimes.clear();

                                    materialDialog.dismiss();
                                    Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                materialDialog.dismiss();
                            }
                        });
                materialDialog.show();
                selectCustomDate.setText(formatedDate);
                selectCustomDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                                .setOnDateSetListener(HomeActivity.this)
                                .setPreselectedDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                                .setDoneText("Select")
                                .setCancelText("Back");

                            cdp.show(getFragmentManager(), null);

                    }
                });

            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {

        String AM_PM;
        if(hourOfDay < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }


        Toast.makeText(getActivity(), (getString(R.string.radial_time_picker_result_value, hourOfDay, minute)), Toast.LENGTH_SHORT).show();
        switch (currFocused){
            case 0:
                mTimeIn.setText(getString(R.string.radial_time_picker_result_value, hourOfDay, minute) + " " + AM_PM);
                break;
            case 1:
                mTimeOut.setText( getString(R.string.radial_time_picker_result_value, hourOfDay, minute) + " " + AM_PM);
                break;
        }
    }


    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {

        String valueOfday = String.valueOf(dayOfMonth), monthString = String.valueOf(monthOfYear + 1);
        if(valueOfday.length() <= 1){

            valueOfday = "0" + valueOfday;
        }
        if(monthString.length() <= 1){
            monthString = "0" + monthString;
        }


        selectCustomDate.setText(valueOfday + "-"+ monthString+ "-" + year);
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
