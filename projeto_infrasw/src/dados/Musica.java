package dados;

public class Musica {
	private String songName;
	private String artistName;
	private int songTime;
	
	public Musica(String song, String artist, double time) {
		this.songName = song;
		this.artistName = artist;
		this.songTime = (((int)time * 60) + (int)((time - (int)time) * 100)); //transforma o tempo em segundos
	}
	
	public void setSongName(String song) {
		this.songName = song;
	}
	
	public String getSongName() {
		return this.songName;
	}
	
	public void setArtistName(String artist) {
		this.artistName = artist;
	}
	
	public String getArtistName() {
		return this.artistName;
	}
	
	public void setSongTime(int time) {
		this.songTime = time;
	}
	
	public int getSongTime() {
		return this.songTime;
	}
}

