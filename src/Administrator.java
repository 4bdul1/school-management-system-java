import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileOutputStream;

public class Administrator {

    private School school;
    private Random random;
    private int studentCounter = 1;
    private int instructorCounter = 1;

    public Administrator(School school) {
        this.school = school;
        this.random = new Random();
    }

    public void saveSimulation(String filename) {
        String saveFile = filename + ".save.txt";
        try {
            PrintStream ps = new PrintStream(new FileOutputStream(saveFile, false));

            ps.println("school:" + school.getName());

            for (Subject subject : school.getSubjects()) {
                ps.println("subject:" + subject.getDescription() + "," + subject.getID() + "," + subject.getSpecialism() + "," + subject.getDuration());
            }

            for (Student student : school.getStudents()) {
                StringBuilder line = new StringBuilder();
                line.append("student:").append(student.getName()).append(",").append(student.getGender()).append(",").append(student.getAge());
                for (int certId : student.getCertificates()) {
                    line.append(",cert:").append(certId);
                }
                ps.println(line.toString());
            }

            for (Instructor instructor : school.getInstructors()) {
                String type;
                if (instructor instanceof GUITrainer) type = "GUITrainer";
                else if (instructor instanceof OOTrainer) type = "OOTrainer";
                else if (instructor instanceof Demonstrator) type = "Demonstrator";
                else type = "Teacher";
                ps.println(type + ":" + instructor.getName() + "," + instructor.getGender() + "," + instructor.getAge());
            }

            for (Course course : school.getCourses()) {
                StringBuilder line = new StringBuilder();
                line.append("course:").append(course.getSubject().getID()).append(",").append(course.getDaysUntilStarts()).append(",").append(course.getDaysToRun());

                if (course.getInstructor() != null) {
                    line.append(",instructor:").append(course.getInstructor().getName());
                }

                for (Student s : course.getStudents()) {
                    if (s != null) {
                        line.append(",student:").append(s.getName());
                    }
                }
                ps.println(line.toString());
            }

            ps.println("counters:" + studentCounter + "," + instructorCounter);
            ps.close();
            System.out.println("Simulation saved to " + saveFile);

        } catch (IOException e) {
            System.out.println("Error saving simulation: " + e.getMessage());
        }
    }

    public void loadSave(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                parseResumeLine(line);
            }
        } catch (IOException e) {
            System.out.println("Error loading save file: " + e.getMessage());
        }
    }

    private void parseResumeLine(String line) {
        int colonIndex = line.indexOf(':');
        if (colonIndex == -1) {
            System.out.println("Malformed save line: " + line);
            return;
        }

        String type = line.substring(0, colonIndex).trim();
        String data = line.substring(colonIndex + 1).trim();

        try {
            switch (type) {
                case "school":
                    this.school = new School(data);
                    break;
                case "subject":
                    parseSubjectResume(data);
                    break;
                case "student":
                    parseStudentResume(data);
                    break;
                case "Teacher":
                case "Demonstrator":
                case "OOTrainer":
                case "GUITrainer":
                    parseInstructorResume(type, data);
                    break;
                case "course":
                    parseCourseResume(data);
                    break;
                case "counters":
                    parseCounters(data);
                    break;
                default:
                    System.out.println("Unknown save line type: " + type);
            }
        } catch (SimulationStateException e) {
            System.out.println("Error loading save: " + e.getMessage());
        }
    }

    private void parseSubjectResume(String data) throws SimulationStateException {
        String[] parts = data.split(",");
        if (parts.length != 4) {
            throw new SimulationStateException("Malformed subject in save file: " + data);
        }
        try {
            String description = parts[0].trim();
            int id = Integer.parseInt(parts[1].trim());
            int specialism = Integer.parseInt(parts[2].trim());
            int duration = Integer.parseInt(parts[3].trim());
            this.school.addSubject(new Subject(id, specialism, duration, description));
        } catch (NumberFormatException e) {
            throw new SimulationStateException("Non-numeric value in subject save data: " + data);
        }
    }

    private void parseStudentResume(String data) throws SimulationStateException {
        String[] parts = data.split(",");
        if (parts.length < 3) {
            throw new SimulationStateException("Malformed student in save file: " + data);
        }
        try {
            String name = parts[0].trim();
            char gender = parts[1].trim().charAt(0);
            int age = Integer.parseInt(parts[2].trim());
            Student student = new Student(name, gender, age);

            for (int i = 3; i < parts.length; i++) {
                if (parts[i].startsWith("cert:")) {
                    int certId = Integer.parseInt(parts[i].substring(5).trim());
                    for (Subject subject : this.school.getSubjects()) {
                        if (subject.getID() == certId) {
                            student.graduate(subject);
                            break;
                        }
                    }
                }
            }
            this.school.addStudent(student);
        } catch (NumberFormatException e) {
            throw new SimulationStateException("Non-numeric value in student save data: " + data);
        }
    }

    private void parseInstructorResume(String type, String data) throws SimulationStateException {
        String[] parts = data.split(",");
        if (parts.length < 3) {
            throw new SimulationStateException("Malformed instructor in save file: " + data);
        }
        try {
            String name = parts[0].trim();
            char gender = parts[1].trim().charAt(0);
            int age = Integer.parseInt(parts[2].trim());

            Instructor instructor;
            switch (type) {
                case "Teacher":
                    instructor = new Teacher(name, gender, age);
                    break;
                case "Demonstrator":
                    instructor = new Demonstrator(name, gender, age);
                    break;
                case "OOTrainer":
                    instructor = new OOTrainer(name, gender, age);
                    break;
                case "GUITrainer":
                    instructor = new GUITrainer(name, gender, age);
                    break;
                default:
                    throw new SimulationStateException("Unknown instructor type in save file: " + type);
            }
            this.school.addInstructor(instructor);
        } catch (NumberFormatException e) {
            throw new SimulationStateException("Non-numeric value in instructor save data: " + data);
        }
    }

    private void parseCourseResume(String data) throws SimulationStateException {
        String[] parts = data.split(",");
        if (parts.length < 3) {
            throw new SimulationStateException("Malformed course in save file: " + data);
        }
        try {
            int subjectId = Integer.parseInt(parts[0].trim());
            int daysUntilStarts = Integer.parseInt(parts[1].trim());
            int daysToRun = Integer.parseInt(parts[2].trim());

            Subject subject = null;
            for (Subject s : this.school.getSubjects()) {
                if (s.getID() == subjectId) {
                    subject = s;
                    break;
                }
            }
            if (subject == null) {
                throw new SimulationStateException("Subject not found for course with ID: " + subjectId);
            }

            Course course = new Course(subject, daysUntilStarts);
            course.setDaysToRun(daysToRun);

            for (int i = 3; i < parts.length; i++) {
                String part = parts[i].trim();
                if (part.startsWith("instructor:")) {
                    String instructorName = part.substring(11).trim();
                    for (Instructor instructor : this.school.getInstructors()) {
                        if (instructor.getName().equals(instructorName)) {
                            course.setInstructor(instructor);
                            instructor.assignCourse(course);
                            break;
                        }
                    }
                } else if (part.startsWith("student:")) {
                    String studentName = part.substring(8).trim();
                    for (Student student : this.school.getStudents()) {
                        if (student.getName().equals(studentName)) {
                            course.enrolStudent(student);
                            break;
                        }
                    }
                }
            }
            this.school.addCourse(course);
        } catch (NumberFormatException e) {
            throw new SimulationStateException("Non-numeric value in course save data: " + data);
        }
    }

    private void parseCounters(String data) throws SimulationStateException {
        String[] parts = data.split(",");
        if (parts.length != 2) {
            throw new SimulationStateException("Malformed counters in save file: " + data);
        }
        try {
            studentCounter = Integer.parseInt(parts[0].trim());
            instructorCounter = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new SimulationStateException("Non-numeric counter values: " + data);
        }
    }

    public void loadConfiguration(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    parseConfigLine(line);
                } catch (InvalidConfigurationException e) {
                    System.out.println("Configuration error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading configuration file: " + e.getMessage());
        }
    }

    private void parseConfigLine(String line) throws InvalidConfigurationException {
        int colonIndex = line.indexOf(':');
        if (colonIndex == -1) {
            throw new InvalidConfigurationException("Malformed line (no colon found): " + line);
        }

        String type = line.substring(0, colonIndex).trim();
        String data = line.substring(colonIndex + 1).trim();

        switch (type) {
            case "school":
                this.school = new School(data);
                break;
            case "subject":
                parseSubjectConfig(data);
                break;
            case "student":
                parseStudentConfig(data);
                break;
            case "Teacher":
            case "Demonstrator":
            case "OOTrainer":
            case "GUITrainer":
                parseInstructorConfig(type, data);
                break;
            default:
                throw new InvalidConfigurationException("Unknown entity type: " + type);
        }
    }

    private void parseSubjectConfig(String data) throws InvalidConfigurationException {
        String[] parts = data.split(",");
        if (parts.length != 4) {
            throw new InvalidConfigurationException("Malformed subject line: " + data);
        }
        try {
            String description = parts[0].trim();
            int id = Integer.parseInt(parts[1].trim());
            int specialism = Integer.parseInt(parts[2].trim());
            int duration = Integer.parseInt(parts[3].trim());
            this.school.addSubject(new Subject(id, specialism, duration, description));
        } catch (NumberFormatException e) {
            throw new InvalidConfigurationException("Non-numeric value in subject line: " + data);
        }
    }

    private void parseStudentConfig(String data) throws InvalidConfigurationException {
        String[] parts = data.split(",");
        if (parts.length != 3) {
            throw new InvalidConfigurationException("Malformed student line: " + data);
        }
        try {
            String name = parts[0].trim();
            char gender = parts[1].trim().charAt(0);
            int age = Integer.parseInt(parts[2].trim());
            this.school.addStudent(new Student(name, gender, age));
        } catch (NumberFormatException e) {
            throw new InvalidConfigurationException("Non-numeric age in student line: " + data);
        }
    }

    private void parseInstructorConfig(String type, String data) throws InvalidConfigurationException {
        String[] parts = data.split(",");
        if (parts.length != 3) {
            throw new InvalidConfigurationException("Malformed instructor line: " + data);
        }
        try {
            String name = parts[0].trim();
            char gender = parts[1].trim().charAt(0);
            int age = Integer.parseInt(parts[2].trim());

            Instructor instructor;
            switch (type) {
                case "Teacher":
                    instructor = new Teacher(name, gender, age);
                    break;
                case "Demonstrator":
                    instructor = new Demonstrator(name, gender, age);
                    break;
                case "OOTrainer":
                    instructor = new OOTrainer(name, gender, age);
                    break;
                case "GUITrainer":
                    instructor = new GUITrainer(name, gender, age);
                    break;
                default:
                    throw new InvalidConfigurationException("Unknown instructor type: " + type);
            }
            this.school.addInstructor(instructor);
        } catch (NumberFormatException e) {
            throw new InvalidConfigurationException("Non-numeric age in instructor line: " + data);
        }
    }

    private void admitStudents() {
        int numStudents = random.nextInt(3);
        for (int i = 0; i < numStudents; i++) {
            String name = "Student" + studentCounter++;
            char gender = random.nextBoolean() ? 'M' : 'F';
            int age = 18 + random.nextInt(40);
            this.school.addStudent(new Student(name, gender, age));
        }
    }

    private void admitInstructors() {
        String name;
        char gender;
        int age;

        if (random.nextInt(100) < 20) {
            name = "Teacher" + instructorCounter++;
            gender = random.nextBoolean() ? 'M' : 'F';
            age = 25 + random.nextInt(35);
            this.school.addInstructor(new Teacher(name, gender, age));
        }
        if (random.nextInt(100) < 10) {
            name = "Demonstrator" + instructorCounter++;
            gender = random.nextBoolean() ? 'M' : 'F';
            age = 25 + random.nextInt(35);
            this.school.addInstructor(new Demonstrator(name, gender, age));
        }
        if (random.nextInt(100) < 5) {
            name = "OOTrainer" + instructorCounter++;
            gender = random.nextBoolean() ? 'M' : 'F';
            age = 25 + random.nextInt(35);
            this.school.addInstructor(new OOTrainer(name, gender, age));
        }
        if (random.nextInt(100) < 5) {
            name = "GUITrainer" + instructorCounter++;
            gender = random.nextBoolean() ? 'M' : 'F';
            age = 25 + random.nextInt(35);
            this.school.addInstructor(new GUITrainer(name, gender, age));
        }
    }

    private void endOfDayUpdates() {
        ArrayList<Instructor> instructorsToRemove = new ArrayList<>();
        for (Instructor instructor : this.school.getInstructors()) {
            if (instructor.getAssignedCourse() == null) {
                if (random.nextInt(100) < 20) {
                    instructorsToRemove.add(instructor);
                }
            }
        }
        for (Instructor instructor : instructorsToRemove) {
            this.school.removeInstructor(instructor);
        }

        ArrayList<Student> studentsToRemove = new ArrayList<>();
        for (Student student : this.school.getStudents()) {
            boolean isEnrolled = false;
            for (Course course : this.school.getCourses()) {
                for (Student s : course.getStudents()) {
                    if (s == student) {
                        isEnrolled = true;
                        break;
                    }
                }
            }
            boolean hasAll = true;
            for (Subject subject : this.school.getSubjects()) {
                if (!student.hasCertificate(subject)) {
                    hasAll = false;
                    break;
                }
            }
            if (hasAll) {
                studentsToRemove.add(student);
            } else if (!isEnrolled && random.nextInt(100) < 5) {
                studentsToRemove.add(student);
            }
        }
        for (Student student : studentsToRemove) {
            this.school.removeStudent(student);
        }
    }

    private void printReport(int day) {
        System.out.println("\n========== Day " + day + " Report ==========");
        System.out.println(this.school.toString());
    }

    public void run(int days) {
        for (int day = 1; day <= days; day++) {
            System.out.println("\n--- Simulating Day " + day + " ---");
            admitStudents();
            admitInstructors();
            this.school.aDayAtSchool();
            endOfDayUpdates();
            printReport(day);
        }
    }

    public void run() {
        int day = 1;
        while (true) {
            System.out.println("\n--- Simulating Day " + day + " ---");
            admitStudents();
            admitInstructors();
            this.school.aDayAtSchool();
            endOfDayUpdates();
            printReport(day);
            day++;
        }
    }

    public static void main(String[] args) {
        Administrator admin;

        if (args.length >= 2) {
            String filename = args[0];
            int days;
            try {
                days = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number of days: " + args[1]);
                return;
            }

            School school = new School("Default School");
            admin = new Administrator(school);

            if (filename.endsWith(".save.txt")) {
                admin.loadSave(filename);
            } else {
                admin.loadConfiguration(filename);
            }
            admin.run(days);

        } else if (args.length == 1) {
            try {
                int days = Integer.parseInt(args[0]);
                School school = new School("ECS Java Training School");
                school.addSubject(new Subject(1, 1, 5, "Basics"));
                school.addSubject(new Subject(2, 2, 2, "Lab 1"));
                school.addSubject(new Subject(3, 1, 3, "Arrays"));
                school.addSubject(new Subject(4, 1, 1, "Algorithms"));
                school.addSubject(new Subject(5, 1, 3, "Testing and Debugging"));
                school.addSubject(new Subject(6, 2, 2, "Lab 2"));
                school.addSubject(new Subject(7, 3, 6, "Object-Oriented 1"));
                school.addSubject(new Subject(8, 3, 7, "Object-Oriented 2"));
                school.addSubject(new Subject(9, 2, 3, "Lab 3"));
                school.addSubject(new Subject(10, 4, 5, "Graphics"));
                school.addSubject(new Subject(11, 4, 2, "Controllers"));
                school.addSubject(new Subject(12, 2, 3, "Lab 4"));
                admin = new Administrator(school);
                admin.run(days);
            } catch (NumberFormatException e) {
                System.out.println("Usage: java Administrator [configFile.txt] [days]");
            }

        } else {
            School school = new School("ECS Java Training School");
            school.addSubject(new Subject(1, 1, 5, "Basics"));
            school.addSubject(new Subject(2, 2, 2, "Lab 1"));
            school.addSubject(new Subject(3, 1, 3, "Arrays"));
            school.addSubject(new Subject(4, 1, 1, "Algorithms"));
            school.addSubject(new Subject(5, 1, 3, "Testing and Debugging"));
            school.addSubject(new Subject(6, 2, 2, "Lab 2"));
            school.addSubject(new Subject(7, 3, 6, "Object-Oriented 1"));
            school.addSubject(new Subject(8, 3, 7, "Object-Oriented 2"));
            school.addSubject(new Subject(9, 2, 3, "Lab 3"));
            school.addSubject(new Subject(10, 4, 5, "Graphics"));
            school.addSubject(new Subject(11, 4, 2, "Controllers"));
            school.addSubject(new Subject(12, 2, 3, "Lab 4"));
            admin = new Administrator(school);
            admin.run(10);
        }
    }
}