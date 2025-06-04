package epamlab.spring.gymapp.model;

public abstract class User {

    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private boolean isActive;
    private   long userId;


    public User(String firstName, String lastName, String userName, String password, boolean isActive, long userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.isActive = isActive;
        this.userId=userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActive() {
        return isActive;
    }

    public long getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", isActive=" + isActive +
                ", userId=" + userId +
                '}';
    }

}
