package it.gammainnovation.ms3administration;


import it.gammainnovation.librarymodel.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.regex.Pattern;

@RestController
public class Controller {

    RestTemplate rt;

    public Controller() {
        this.rt = new RestTemplate();
    }

    /* ######################################## SIGNUP ######################################## */

    @PostMapping("/signup")
    public User signUp(@RequestBody User newUser) {

        User signedUpUser = null;

        /*#################### PARAMETERS CHECKS ####################*/

        if (newUser.getUuid() != null) {
            throw new IllegalArgumentException("ERROR: uuid must be null for new user");
        }

        if (!Pattern.matches("[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]", newUser.getTaxIdCode())) {
            System.err.println("ERROR: wrong taxIdCode format");
            throw new IllegalArgumentException("ERROR: wrong taxIdCode format");
        }

        if (
            newUser.getGender().compareTo("MALE") != 0
            && newUser.getGender().compareTo("FEMALE") != 0
            && newUser.getGender().compareTo("NONBIN") != 0
        ) {
            System.err.println("ERROR: unknown gender");
            throw new IllegalArgumentException("ERROR: unknown gender");
        }

        if (!Pattern.matches("[a-z0-9]+@[a-z0-9]+[.][a-z]{2,3}", newUser.getEmail())) {
            System.err.println("ERROR: wrong email format");
            throw new IllegalArgumentException("ERROR: wrong email format");
        }

        if (!Pattern.matches("[+]?[0-9]{10,15}", newUser.getPhoneNumber())) {
            System.err.println("ERROR: wrong phone number format");
            throw new IllegalArgumentException("ERROR: wrong phone number format");
        }

        /* #################### GEN UUID AND SEND REQUEST ####################*/

        newUser.setUuid(UUID.randomUUID().toString());

        try {
            signedUpUser = rt.postForObject(
                    "http://localhost:8082/signup",
                    newUser,
                    User.class
            );
        } catch (Exception e) {
            System.err.println("[MS3Controller:signUp(User)] ERROR: postForObject failed");
        }

        if (signedUpUser == null) {
            throw new RuntimeException("[MS3Controller:signUp(User)] ERROR: DB update failed");
        }

        return newUser;
    }

    /* ######################################## LOGIN ######################################## */

    @PostMapping("/login")
    public User login(@RequestBody User credentials) {
        User loggedUser;

        System.out.println("[MS3Controller:login] credentials: email " + credentials.getEmail() + "; password " + credentials.getPassword());

        loggedUser = rt.postForObject(
                "http://localhost:8082/login",
                credentials,
                User.class
        );

        return loggedUser;
    }

}
