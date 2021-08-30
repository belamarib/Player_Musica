package dados;

public class Playlist {
	private Musica[] playlist;
	private int max_size = 1000; //tamanho máximo = 1000 músicas.
	private int curr_size = 0;
	private boolean em_uso = false;
	
	public Playlist() {
		this.playlist = new Musica[this.max_size];
	}
	
	public boolean vazia() {
		return (this.curr_size==0);
	}
	
	public boolean getEm_uso() {
		return this.em_uso;
	}
	
	public void setEm_uso(boolean b) {
		this.em_uso = b;
	}
	
	
	public void addMusic(String song, String artist, double time) {
		if (this.curr_size < this.max_size) {
			Musica nova_msc = new Musica(song, artist, time);
			this.playlist[this.curr_size] = nova_msc;
			this.curr_size ++;
		} else {
			System.out.println("Sem espaço para mais músicas!");
		}

	}
	
	public Musica getSong(int indice) {
		if (indice < this.curr_size) {
			return this.playlist[indice]; 
		} else {
			return null;
		}
	}
		
	
	public int getCurrSize() {
		return this.curr_size;
	}
	
	public void removeMusic(int num_rem) {
		for (int j = num_rem; j < this.curr_size-1; j++) {
			this.playlist[j] = this.playlist[j+1];
		}
		this.curr_size--;
	}
	
}

