package epamlab.spring.gymapp.util;



public class UsernameGenerator {



    public  static String generateUsername(String firstName, String lastName, boolean usernameExists ) {

        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int suffix = 1;

        while (usernameExists) {
            username = baseUsername + suffix;
            suffix++;
        }


        return username;
    }


}
