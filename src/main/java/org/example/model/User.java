package org.example.model;

public class User {
    private String username;
    private String password;
    private String uidUser;
    private String name;
    private boolean auth = false;
    private String typeUser;

    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void exit() {
        setName("");
        setUidUser("");
        setUsername("");
        setPassword("");
        setTypeUser("");
    }
    public void forgetUser()
    {
        setName("");
        setPassword("");
        setAuth(false);
    }

    @Override
    public String toString() {
        return "{" +
                "uidUser='" + uidUser + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
