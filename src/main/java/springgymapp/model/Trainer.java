package springgymapp.model;

public class Trainer extends User {

    private String specialization;
    private long userID;
    private TrainingType trainingType;


    private Training training;

    // No training in the constructor
    public Trainer(String firstName, String lastName, String userName, String password, boolean isActive, String specialization, TrainingType trainingType, Training training, long userID) {
        super(firstName, lastName, userName, password, isActive);
        this.specialization=specialization;
        this.trainingType=trainingType;
        this.training=training;
        this.userID=userID;
    }


    public String getSpecialization() {
        return specialization;
    }

    public long getUserID() {
        return userID;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public Training getTraining() {
        return training;
    }

    public void setTraining(Training training) {
        this.training = training;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "specialization='" + specialization + '\'' +
                ", userID=" + userID +
                ", trainingType=" + trainingType +
                ", training=" + training.getId() +
                '}';
    }
}
