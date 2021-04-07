package in.thegforest.chatting.Main.Notification;

public class Data {
    private String user;
    private String body;
    private int icon;
    private String title;
    private String sented;
    public Data(String user, String body, int icon, String title, String sented) {
        this.user = user;
        this.body = body;
        this.icon = icon;
        this.title = title;
        this.sented = sented;
    }

    public Data() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }
}
