package epamlab.spring.gymapp.services.serviceInterfaces;

public interface UsernamePasswordGeneration {

     String generateUsername(String firstName, String lastName, long numberOfSameNames);
     String generatePassword();
}
