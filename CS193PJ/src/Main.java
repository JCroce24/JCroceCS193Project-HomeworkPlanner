import java.time.LocalDateTime;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    ArrayList<Assignment> assignments;
    HashMap<String, Boolean> onOffSettings;

    void main() {
        IO.println("Welcome to JCroce24 Homework Planner!\n");
        startup();

        MainLoop:
        while (true) {
            String userInput = IO.readln("Commands:\n1: View\n2: Add\n3: Remove\n4: Settings\n5: Save & Quit\n");

            if (userInput.equals("1")) {
                //view
                assignments = sort(assignments);
                view(assignments);
                if (onOffSettings.get("AutoSave")) {
                    save();
                }
            } else if (userInput.equals("2")) {
                //add
                String nameInput = IO.readln("Input assignment name (type \"back\" to go back):\n");

                if (nameInput.equalsIgnoreCase("back")) {
                    continue MainLoop;
                }

                for (int i = 0; i < assignments.size(); i++) {
                    if (assignments.get(i).getName().equalsIgnoreCase(nameInput)) {
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

                if (onOffSettings.get("AlwaysDueMidnight")) {
                    String dateInput = IO.readln("Input due date using the following format:\n" +
                            "November 16th, 2026 = 11/16/26\n");
                    String[] dateArray = dateInput.split("/", 3);

                    try {
                        String dateFormatted = String.format("20%s-%s-%sT23:59:99",
                                dateArray[2], dateArray[0], dateArray[1]);
                        assignments.add(new Assignment(nameInput, dateFormatted, completionTime));

                        IO.println("Assignment Added");
                    } catch (Exception e) {
                        IO.println("Encountered error while creating assignment:\n" + e.getMessage());
                    }
                } else {
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
                }

                if (onOffSettings.get("AutoSave")) {
                    save();
                }

            } else if (userInput.equals("3")) {
                //remove
                String nameInput = IO.readln("Input the name of the assignment you want to delete (input \"remove all\" to remove all):\n");

                if (nameInput.equalsIgnoreCase("remove all")) {
                    assignments = new ArrayList<Assignment>();
                    IO.println("All Assignments Deleted");
                    continue MainLoop;
                }

                for (int i = 0; i < assignments.size(); i++) {
                    if (assignments.get(i).getName().equalsIgnoreCase(nameInput)) {
                        assignments.remove(assignments.indexOf(assignments.get(i)));
                        IO.println("Assignment Deleted\n");
                        continue MainLoop;
                    }
                }

                IO.println("Assignment Not Found");

                if (onOffSettings.get("AutoSave")) {
                    save();
                }
            } else if (userInput.equals("4")) {
                //settings
                while (true) {
                    IO.println("Input the name of the setting to switch it from true to false (type \"back\" to go back)");
                    onOffSettings.forEach((key, value) -> System.out.println(key + " - " + value));
                    String settingsInput = IO.readln();

                    if (onOffSettings.get(settingsInput) != null && !settingsInput.equalsIgnoreCase("back")) {
                        onOffSettings.replace(settingsInput, !onOffSettings.get(settingsInput));
                        IO.println();
                    } else if (settingsInput.equalsIgnoreCase("back")) {
                        break;
                    } else {
                        IO.println("Invalid Setting\n");
                    }
                }

            } else if (userInput.equals("5")) {
                //quit
                save();
                IO.println("Thank you for using JCroce24 Homework Planner.");
                return;
            } else {
                //default
                IO.println("Not a command");
            }

            IO.println();
        }
    }

    // use a for loop and use i-- to go back when you compare somethign and the latter one
    // is larger so that it will keep going back if its ahead of the stuff before e
    ArrayList<Assignment> sort(ArrayList<Assignment> assignments) {
        int i = -1;
        while (i < assignments.size() - 2) {
            i++;
            if (assignments.get(i).getDueDate().isBefore(LocalDateTime.now())) {
                assignments.remove(i);
                i--;
            }

            if (assignments.get(i).getPriority() < assignments.get(i + 1).getPriority()) {
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
    void startup() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("AssignmentsList"))) {
            assignments = (ArrayList<Assignment>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Settings"))) {
            onOffSettings = (HashMap<String, Boolean>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("AssignmentsList"))) {
            oos.writeObject(assignments);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Settings"))) {
            oos.writeObject(onOffSettings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}