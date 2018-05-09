package ada.osc.taskie.view;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ada.osc.taskie.R;
import ada.osc.taskie.TaskRepository;
import ada.osc.taskie.model.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TasksActivity extends AppCompatActivity {

    private static final String TAG = TasksActivity.class.getSimpleName();
    private static final int REQUEST_NEW_TASK = 10;
    public static final String EXTRA_TASK = "task";
    private static final int REQUEST_UPDATE_TASK = 20;

    TaskRepository mRepository = TaskRepository.getInstance();
    TaskAdapter mTaskAdapter;
    List<Task> tasks = mRepository.getTasks();


    @BindView(R.id.fab_tasks_addNew)
    FloatingActionButton mNewTask;
    @BindView(R.id.recycler_tasks)
    RecyclerView mTasksRecycler;
    @BindView(R.id.sortButton)
    Button mSortButton;

    TaskClickListener mListener = new TaskClickListener() {
        @Override
        public void onClick(Task task) {
            Intent updateTask = new Intent();
            updateTask.setClass(getBaseContext(), NewTaskActivity.class);

            updateTask.putExtra("taskTitle", task.getTitle());
            updateTask.putExtra("taskDescription", task.getDescription());
            updateTask.putExtra("taskDueDate", task.getmDueDate());
            updateTask.putExtra("taskPriority", task.getPriority());
            updateTask.putExtra("taskId", task.getId());
            startActivityForResult(updateTask, REQUEST_UPDATE_TASK);
            updateTasksDisplay();
        }

        @Override
        public void onLongClick(Task task) {
            mRepository.removeTask(task);
            updateTasksDisplay();
        }

        @Override
        public void changePriority(Task task){
            mRepository.changePriority(task);
            updateTasksDisplay();
        }

        @Override
        public void toggleComplete(Task task){
            toastTask(task);
            mRepository.toggleComplete(task);
            updateTasksDisplay();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        ButterKnife.bind(this);
        setUpRecyclerView();
        updateTasksDisplay();

    }

    private void setUpRecyclerView() {

        int orientation = LinearLayoutManager.VERTICAL;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                this,
                orientation,
                false
        );

        RecyclerView.ItemDecoration decoration =
                new DividerItemDecoration(this, orientation);

        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();

        mTaskAdapter = new TaskAdapter(mListener);

        mTasksRecycler.setLayoutManager(layoutManager);
        mTasksRecycler.addItemDecoration(decoration);
        mTasksRecycler.setItemAnimator(animator);
        mTasksRecycler.setAdapter(mTaskAdapter);
    }

    private void updateTasksDisplay() {
        mTaskAdapter.updateTasks(tasks);
        for (Task t : tasks) {
            Log.d(TAG, t.getTitle());
        }
    }

    private void toastTask(Task task) {
        Toast.makeText(
                this,
                task.getTitle() + "\n" + task.isCompleted(),
                Toast.LENGTH_SHORT
        ).show();
    }

    @OnClick(R.id.fab_tasks_addNew)
    public void startNewTaskActivity() {
        Intent newTask = new Intent();
        newTask.setClass(this, NewTaskActivity.class);
        startActivityForResult(newTask, REQUEST_NEW_TASK);
    }

    @OnClick(R.id.sortButton)
    public void sortList(){
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return t2.getPriority().compareTo(t1.getPriority());
            }
        });
        Toast.makeText(
                this,
                tasks.toString(),
                Toast.LENGTH_SHORT
        ).show();
        mTaskAdapter.updateTasks(tasks);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_NEW_TASK && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra(EXTRA_TASK)) {
                Task task = (Task) data.getSerializableExtra(EXTRA_TASK);

                mRepository.saveTask(task);
                updateTasksDisplay();

            }
        }
        else if (requestCode == REQUEST_UPDATE_TASK && resultCode == RESULT_OK){
            updateTasksDisplay();
        }
    }
}
