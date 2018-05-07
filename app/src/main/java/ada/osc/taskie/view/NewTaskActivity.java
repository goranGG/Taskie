package ada.osc.taskie.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import ada.osc.taskie.R;
import ada.osc.taskie.TaskRepository;
import ada.osc.taskie.model.Task;
import ada.osc.taskie.model.TaskPriority;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "";
    @BindView(R.id.edittext_newtask_title)
    EditText mTitleEntry;
    @BindView(R.id.edittext_newtask_description)
    EditText mDescriptionEntry;
    @BindView(R.id.spinner_newtask_priority)
    Spinner mPriorityEntry;
    @BindView(R.id.textview_newtask_duedate)
    TextView mDueDate;
    @BindView(R.id.button_newtask_datepicker)
    Button mDatePicker;
    Calendar calendar = Calendar.getInstance();
    private TaskRepository mTaskRepository = TaskRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        ButterKnife.bind(this);
        setUpSpinnerSource();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        // if updating
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mTitleEntry.setText(getIntent().getStringExtra("taskTitle"));
            mDescriptionEntry.setText(getIntent().getStringExtra("taskDescription"));
            mDueDate.setText(getIntent().getStringExtra("taskDueDate"));

            //the value you want the position for @TODO change spinner?
            String myString = getIntent().getStringExtra("taskPriority");
            Log.i(TAG, "\n myString " + myString);
            //cast to an ArrayAdapter
            ArrayAdapter myAdap = (ArrayAdapter) mPriorityEntry.getAdapter();
            int spinnerPosition = myAdap.getPosition(myString);
            Log.i(TAG, "\n position " + spinnerPosition);

            //set the default according to value
            mPriorityEntry.setSelection(spinnerPosition);
        } else {
            mDueDate.setText(dateFormat.format(calendar.getTime()));
        }
    }


    private void setUpSpinnerSource() {
        mPriorityEntry.setAdapter(
                new ArrayAdapter<TaskPriority>(
                        this, android.R.layout.simple_list_item_1, TaskPriority.values()
                )
        );
        mPriorityEntry.setSelection(0);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance().format(calendar.getTime());
        mDueDate.setText(currentDateString);
    }


    @OnClick(R.id.button_newtask_datepicker)
    public void datePickerFragment() {
        android.support.v4.app.DialogFragment datePickerDialog = new DatePickerFragment();
        datePickerDialog.show(getSupportFragmentManager(), "Date picker");
    }

    @OnClick(R.id.imagebutton_newtask_savetask)
    public void saveTask() {
        Bundle extras = getIntent().getExtras();

        String title = mTitleEntry.getText().toString();
        String description = mDescriptionEntry.getText().toString();
        String dueDate = mDueDate.getText().toString();
        TaskPriority priority = (TaskPriority) mPriorityEntry.getSelectedItem();


        if (extras == null) {
            try {
                if (!title.trim().equals("")) {
                    Task newTask = new Task(title, description, priority, dueDate);
                    Intent saveTaskIntent = new Intent(this, TasksActivity.class);
                    saveTaskIntent.putExtra(TasksActivity.EXTRA_TASK, newTask);
                    setResult(RESULT_OK, saveTaskIntent);
                    finish();

                } else {
                    throw new Exception("Title cant be empty");
                }
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // load task and update
            List<Task> tasks = getTasksFromTasksRepository();
            int taskIndex = -1;
            // get single task
            for (Task task : tasks) {
                for (int position = 0; position < tasks.size(); position++) {
                    if (task.getId() == getIntent().getIntExtra("taskId", -1)) {
                        taskIndex = position;
                    }
                }
            }
            Task task = getTasksFromTasksRepository().get(taskIndex);
            task.setTitle(title);
            task.setDescription(description);
            task.setmDueDate(dueDate);
            task.setPriority(priority);
//            finish();
            Intent saveTaskIntent = new Intent(this, TasksActivity.class);
            saveTaskIntent.putExtra(TasksActivity.EXTRA_TASK, task);
            setResult(RESULT_OK, saveTaskIntent);
            finish();

        }

    }

    private List<Task> getTasksFromTasksRepository() {
        return mTaskRepository.getTasks();
    }
}
