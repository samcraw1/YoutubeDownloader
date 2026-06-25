// Handles the actual downloading by running yt-dlp as a command-line process
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class Downloader {

    private String url;
    private String outputPath;
    private String format;
    

    // Stores the URL, where to save the file, and whether to download as mp4 or mp3
    Downloader(String url, String outputpath, String format) {
        this.url = url;
        this.outputPath = outputpath;
        this.format = format;
    }

    // Builds and runs the yt-dlp command, reads its output to update the progress bar
    public void download(JProgressBar progressBar, JLabel progressLabel) throws Exception {
        ProcessBuilder pb;
        if (format.equals("mp3")) {
            pb = new ProcessBuilder("/opt/homebrew/bin/yt-dlp", "-x", "--audio-format", "mp3", "-o", outputPath + "/%(title)s.%(ext)s", url);
        } else {
            pb = new ProcessBuilder("/opt/homebrew/bin/yt-dlp", "-f", "mp4", "-o", outputPath + "/%(title)s.%(ext)s", url);
        }
        pb.redirectErrorStream(true); // merge stderr into stdout so we catch all output
        Process process = pb.start();

        // read yt-dlp output line by line
        java.io.BufferedReader reader = new java.io.BufferedReader(
            new java.io.InputStreamReader(process.getInputStream())
        );
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("[download]") && line.contains("%")) {
                // line looks like: [download]  45.3% of 10.00MiB at 1.50MiB/s
                String trimmed = line.trim().split("%")[0]; // grab everything before the %
                String percentStr = trimmed.substring(trimmed.lastIndexOf(" ") + 1); // grab the last word
                int percent = (int) Double.parseDouble(percentStr);
                progressBar.setValue(percent);
                progressLabel.setText(percent + "%");
            }
        }
        process.waitFor();
    }
}
