public class GUITrainer extends Instructor{

    public GUITrainer(String name, char gender, int age) {
        super(name, gender, age);
    }
    public boolean canTeach(Subject subject) {
        if (subject.getSpecialism()==1 || subject.getSpecialism() == 2 || subject.getSpecialism()==4) {
            return true;
        } else {
            return false;
        }
    }
}
