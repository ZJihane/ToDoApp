package Model;

public class User {
    private String First_Name;
    private String Last_Name;
    private String Phone_Number;
    private String UID;

    public User() {

    }

    public User(String First_Name, String Last_Name, String Phone_Number, String UID) {
        this.First_Name = First_Name;
        this.Last_Name = Last_Name;
        this.Phone_Number = Phone_Number;
        this.UID = UID;
    }

    public String getFirst_Name() {
        return First_Name;
    }

    public void setFirst_Name(String First_Name) {
        this.First_Name = First_Name;
    }

    public String getLast_Name() {
        return Last_Name;
    }

    public void setLast_Name(String Last_Name) {
        this.Last_Name = Last_Name;
    }

    public String getPhone_Number() {
        return Phone_Number;
    }

    public void setPhone_Number(String Phone_Number) {
        this.Phone_Number = Phone_Number;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
