package de.hsesslingen.scpprojekt.scp.Authentication;

/**
 * A user created by logging into Keycloak using SAML2
 * @author Jason Patrick Duffy
 */
public class SAMLUser {
    private String email;
    private String firstName;
    private String lastName;

    public SAMLUser(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
