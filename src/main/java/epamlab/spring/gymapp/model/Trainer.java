package epamlab.spring.gymapp.model;

public class Trainer extends User {

    private String specialization;

    private TrainingType trainingType;


    public Trainer(String firstName, String lastName, String userName, String password, boolean isActive, String specialization, TrainingType trainingType,  long userID) {
        super(firstName, lastName, userName, password, isActive, userID);
        this.specialization = specialization;
        this.trainingType = trainingType;
    }


    public String getSpecialization() {
        return specialization;
    }


    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    @Override
    public String toString() {
        return "Trainer{" + super.toString() +
                "specialization='" + specialization + '\'' +
                ", trainingType=" + trainingType +
                '}';
    }


}
