public class Demonstrator extends Instructor{

    public Demonstrator(String name, char gender, int age) {
        super(name, gender, age);
    }
    public boolean canTeach(Subject subject) {
        if (subject.getSpecialism()==2) {
            return true;
        } else {
            return false;
        }
    }
}
