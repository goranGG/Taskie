package ada.osc.taskie.model;

public enum TaskPriority {
	LOW, MEDIUM, HIGH;

	public TaskPriority next(){
		TaskPriority taskPriority[] = TaskPriority.values();
		return taskPriority[(this.ordinal()+1) % taskPriority.length];
	}
}
