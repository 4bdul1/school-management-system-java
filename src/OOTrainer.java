public class OOTrainer extends Instructor{

    public OOTrainer(String name, char gender, int age) {
        super(name, gender, age);
    }
    public boolean canTeach(Subject subject) {
        if (subject.getSpecialism()==1 || subject.getSpecialism() == 2 || subject.getSpecialism()==3) {
            return true;
        } else {
            return false;
        }
    }
}
