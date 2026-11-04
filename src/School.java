import java.util.ArrayList;

public class School {
    private String name;
    private ArrayList<Student> students;
    private ArrayList<Instructor> instructors;
    private ArrayList<Subject> subjects;
    private ArrayList<Course> courses;

    public School(String name) {
        this.name = name;
        students = new ArrayList<Student>();
        instructors = new ArrayList<Instructor>();
        subjects = new ArrayList<Subject>();
        courses = new ArrayList<Course>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }
    public void removeStudent(Student student) {
        students.remove(student);
    }
    public ArrayList<Student> getStudents() {
        return students;
    }
    
    public void addInstructor(Instructor instructor) {
        instructors.add(instructor);
    }
    public void removeInstructor(Instructor instructor) {
        instructors.remove(instructor);
    }
    public ArrayList<Instructor> getInstructors() {
        return instructors;
    }

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }
    public void removeSubject(Subject subject) {
        subjects.remove(subject);
    }
    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public void addCourse(Course course) {
        courses.add(course);
    }
    public void removeCourse(Course course) {
        courses.remove(course);
    }
    public ArrayList<Course> getCourses() {
        return courses;
    }

    public String toString() {
        String result = name + " has " + students.size() + " students.\n";
        result += "It has " + instructors.size() + " instructors.\n";
        result += "It has " + subjects.size() + " subjects.\n";
        result += "It has " + courses.size() + " courses.\n";

        result += "\nCourses:\n";
        for (Course course : courses) {
            result += "  " + course.getSubject().getDescription();
            result += " | Status: " + course.getStatus();
            if (course.hasInstructor()) {
                result += " | Instructor: " + course.getInstructor().getName();
            } else {
                result += " | Instructor: None";
            }
            result += " | Students: ";
            for (Student s : course.getStudents()) {
                if (s != null) {
                    result += s.getName() + " ";
                }
            }
            result += "\n";
        }

        result += "\nStudents:\n";
        for (Student student : students) {
            result += "  " + student.getName() + " | Certificates: " + student.getCertificates() + "\n";
        }

        result += "\nInstructors:\n";
        for (Instructor instructor : instructors) {
            if (instructor.getAssignedCourse() != null) {
                result += "  " + instructor.getName() + " | Course: " + instructor.getAssignedCourse().getSubject().getDescription() + "\n";
            } else {
                result += "  " + instructor.getName() + " | Course: None\n";
            }
        }

        return result;
    }
    public void aDayAtSchool() {
        for (Subject subject : subjects) {
            boolean hasCourse = false;
            for (Course course : courses) {
                if (course.getSubject().getID() == subject.getID()) {
                    hasCourse = true;
                    break;
                }
            }
            if (!hasCourse) {
                Course newCourse = new Course(subject, 2); // starts in 2 days
                courses.add(newCourse);
            }
        }

        for (Course course : courses) {
            if (!course.hasInstructor() && course.getStatus() < 0) {
                for (Instructor instructor : instructors) {
                    if (instructor.getAssignedCourse() == null
                            && instructor.canTeach(course.getSubject())) {
                        if (course.setInstructor(instructor)) {
                            instructor.assignCourse(course);
                            break;
                        }
                    }
                }
            }
        }

        for (Student student : students) {
            boolean enrolled = false;
            for (Course course : courses) {
                // check if student is already in this course
                for (Student s : course.getStudents()) {
                    if (s == student) enrolled = true;
                }
            }
            if (!enrolled) {
                for (Course course : courses) {
                    if (course.getSize() < 3
                            && course.getStatus() < 0  // not started yet
                            && !student.hasCertificate(course.getSubject())) {
                        course.enrolStudent(student);
                        break;
                    }
                }
            }
        }

        for (Course course : courses) {
            course.aDayPasses();
        }

        ArrayList<Course> toRemove = new ArrayList<Course>();
        for (Course course : courses) {
            if (course.getStatus() == 0 || course.isCancelled()) {
                toRemove.add(course);
            }
        }
        courses.removeAll(toRemove);
    }

    public String getName() {
        return name;
    }
}
