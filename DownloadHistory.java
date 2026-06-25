import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Saves and reads a log of past downloads stored in a text file on disk
public class DownloadHistory {

    private String filePath; // path to the history text file

    // Takes the file path where history will be saved
    DownloadHistory(String filePath) {
        this.filePath = filePath;
    }

    // Appends a new entry to the history file in the format: date | format | url
    public void add(String url, String format) throws IOException {
        FileWriter fw = new FileWriter(filePath, true); // true = append mode, keeps existing entries
        fw.write(LocalDate.now() + " | " + format + " | " + url + "\n");
        fw.close();
    }

    // Reads all lines from the history file and returns them as a list
    public List<String> getAll() throws IOException {
        List<String> entries = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return entries; // return empty list if no downloads yet
        BufferedReader reader = new BufferedReader(new FileReader(file)); // FileReader opens the file, BufferedReader lets us read line by line
        String line;
        while ((line = reader.readLine()) != null) { // readLine() returns null when there are no more lines
            entries.add(line);
        }
        reader.close();
        return entries;
    }

}
