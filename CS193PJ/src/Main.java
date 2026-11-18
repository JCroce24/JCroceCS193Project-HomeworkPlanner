import java.time.LocalDateTime;
import java.io.*;
import java.util.ArrayList;

void main() {
    IO.println("Welcome to JCroce24 Homework Planner!\n");
    ArrayList<Assignment> assignments = startup();

    MainLoop:
    while (true) {
        String userInput = IO.readln("Commands:\n1: View\n2: Add\n3: Remove\n4: Quit\n");

        IO.println();

        if (userInput.equals("1")) {
            assignments = sort(assignments);
            view(assignments);
        } else if (userInput.equals("2")) {
            String nameInput = IO.readln("Input assignment name:\n");

            for (int i = 0; i < assignments.size(); i++) {
                if (assignments.get(i).getName().equals(nameInput)) {
                    IO.println("Duplicate name\n");
                    continue MainLoop;
                }
            }

            int completionTime = -1;

            CompletionTimeInputLoop:
            while (completionTime <= 0) {
                try {
                    completionTime = Integer.parseInt(IO.readln("Enter the estimated time to complete the assignment in minutes:\n"));

                    if (completionTime > 0) {
                        break CompletionTimeInputLoop;
                    } else {
                        IO.println("Completion time must be longer than 0 seconds\n");
                    }
                } catch (NumberFormatException e) {
                    IO.println("Invalid integer value");
                }
            }

            String dateInput = IO.readln("Input due date using the following format:\n" +
                    "November 16th, 2026 at 11:30PM = 11/16/26/23:30\n");
            String[] dateArray = dateInput.split("/", 4);

            try {
                String dateFormatted = String.format("20%s-%s-%sT%s:00",
                        dateArray[2], dateArray[0], dateArray[1], dateArray[3]);
                assignments.add(new Assignment(nameInput, dateFormatted, completionTime));

                IO.println("Assignment Added");
            } catch (Exception e) {
                IO.println("Encountered error while creating assignment:\n" + e.getMessage());
            }

        } else if (userInput.equals("3")) {
            String nameInput = IO.readln("Input the name of the assignment you want to delete:\n");

            for (int i = 0; i < assignments.size(); i++) {
                if (assignments.get(i).getName().equals(nameInput)) {
                    assignments.remove(assignments.indexOf(assignments.get(i)));
                    IO.println("Assignment Deleted\n");
                    continue MainLoop;
                }
            }
        } else if (userInput.equals("4")) {
            shutdown(assignments);
            IO.println("Thank you for using JCroce24 Homework Planner.");
            return;
        } else {
            IO.println("Not a command");
        }

        IO.println();
    }
}

// use a for loop and use i-- to go back when you compare somethign and the latter one
// is larger so that it will keep going back if its ahead of the stuff before e
ArrayList<Assignment> sort(ArrayList<Assignment> assignments) {
    for (int i = 0; i < assignments.size() - 1; i++) {
        if (assignments.get(i).getDueDate().isBefore(LocalDateTime.now())) {
            assignments.remove(i);
        }

        if (assignments.get(i).getPriority() < assignments.get(i+1).getPriority()) {
            Assignment temp = assignments.get(i);
            assignments.remove(i);
            assignments.add(i + 1, temp);
            if (i > 0) {
                i -= 2;
            }
         }
    }
    return assignments;
}

void view(ArrayList<Assignment> assignments) {
    for (int i = 0; i < assignments.size(); i++) {
        IO.println(String.format("%d. %s", (i + 1), assignments.get(i).toString()));
    }
}

@SuppressWarnings("unchecked")
ArrayList<Assignment> startup() {
    ArrayList<Assignment> assignments = new ArrayList<Assignment>();

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("AssignmentsList"))) {
        assignments = (ArrayList<Assignment>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
    }
    return assignments;
}

@SuppressWarnings("unchecked")
public void shutdown(ArrayList<Assignment> assignments) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("AssignmentsList"))) {
        oos.writeObject(assignments);
    } catch (IOException e) {
        e.printStackTrace();
    }
}