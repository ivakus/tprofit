package ru.c0ner.tprofit.datashema;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class dataObject_Status {
    public dataObject_Status() {
        this.person = new ArrayList<Person>();
    }
    @SerializedName("all_person")
    @Expose
    private Integer allPerson;
    @SerializedName("manager_name")
    @Expose
    private String managerName;
    @SerializedName("mamager")
    @Expose
    private Integer mamager;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("object_id")
    @Expose
    private Integer objectId;
    @SerializedName("person")
    @Expose
    private ArrayList<Person> person = null;
    @SerializedName("today_person")
    @Expose
    private Integer todayPerson;

    public Integer getAllPerson() {
        return allPerson;
    }

    public void setAllPerson(Integer allPerson) {
        this.allPerson = allPerson;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public Integer getMamager() {
        return mamager;
    }

    public void setMamager(Integer mamager) {
        this.mamager = mamager;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public ArrayList<Person> getPerson() {
        return person;
    }

    public void setPerson(ArrayList<Person> person) {
        this.person = person;
    }

    public Integer getTodayPerson() {
        return todayPerson;
    }

    public void setTodayPerson(Integer todayPerson) {
        this.todayPerson = todayPerson;
    }

}