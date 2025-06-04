package epamlab.spring.gymapp.model;

public class Trainer extends User {

    private String specialization;

    private TrainingType trainingType;

    private Training training;

    public Trainer(String firstName, String lastName, String userName, String password, boolean isActive, String specialization, TrainingType trainingType, Training training, long userID) {
        super(firstName, lastName, userName, password, isActive, userID);
        this.specialization = specialization;
        this.trainingType = trainingType;
        this.training = training;
    }


    public String getSpecialization() {
        return specialization;
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
        return "Trainer{" + super.toString() +
                "specialization='" + specialization + '\'' +
                ", trainingType=" + trainingType +
                ", training=" + training.getId() +
                '}';
    }


}
