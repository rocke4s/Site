package org.example.model;

public class NewTask {
    private String Importance;
    private String contentTask;
    private String uidUser;
    private String nameTask;

    public String getImportance() {
        return Importance;
    }

    public void setImportance(String Importance) {
        this.Importance = Importance;
    }

    public String getContentTask() {
        return contentTask;
    }

    public void setContentTask(String contentTask) {
        this.contentTask = contentTask;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }

    public String getNameTask() {
        return nameTask;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }
}
