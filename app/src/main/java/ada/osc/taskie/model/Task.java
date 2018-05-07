package ada.osc.taskie.model;

import java.io.Serializable;

public class Task implements Serializable{

	private static int sID = 0;

	int mId;
	private String mTitle;
	private String mDescription;
	private boolean mCompleted;
	private TaskPriority mPriority;
	private String mDueDate;

	public Task(String title, String description, TaskPriority priority, String dueDate) {
		mId = sID++;
		mTitle = title;
		mDescription = description;
		mCompleted = false;
		mPriority = priority;
		mDueDate = dueDate;
	}

	public int getId() {
		return mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}

	public boolean isCompleted() {
		return mCompleted;
	}

	public void setCompleted(boolean completed) {
		mCompleted = completed;
	}

	public String getmDueDate() {
		return mDueDate;
	}

	public void setmDueDate(String mDueDate) {
		this.mDueDate = mDueDate;
	}

	public TaskPriority getPriority() {
		return mPriority;
	}

	public void setPriority(TaskPriority priority) {
		mPriority = priority;
	}

	public void updateTask(Task task){

	}
}
