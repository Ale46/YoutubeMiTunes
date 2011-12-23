/**
 * A simple representation of a song in the iTunes library.
 */
public class Song {

	private String name;
	private String artist;
	private String album;
	private String year;

	public Song (String name, String artist, String album, String year){
		this.name  = name;
		this.artist = artist;
		this.album = album;
		this.year = year;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artistName) {
		this.artist = artistName;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String albumName) {
		this.album = albumName;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

}