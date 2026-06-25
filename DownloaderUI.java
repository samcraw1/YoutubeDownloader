import javax.swing.*;
import java.awt.event.*;
import java.util.List;

// Builds the app window and handles button clicks
public class DownloaderUI implements ActionListener {

    private JFrame frame;                // the main window
    private JPanel panel;                // container that holds all the UI components
    private JLabel statusLabel;          // shows messages like "Downloading..." or "Done!"
    private JTextField urlField;         // where the user types the YouTube URL
    private JButton downloadButton;      // triggers the download
    private JComboBox<String> formatBox; // dropdown to choose mp4 or mp3
    private DownloadHistory history;     // handles saving and reading download history
    private JButton historyButton;       // opens the history popup
    private JLabel thumbnailLabel;       // displays the video thumbnail
    private JProgressBar progressBar;
    private JLabel  progressLabel;
    private JButton browseButton;
    private JLabel saveLocationLabel;

    DownloaderUI() {
        frame = new JFrame("Youtube Downloader");
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // stacks components vertically

        urlField = new JTextField(30);
        downloadButton = new JButton("Download");
        downloadButton.addActionListener(this); // calls actionPerformed when clicked
        historyButton = new JButton("Show History");
        historyButton.addActionListener(this);
        browseButton = new JButton("Browse");
        browseButton.addActionListener(this);
        saveLocationLabel = new JLabel(System.getProperty("user.home") + "/Downloads");
        statusLabel = new JLabel("Enter a URL and click Download");

        progressBar = new JProgressBar(0,100);
        progressBar.setStringPainted(false);
        progressBar.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, progressBar.getPreferredSize().height));
        
        progressLabel = new JLabel("0%");


        thumbnailLabel = new JLabel();

        formatBox = new JComboBox<>(new String[]{"mp4", "mp3"});

        // points to the file where download history will be saved
        history = new DownloadHistory(System.getProperty("user.home") + "/Downloads/yt-history.txt");

        // add all components to the panel in order
        panel.add(new JLabel("Youtube URL"));
        panel.add(urlField);
        panel.add(new JLabel("format"));
        panel.add(formatBox);
        panel.add(downloadButton);
        panel.add(progressBar);
        panel.add(progressLabel);
        panel.add(thumbnailLabel);
        panel.add(historyButton);
        panel.add(statusLabel);
        panel.add(new JLabel("Save to:"));
        panel.add(saveLocationLabel);
        panel.add(browseButton);

        thumbnailLabel.setPreferredSize(new java.awt.Dimension(160, 90));
        thumbnailLabel.setMinimumSize(new java.awt.Dimension(160, 90));
        thumbnailLabel.setMaximumSize(new java.awt.Dimension(160, 90));
        urlField.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, urlField.getPreferredSize().height));
        formatBox.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, formatBox.getPreferredSize().height));

        frame.add(panel);
        frame.pack();
        frame.setMinimumSize(frame.getSize()); // prevent shrinking below the default size
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == downloadButton) {
            String url = urlField.getText();
            String format = (String) formatBox.getSelectedItem();
            showThumbnail(url); // fetch and display thumbnail before downloading
            statusLabel.setText("Downloading...");
            // run the download in a separate thread so the UI doesn't freeze
            new Thread(() -> {
                try {
                    Downloader downloader = new Downloader(url, saveLocationLabel.getText(),format);
                    downloader.download(progressBar, progressLabel);
                    history.add(url, format); // save to history after a successful download
                    statusLabel.setText("Done!");
                } catch (Exception ex) {
                    statusLabel.setText("Error: " + ex.getMessage());
                }
            }).start();
        } else if (e.getSource() == historyButton) {
            // load all history entries and show them in a popup dialog
            try {
                List<String> entries = history.getAll();
                String text = entries.isEmpty() ? "No history yet." : String.join("\n", entries);
                JOptionPane.showMessageDialog(frame, text, "Download History", JOptionPane.PLAIN_MESSAGE);
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        }else if (e.getSource() == browseButton) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                saveLocationLabel.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }
    }

    // pulls the video ID out of a YouTube URL
    private String extractVideoId(String url) {
        String id;
        if (url.contains("?v=")) {
            int index = url.indexOf("?v=");
            id = url.substring(index + 3);
        } else if (url.contains("&v=")) {
            int index = url.indexOf("&v=");
            id = url.substring(index + 3);
        } else {
            int index = url.lastIndexOf("/");
            id = url.substring(index + 1);
        }
        // strip any extra query params like ?si=... or &t=...
        if (id.contains("?")) id = id.substring(0, id.indexOf("?"));
        if (id.contains("&")) id = id.substring(0, id.indexOf("&"));
        return id;
    }

    // fetches the thumbnail image from YouTube and displays it in thumbnailLabel
    private void showThumbnail(String url) {
        try {
            String id = extractVideoId(url);
            java.net.URL imageUrl = new java.net.URL("https://img.youtube.com/vi/" + id + "/0.jpg");
            java.awt.Image image = javax.imageio.ImageIO.read(imageUrl);
            image = image.getScaledInstance(160, 90, java.awt.Image.SCALE_SMOOTH);
            thumbnailLabel.setIcon(new ImageIcon(image));
            frame.pack(); // resize the window to fit the thumbnail
        } catch (Exception ex) {
            statusLabel.setText("Could not load thumbnail.");
        }
    }
}
