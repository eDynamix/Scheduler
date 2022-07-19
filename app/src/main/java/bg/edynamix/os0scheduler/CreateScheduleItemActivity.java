package bg.edynamix.os0scheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateScheduleItemActivity extends AppCompatActivity {

    private int mMinute, mHour;
    private int mYear, mMonth, mDay;

    private void datePicker(){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear + 1;
                        mDay = dayOfMonth;
                        timePicker();
                    }
                }, mYear, mMonth, mDay);
        // Hope this works
        SharedPreferences sp = getSharedPreferences("Clock", MODE_PRIVATE);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - sp.getLong("mss", -1));
        datePickerDialog.show();
    }
    private void timePicker(){
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // TODO: Check time (5 minutes from set time)
                        mHour = hourOfDay;
                        mMinute = minute;
                        Button btn = findViewById(R.id.schedulerDatePicker);
                        btn.setText("Pick date" + "\n" + String.format("%02d/%02d/%d @ %02d:%02d", mDay, mMonth, mYear, mHour, mMinute));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule_item);
        if(getIntent().getBooleanExtra("editMode", false)) {
            EditText title = findViewById(R.id.scheduleTitle);
            EditText desc = findViewById(R.id.scheduleDesc);
            Button date = findViewById(R.id.schedulerDatePicker);
            Gson gson = new Gson();
            Type type=new TypeToken<ArrayList<ScheduleItem>>(){}.getType();
            ArrayList<ScheduleItem> items = gson.fromJson(getIntent().getStringExtra("json"), type);
            ScheduleItem item = items.get(getIntent().getIntExtra("pos", -1));
            title.setText(item.getTitle());
            desc.setText(item.getDescription());
            date.setText(date.getText() + "\n" + item.getFormattedDate());
            mYear = item.getYear();
            mMonth = item.getMonth();
            mDay = item.getDay();
            mHour = item.getHour();
            mMinute = item.getMinute();
        }
        // Back button
        findViewById(R.id.schedulerBack).setOnClickListener(view -> {
            Intent intent = new Intent(CreateScheduleItemActivity.this, SchedulerMainActivity.class);
            setResult(999, intent);
            finish();
        });
        // Date picker
        findViewById(R.id.schedulerDatePicker).setOnClickListener(view -> {
            datePicker();
        });
        // Save schedule
        findViewById(R.id.schedulerDone).setOnClickListener(view ->  {
            // Retrieve list
            Gson gson = new Gson();
            Type type=new TypeToken<ArrayList<ScheduleItem>>(){}.getType();
            ArrayList<ScheduleItem> items = gson.fromJson(getIntent().getStringExtra("json"), type);
            EditText title = findViewById(R.id.scheduleTitle);
            EditText desc = findViewById(R.id.scheduleDesc);
            if(items == null) {
                items = new ArrayList<>();
            }
            // Edit item
            if(getIntent().getExtras().size() == 3) {
                int pos = getIntent().getIntExtra("pos", -1);
                items.set(pos, new ScheduleItem(title.getText().toString().trim(),
                          desc.getText().toString().trim(),
                          mYear, mMonth, mDay, mHour, mMinute));
            }
            // Add new item
            else {
                if(mYear == 0 && mMonth == 0 && mDay == 0 && mHour == 0 && mMinute == 0) {
                    datePicker();
                    return;
                }
                else {
                    items.add(new ScheduleItem(title.getText().toString().trim(),
                            desc.getText().toString().trim(),
                            mYear, mMonth, mDay, mHour, mMinute));
                }
            }
            // Serialize and send back
            String json = gson.toJson(items);
            Intent intent = new Intent(CreateScheduleItemActivity.this, SchedulerMainActivity.class);
            intent.putExtra("jsonBack", json);
            if(getIntent().getBooleanExtra("editMode", false)) {
                setResult(1001, intent);
            } else {
                setResult(1000, intent);
            }
            finish();
        });
    }
}