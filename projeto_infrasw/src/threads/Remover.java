package threads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import dados.*;

public class Remover extends Thread {
	private int numero_musica;
	private Playlist playlist;
	private int curr_song;
	private final Lock lock;
	private final Condition inUse;
	private final Condition notInUse;



	public Remover(Lock l, Condition inUseCondition, Condition notInUseCondition, Playlist p, int numero_rem, int currsong) {
		this.numero_musica = numero_rem;
		this.playlist = p;
		this.inUse = inUseCondition;
		this.lock = l;
		this.curr_song = currsong;
		this.notInUse = notInUseCondition;
	}
	
	public void run() {
		try {
			lock.lock();
			while(this.playlist.getEm_uso()) {
            	notInUse.await();
            }
			this.playlist.setEm_uso(true);
			this.playlist.removeMusic(this.numero_musica);
			this.playlist.setEm_uso(false);
			inUse.signalAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}


}


