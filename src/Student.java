import java.util.ArrayList;

public class Student extends Person{
    private ArrayList<Integer> certificates = new ArrayList<Integer>();

    public Student(String name, char gender, int age) {
        super(name, gender, age);
    }
    public void graduate(Subject subject) {
        certificates.add(subject.getID());
    }
    public ArrayList<Integer> getCertificates() {
        return certificates;
    }
    public boolean hasCertificate(Subject subject) {
        if (certificates.contains(subject.getID())) {
            return true;
        } else {
            return false;
        }
    }

}
