package it.unibo.oop.lab.lambda.ex02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return songs.stream()
                .map(o -> o.getSongName())
                .sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums.entrySet().stream().filter(o -> o.getValue() == year).map(o -> o.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) this.songs.stream()
                .filter(o -> o.getAlbumName().isPresent())
                .filter(o -> o.getAlbumName().get().equals(albumName))
                .count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream().filter(o -> o.getAlbumName().isEmpty()).count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.songs.stream()
                .filter(o -> o.getAlbumName().isPresent() && o.getAlbumName().get().equals(albumName))
                .mapToDouble(o -> o.getDuration())
                .average();
    }

    @Override
    public Optional<String> longestSong() {
        return this.songs.stream()
                //.mapToDouble(())
                .reduce((o1, o2) -> {
                    if (o1.getDuration() > o2.getDuration()) {
                        return o1;
                    }
                    return o2;
                })
                .map(o -> o.getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
        return this.songs.stream()
                .filter(o -> o.getAlbumName().isPresent())
                .collect(Collectors.groupingBy(o -> o.getAlbumName(), Collectors.summingDouble(o -> o.getDuration())))
                .entrySet().stream()
                .reduce((o1, o2) -> {
                    if (o1.getValue() > o2.getValue()) {
                        return o1;
                    }
                    return o2;
                })
                .map(o -> o.getKey())
                .get();
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
