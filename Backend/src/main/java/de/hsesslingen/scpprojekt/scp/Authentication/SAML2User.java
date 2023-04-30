package de.hsesslingen.scpprojekt.scp.Authentication;

/**
 * A user created by logging into Keycloak using SAML2
 * @author Jason Patrick Duffy
 */
public class SAML2User {
    private String email;
    private String firstName;
    private String lastName;

    public SAML2User(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }

}
