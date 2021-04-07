package in.thegforest.chatting;

public class Users {
    String name;
    String email;
    String password;
    String phone;
    Boolean verified;
    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    String profile="https://firebasestorage.googleapis.com/v0/b/fir-app-8334e.appspot.com/o/images%2Fimg_323984.png?alt=media&token=b7289541-3fac-481a-af19-1e3199b1424e";

    public Users(String name, String email, String password, String phone,Boolean verified) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.verified=verified;
    }

    public Users() {

    }

    public Users(String email, String password) {
        this.email = email;
        this.password = password;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
