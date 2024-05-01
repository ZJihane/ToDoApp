package Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Task implements Parcelable {
    private String taskId;
    private String title;
    private String description;
    private Date createdAt;
    private Date dueDate;
    private TaskStatus status;
    private TaskPriority priority;

    private String UID ;

    public Task() {
    }

    public Task(String title, String description, Date dueDate, TaskStatus status, TaskPriority priority, String UID) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
        this.createdAt = new Date();
        this.taskId = generateTaskId();
        this.UID = UID; // Initialize UID
    }

    public Task(String taskId, String title, String description, Date dueDate, TaskStatus status, TaskPriority priority, String UID) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
        this.createdAt = new Date();
        this.UID = UID; // Initialize UID
    }

    // Getters and setters for UID

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }




    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    private String generateTaskId() {
        String uniqueId = java.util.UUID.randomUUID().toString();
        String timestamp = String.valueOf(createdAt.getTime());
        return uniqueId + "_" + timestamp;
    }

    public enum TaskStatus {
        COMPLETED,
        IN_PROGRESS,
        PENDING
    }

    public enum TaskPriority {
        HIGH,
        MEDIUM,
        LOW
    }

    // Parcelable implementation
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(taskId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1L);
        dest.writeLong(dueDate != null ? dueDate.getTime() : -1L);
        dest.writeString(status.name());
        dest.writeString(priority.name());
        dest.writeString(UID); // Write UID to parcel
    }

    // Parcelable implementation (readFromParcel method updated to include UID)
    protected Task(Parcel in) {
        taskId = in.readString();
        title = in.readString();
        description = in.readString();
        createdAt = new Date(in.readLong());
        dueDate = new Date(in.readLong());
        status = TaskStatus.valueOf(in.readString());
        priority = TaskPriority.valueOf(in.readString());
        UID = in.readString(); // Read UID from parcel
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
