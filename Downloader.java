// Handles the actual downloading by running yt-dlp as a command-line process
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

    // Builds and runs the yt-dlp command, then waits for it to finish
    public void download() throws Exception {
        ProcessBuilder pb;
        if (format.equals("mp3")) {
            // -x extracts audio, --audio-format mp3 converts it to mp3
            pb = new ProcessBuilder("/opt/homebrew/bin/yt-dlp", "-x", "--audio-format", "mp3", "-o", outputPath + "/%(title)s.%(ext)s", url);
        } else {
            // -f mp4 downloads the video in mp4 format
            pb = new ProcessBuilder("/opt/homebrew/bin/yt-dlp", "-f", "mp4", "-o", outputPath + "/%(title)s.%(ext)s", url);
        }
        pb.inheritIO(); // pipes yt-dlp output to the terminal so you can see progress
        Process process = pb.start();
        process.waitFor(); // pauses this thread until yt-dlp finishes
    }

}
