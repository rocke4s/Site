package org.example.model;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Tasks {

    @SerializedName("TaskContent")
    @Expose
    private String taskContent;
    @SerializedName("TaskContentLVR")
    @Expose
    private String taskContentLVR;
    @SerializedName("TaskData")
    @Expose
    private String taskData;
    @SerializedName("TaskDataDone")
    @Expose
    private String taskDataDone;
    @SerializedName("TaskDeadline")
    @Expose
    private String taskDeadline;
    @SerializedName("TaskEmployee")
    @Expose
    private String taskEmployee;
    @SerializedName("TaskId")
    @Expose
    private String taskId;
    @SerializedName("TaskImportance")
    @Expose
    private String taskImportance;
    @SerializedName("TaskIntensity")
    @Expose
    private String taskIntensity;
    @SerializedName("TaskNumber")
    @Expose
    private String taskNumber;
    @SerializedName("TaskPartner")
    @Expose
    private String taskPartner;
    @SerializedName("TaskStatus")
    @Expose
    private String taskStatus;
    @SerializedName("TaskUrl")
    @Expose
    private String taskUrl;
    @SerializedName("TypeTask")
    @Expose
    private String typeTask;

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public String getTaskContentLVR() {
        return taskContentLVR;
    }

    public void setTaskContentLVR(String taskContentLVR) {
        this.taskContentLVR = taskContentLVR;
    }

    public String getTaskData() {
        return taskData;
    }

    public void setTaskData(String taskData) {
        this.taskData = taskData;
    }

    public String getTaskDataDone() {
        return taskDataDone;
    }

    public void setTaskDataDone(String taskDataDone) {
        this.taskDataDone = taskDataDone;
    }

    public String getTaskDeadline() {
        return taskDeadline;
    }

    public void setTaskDeadline(String taskDeadline) {
        this.taskDeadline = taskDeadline;
    }

    public String getTaskEmployee() {
        return taskEmployee;
    }

    public void setTaskEmployee(String taskEmployee) {
        this.taskEmployee = taskEmployee;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskImportance() {
        return taskImportance;
    }

    public void setTaskImportance(String taskImportance) {
        this.taskImportance = taskImportance;
    }

    public String getTaskIntensity() {
        return taskIntensity;
    }

    public void setTaskIntensity(String taskIntensity) {
        this.taskIntensity = taskIntensity;
    }

    public String getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    public String getTaskPartner() {
        return taskPartner;
    }

    public void setTaskPartner(String taskPartner) {
        this.taskPartner = taskPartner;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskUrl() {
        return taskUrl;
    }

    public void setTaskUrl(String taskUrl) {
        this.taskUrl = taskUrl;
    }

    public String getTypeTask() {
        return typeTask;
    }

    public void setTypeTask(String typeTask) {
        this.typeTask = typeTask;
    }

}