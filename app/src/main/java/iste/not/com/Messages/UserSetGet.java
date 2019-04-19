package iste.not.com.Messages;

public class UserSetGet
{
    String userId;
    String name;
    String major;

    String image;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }



    public UserSetGet() {

    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public UserSetGet(String userId, String name, String major, String Class, String image) {
        this.userId = userId;
        this.name = name;
        this.major = major;

        this.image = image;
    }
}
