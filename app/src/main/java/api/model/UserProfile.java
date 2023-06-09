package api.model;

import java.util.List;

public class UserProfile {
    public long id;
    public String name;
    public String lastName;
    private String email;
    private String password;
    private boolean verified;
    private boolean token;
    public List<Long> photos;
}
