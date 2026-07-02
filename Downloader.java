// Handles the actual downloading by running yt-dlp as a command-line process
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class Downloader {

    private String url;
    private String outputPath;
    private String format;

    private String quality;
    private boolean playlist;


    // Stores the URL, where to save the file, format, and quality
    Downloader(String url, String outputpath, String format, String quality, boolean playlist) {
        this.url = url;
        this.outputPath = outputpath;
        this.format = format;

        this.quality = quality;
        this.playlist = playlist;
    }

        


    // Builds and runs the yt-dlp command, reads its output to update the progress bar
    public void download(JProgressBar progressBar, JLabel progressLabel) throws Exception {
        java.io.File ytdlp = new java.io.File("/opt/homebrew/bin/yt-dlp");
        if(!ytdlp.exists()) {
        throw new Exception("yt-dlp not found. install it with brew install yt-dlp");
        }
        
        ProcessBuilder pb;
        String playlistFlag = playlist ? "--yes-playlist" : "--no-playlist";
        if (format.equals("mp3")) {
            pb = new ProcessBuilder("/opt/homebrew/bin/yt-dlp", playlistFlag, "-x", "--audio-format", "mp3", "-o", outputPath + "/%(title)s.%(ext)s", url);
        } else {
            // strip the "p" from "1080p" to get just the number for yt-dlp
            String height = quality.replace("p", "");
            pb = new ProcessBuilder("/opt/homebrew/bin/yt-dlp", playlistFlag, "-f", "bestvideo[height<=" + height + "]+bestaudio/best", "-o", outputPath + "/%(title)s.%(ext)s", url);
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
