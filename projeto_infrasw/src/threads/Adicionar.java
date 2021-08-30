package threads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import dados.*;


public class Adicionar extends Thread {
	private String nome_musica;
	private String nome_artista;
	private double duracao_musica;
	private Playlist playlist;
	private final Lock lock;
	private final Condition inUse;
	private final Condition notInUse;

	public Adicionar(Lock l, Condition inUseCondition, Condition notInUseCondition,  Playlist p, String nome_msc, String nome_art, double duracao_msc) {
		this.nome_musica = nome_msc;
		this.nome_artista = nome_art;
		this.playlist = p;
		this.duracao_musica = duracao_msc;
		this.inUse = inUseCondition;
		this.lock = l;
		this.notInUse = notInUseCondition;
		
	}
	
	public void run() {
		try {
            lock.lock();
            while(this.playlist.getEm_uso()) {
            	inUse.await();
            }
            this.playlist.setEm_uso(true);
            this.playlist.addMusic(this.nome_musica, this.nome_artista, this.duracao_musica);
			this.playlist.setEm_uso(false);
			notInUse.signalAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

}

