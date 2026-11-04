import java.util.ArrayList;

public class Course {
    private Subject subject;
    private int daysUntilStarts;
    private int daysToRun;
    private ArrayList<Student> enrolledStudents;
    private Student[] studentsArray;
    private Instructor instructor;

    public Course(Subject subject, int daysUntilStarts) {
        this.subject = subject;
        this.daysUntilStarts = daysUntilStarts;
        this.daysToRun = subject.getDuration();
        this.enrolledStudents = new ArrayList<Student>();
        this.studentsArray = new Student[3];
    }

    public Subject getSubject() {
        return subject;
    }
    public int getStatus() {
        if (daysUntilStarts > 0) {
            return -daysUntilStarts;
        } else if (daysUntilStarts <= 0 && daysToRun>0) {
            return daysToRun;
        } else {
            return 0;
        }
    }
    public void aDayPasses() {
        if (daysUntilStarts>0) {
            daysUntilStarts -=1;
        } else if (daysToRun>0) {
            daysToRun -=1;
            if (daysToRun==0) {
                for (Student x : enrolledStudents) {
                    x.graduate(subject);
                }
                if (instructor != null) {
                    instructor.unassignCourse();
                    instructor = null;
                }
            }
        }
        if (daysUntilStarts == 0 && isCancelled()) {

            if (instructor != null) {
                instructor.unassignCourse();
                instructor = null;
            }
        }
    }

    public boolean enrolStudent(Student student) {
        if (enrolledStudents.size() < 3 && daysUntilStarts > 0 && !enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
            studentsArray = enrolledStudents.toArray(studentsArray);
            return true;
        } else {
            return false;
        }
    }

    public int getSize() {
        return enrolledStudents.size();
    }
    public Student[] getStudents() {
        return studentsArray;
    }
    public boolean setInstructor(Instructor instructor) {
        if (instructor.canTeach(subject)) {
            this.instructor = instructor;
            return true;
        } else {
            return false;
        }
    }
    public boolean hasInstructor() {
        if (instructor != null) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isCancelled() {
        if ((daysUntilStarts == 0 && daysToRun > 0) && (!this.hasInstructor() || this.getSize() == 0)) {
            return true;
        } else {
            return false;
        }
    }

    public int getDaysUntilStarts() {
        return daysUntilStarts;
    }

    public int getDaysToRun() {
        return daysToRun;
    }

    public void setDaysToRun(int daysToRun) {
        this.daysToRun = daysToRun;
    }

    public Instructor getInstructor() {
        return instructor;
    }

}
