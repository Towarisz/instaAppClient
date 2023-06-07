package api.model;

public class UserRegister {
    private String name;
    private String lastName;
    private String email;
    private String password;

    public UserRegister(String name, String lastName, String email, String password) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}
