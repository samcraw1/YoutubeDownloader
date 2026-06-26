# YouTube Downloader

A Java Swing desktop app for downloading YouTube videos and audio using yt-dlp.

## Features

- Download videos as **mp4** or audio as **mp3**
- **Video quality selector** — choose 1080p, 720p, 480p, or 360p
- **Thumbnail preview** — shows the video thumbnail before downloading
- **Progress bar** — tracks download progress in real time
- **Playlist support** — download entire YouTube playlists
- **Choose save location** — pick any folder to save downloads
- **Download history** — keeps a log of all previously downloaded videos

## Requirements

- Java 17+
- [yt-dlp](https://github.com/yt-dlp/yt-dlp) installed at `/opt/homebrew/bin/yt-dlp`

## Running the app

```bash
javac -cp .:flatlaf.jar *.java
java -cp .:flatlaf.jar Main
```

Or double-click `YoutubeDownloader.jar` if yt-dlp is installed.

## Dependencies

- [FlatLaf](https://github.com/JFormDesigner/FlatLaf) — dark theme for Swing UI
