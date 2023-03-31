package org.example.model;

public class Profile {
    private String uidUser;
    private String name;
    private String uidOrg;
    private String orgName;
    private String Telefon;
    private int debt;

    public String getUidOrg() {
        return uidOrg;
    }

    public void setUidOrg(String uidOrg) {
        this.uidOrg = uidOrg;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getTelefon() {
        return Telefon;
    }

    public void setTelefon(String telefon) {
        Telefon = telefon;
    }

    public int getDebt() {
        return debt;
    }

    public void setDebt(int debt) {
        this.debt = debt;
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
}
