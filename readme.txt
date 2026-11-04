All parts attempted: 

Section 2 - Subject and Course Classes:
Implemented Subject and Course with getStatus() and aDayPasses().

Section 3 - Student Class:
Implemented Person and Student. Student stores certificate IDs in an ArrayList. Course issues certificates when finished.

Section 4 - Instructor Class:
Implemented abstract Instructor and four subclasses: Teacher, Demonstrator, OOTrainer, GUITrainer. Each implements canTeach() based on specialty.

Section 5 - School Class:
Implemented School with aDayAtSchool() which creates courses, assigns instructors, enrols students, advances and removes courses. toString() returns full school state.

Section 6 - Running the Simulation:
Implemented Administrator with run() and run(int days). Adds students and instructors daily using probability checks.

Section 7 - Configuration File:
Administrator loads school state from a config file.

Section 8 - Saving and Resuming:
saveSimulation() writes full simulation state to .save.txt.

Section 8 - Exception Handling:
InvalidConfigurationException and SimulationStateException handle invalid input, missing values and unknown types.