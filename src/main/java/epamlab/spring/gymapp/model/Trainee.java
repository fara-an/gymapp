package epamlab.spring.gymapp.model;

import java.time.LocalDateTime;

public class Trainee extends User {

    private LocalDateTime birthday;
    private String address;

    public Trainee(String firstName, String lastName, String userName, String password, boolean isActive, LocalDateTime birthday, String address, long userId) {
        super(firstName, lastName, userName, password, isActive, userId);
        this.birthday = birthday;
        this.address = address;
    }


    public LocalDateTime getBirthday() {
        return birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Trainee{" + super.toString() +
                "birthday=" + birthday +
                ", address='" + address + '\'' +
                '}';
    }
}
