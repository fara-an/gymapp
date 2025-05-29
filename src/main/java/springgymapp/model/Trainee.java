package springgymapp.model;

import java.time.LocalDateTime;

public class Trainee extends User {

   private LocalDateTime birthday;
   private String address;
   private   long userId;

   // 1
    private Training training;

    public Trainee(String firstName, String lastName, String userName, String password, boolean isActive,LocalDateTime birthday, String address,long userId ) {
        super(firstName, lastName, userName, password, isActive);
        this.birthday=birthday;
        this.address=address;
        this.userId=userId;
    }



    public LocalDateTime getBirthday() {
        return birthday;
    }

    public String getAddress() {
        return address;
    }

    public long getUserId() {
        return userId;
    }

    public Training getTraining() {
        return training;
    }

    public void setTraining(Training training) {
        this.training = training;
    }

    @Override
    public String toString() {
        return "Trainee{" +
                "birthday=" + birthday +
                ", address='" + address + '\'' +
                ", userId=" + userId +
                ", training=" + training.getTrainingName() +
                '}';
    }
}
