package com.myplaylist.iterator;

import com.myplaylist.model.Video;
import java.util.List;

// 1. Jadikan PUBLIC agar bisa dites
public class VideoListContainer implements Container<Video> {
    private List<Video> videos;

    public VideoListContainer(List<Video> videos) {
        this.videos = videos;
    }

    @Override
    public Iterator<Video> getIterator() {
        return new VideoListIterator();
    }

    // Inner class Iterator
    private class VideoListIterator implements Iterator<Video> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < videos.size();
        }

        @Override
        public Video next() {
            if (this.hasNext()) {
                Video video = videos.get(index);
                index++;
                return video;
            }
            return null;
        }

        // Fitur Mundur (Prev)
        @Override
        public boolean hasPrev() {
            return index > 0;
        }

        @Override
        public Video prev() {
            if (this.hasPrev()) {
                index--;
                return videos.get(index);
            }
            return null;
        }

        @Override
        public Video current() {
            if (index > 0 && index <= videos.size()) {
                return videos.get(index - 1);
            }
            // Fallback jika belum mulai atau index 0
            if (!videos.isEmpty()) return videos.get(0);
            return null;
        }
    }
}