import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class Assignment implements Serializable {
    private String name;
    private LocalDateTime dueDate;
    private int completionTime;

    public Assignment(String name, LocalDateTime dueDate, int completionTime)  {
        this.name = name;
        this.dueDate = dueDate;
        this.completionTime = completionTime;
    }

    public Assignment(String name, String dueDate, int completionTime) throws Exception {
        this.name = name;
        try {
            this.dueDate = LocalDateTime.parse(dueDate);
        } catch (Exception e) {
            throw new Exception("Could not parse dueDate String to LocalDateTime: " + e.getMessage());
        }
        this.completionTime = completionTime;
    }

    public String getName() {
        return this.name;
    }

    public LocalDateTime getDueDate() {
        return this.dueDate;
    }

    public int getCompletionTime() {
        return this.completionTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setDueDate(String dueDate) throws Exception {
        try {
            this.dueDate = LocalDateTime.parse(dueDate);
        } catch (Exception e) {
            throw new Exception("Could not parse dueDate String to LocalDateTime: " + e.getMessage());
        }
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public int getPriority() {
        long minutesUntil = LocalDateTime.now().until(getDueDate(), ChronoUnit.MINUTES);
        double mu = (double) minutesUntil;
        double returnValue = (400000 / (mu + 160)) +
                (5 * Math.sqrt((double) getCompletionTime()));
        return Math.toIntExact(Math.round(returnValue));
    }
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return String.format("%s - %s | Priority score: %d", this.name, this.dueDate.format(formatter), getPriority());
    }
}
