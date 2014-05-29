package covisoft.android.managebackgroundtask.listener;

import covisoft.android.managebackgroundtask.TaskResponse;
import covisoft.android.managebackgroundtask.Task;

public interface ITaskListenter {
	/*
	 * Called when a request has been finished without cancelled
	 */
	public void onComplete(Task task, TaskResponse data);
}
