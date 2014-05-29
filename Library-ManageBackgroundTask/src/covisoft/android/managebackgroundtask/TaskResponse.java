package covisoft.android.managebackgroundtask;

public class TaskResponse {
	private TaskAction action;
	private ResultCode resultCode;
	private Object data;
	public TaskResponse(TaskAction action, ResultCode resultCode, Object data) {
		super();
		this.action = action;
		this.resultCode = resultCode;
		this.data = data;
	}
	
	public boolean isSuccess(){
		return (resultCode == ResultCode.Success);
	}
	
	public TaskAction getAction() {
		return action;
	}
	
	public ResultCode getResultCode() {
		return resultCode;
	}
	
	public Object getData() {
		return data;
	}
	
	
	
}
