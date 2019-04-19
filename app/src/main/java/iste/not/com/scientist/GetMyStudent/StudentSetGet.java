package iste.not.com.scientist.GetMyStudent;

public class StudentSetGet
{
    String image;
    String  name;
    String number;
    String studentId;

    public StudentSetGet() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public StudentSetGet(String image, String name, String number, String studentId) {
        this.image = image;

        this.name = name;
        this.number = number;
        this.studentId = studentId;
    }
}
