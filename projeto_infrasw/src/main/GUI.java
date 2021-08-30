package main;

/*
 * PLAYLIST - SEGUNDA ENTREGA
 * 
 * DUPLA: AMANDA ALVES GUIMARÃES (aag) e ISABELA MARINHO RIBEIRO (imr)
 * 
 * OBSERVAÇÕES:
 * - Quando uma música acaba de ser executada, não avançamos automaticamente para a próxima música. Permanecemos na mesma música e,
 * para executá-la novamente, é preciso pressionar o botão PLAY 2x.
 * - Para executar uma nova música, após pressionar ANTERIOR/PRÓXIMO, é necessário pressionar o botão PLAY.
 * - A playlist só é exibida após pressionar o botão EXIBIR PLAYLIST ATUALIZADA.
 * - Não será possível remover uma música que está sendo executada
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dados.Playlist;
import threads.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GUI implements ActionListener, ListSelectionListener, FocusListener {
	
	//botões
	private JButton addButton;
	private JButton remButton;
	private JButton playpauseButton;
	private JButton nextButton;
	private JButton backButton;
	private JButton showButton;
	private JButton	shuffleButton;
	
	//labels
	private JLabel msg_mscExecutada;
	private JLabel msg_formatoTempo;
	private JLabel msg_apenasNumeros;
	private JLabel msg_instrucoes;
	
	//lista de musicas
	private JTextArea lista_musicas;
	
	//caixas de input
	private JTextField nomeArtista;
	private JTextField nomeMusica;
	private JTextField indexMusica;
	private JTextField duracaoMusica;
	
	//barra de progresso
	private JProgressBar musicaExecucao;
	
	//variáveis para auxiliar no controle de algumas funcionalidades
	private boolean deleteArtistText = true; //controle do texto informativo dentro da caixa do input do nome do artista
	private boolean deleteSongText = true; //controle do texto informativo dentro da caixa do input do nome da música
	private boolean deleteIndexText = true; //controle do texto informativo dentro da caixa do input do número do index
	private boolean deleteDuracaoMusica = true; //controle do texto informativo dentro da caixa do input da duração da música
	private boolean play = false; //interrompe loop dentro da thread play/pause
	private boolean playagain = false; //checa se a música acabou para reiniciar a barra de progresso e variáveis para a execução da música
	private boolean novaMusica = true; //reinicia barra de progresso e execução da música
	private boolean isShuffle = false; //informa se a lista está no modo aleatório
	
	private int counter1 = 0; //counter para controlar se a música acabou
	private int msc_atual = -1; //índice da música atual.
	private int tamanho_msc_atual = 0; //em segundos
	
	private Playlist playlist;
	
	private Lock lock = new ReentrantLock();
	private Condition inUse = lock.newCondition();
	private Condition notInUse = lock.newCondition();
	
	
	public GUI() {
		
		//playlist
		playlist = new Playlist();
		
		
		//lista de músicas (modo de exibição)
		lista_musicas = new JTextArea();
		lista_musicas.setBounds(142, 120, 1118, 310);
		
		
		//botões
		addButton = new JButton("Adicionar Música");
		addButton.addActionListener(this);
		addButton.setActionCommand("add_act");
		addButton.setBounds(502, 50, 150, 30);
		
		remButton = new JButton("Remover Música");
		remButton.addActionListener(this);
		remButton.setActionCommand("rem_act");
		remButton.setBounds(920, 50, 150, 30);
		
		playpauseButton = new JButton("PLAY"); //muda para pause após ter pressionado o play
		playpauseButton.addActionListener(this);
		playpauseButton.setActionCommand("playpause_act");
		playpauseButton.setBounds(604, 500, 200, 40);
		
		nextButton = new JButton("PRÓXIMA");
		nextButton.addActionListener(this);
		nextButton.setActionCommand("next_act");
		nextButton.setBounds(900, 500, 100, 40); //tava 1060
		
		backButton = new JButton("ANTERIOR");
		backButton.addActionListener(this);
		backButton.setActionCommand("back_act");
		backButton.setBounds(400, 500, 100, 40); //x tava 142
		
		showButton = new JButton("Exibir playlist atualizada");
		showButton.addActionListener(this);
		showButton.setActionCommand("show_act");
		showButton.setBounds(1090, 50, 180, 30);
		
		shuffleButton = new JButton("Modo aleatório OFF");
		shuffleButton.addActionListener(this);
		shuffleButton.setActionCommand("shuffle_act");
		shuffleButton.setBounds(1100, 500, 150, 40);
		
		//progress bar
		musicaExecucao = new JProgressBar();
		musicaExecucao.setStringPainted(true);
		musicaExecucao.setValue(0);
		musicaExecucao.setString("0");
		musicaExecucao.setSize(100, 25);
		musicaExecucao.setBounds(142, 450, 1118, 15);
		
		
		//campos de texto para input
		nomeArtista = new JTextField(10);
		nomeArtista.setLayout(new FlowLayout());
		nomeArtista.setSize(20, 5);
		nomeArtista.setVisible(true);
		nomeArtista.addActionListener(this);
		nomeArtista.setEditable(true);
		nomeArtista.setText("Artista");
		nomeArtista.addFocusListener(this);
		nomeArtista.setBounds(262, 50, 100, 30);
		
		nomeMusica = new JTextField(10);
		nomeMusica.setLayout(new FlowLayout());
		nomeMusica.setSize(20, 5);
		nomeMusica.setVisible(true);
		nomeMusica.addActionListener(this);
		nomeMusica.setEditable(true);
		nomeMusica.setText("Música");
		nomeMusica.addFocusListener(this);
		nomeMusica.setBounds(142, 50, 100, 30);
		
		indexMusica = new JTextField(10);
		indexMusica.setLayout(new FlowLayout());
		indexMusica.setSize(100, 5);
		indexMusica.setVisible(true);
		indexMusica.addActionListener(this);
		indexMusica.setEditable(true);
		indexMusica.setText("Número da música para remover");
		indexMusica.addFocusListener(this);
		indexMusica.setBounds(702, 50, 200, 30);
		
		duracaoMusica = new JTextField(10);
		duracaoMusica.setLayout(new FlowLayout());
		duracaoMusica.setSize(100,  5);
		duracaoMusica.setVisible(true);
		duracaoMusica.addActionListener(this);
		duracaoMusica.setEditable(true);
		duracaoMusica.setText("Duração");
		duracaoMusica.addFocusListener(this);
		duracaoMusica.setBounds(382, 50, 100, 30);
		
		//textos e mensagens informativas
		msg_mscExecutada = new JLabel();
		msg_mscExecutada.setBounds(142, 470, 1118, 15);
		
		msg_formatoTempo = new JLabel();
		msg_formatoTempo.setText("Formato: x.y, onde x = minuto(s) e y = segundo(s)");
		msg_formatoTempo.setBounds(382, 80, 400, 30);
		
		msg_apenasNumeros = new JLabel();
		msg_apenasNumeros.setBounds(702, 80, 300, 30);
		
		msg_instrucoes = new JLabel();
		msg_instrucoes.setBounds(142, 550, 1118, 15);
		msg_instrucoes.setText("Ao final de uma música, para executá-la novamente, clique 2x em PLAY. Após avançar/voltar uma música, é necessário pressionar PLAY para que ela execute.");
		
		
		//panel
		JPanel panel = new JPanel(null); //
		panel.add(lista_musicas);
		panel.add(nomeMusica);
		panel.add(nomeArtista);
		panel.add(duracaoMusica);
		panel.add(addButton);
		panel.add(indexMusica);
		panel.add(remButton);
		panel.add(playpauseButton);
		panel.add(musicaExecucao);
		panel.add(nextButton);
		panel.add(backButton);
		panel.add(msg_mscExecutada);
		panel.add(msg_formatoTempo);
		panel.add(msg_apenasNumeros);
		panel.add(msg_instrucoes);
		panel.add(showButton);
		panel.add(shuffleButton);
		
		
		//frame
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.setTitle("GUI");
		frame.setSize(1650,1080);
		frame.setVisible(true);
		
	}
	
	public static void main(String [] args) {
		new GUI();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("add_act")) { //adicionar uma música
			msg_formatoTempo.setText("Formato: x.y, onde x = minuto(s) e y = segundo(s)");
			String nome_musica = nomeMusica.getText();
			String nome_artista = nomeArtista.getText();
			int curr_size = playlist.getCurrSize();
			try {
				double duracao_msc = Double.parseDouble(duracaoMusica.getText());
				if (duracao_msc <= 0) {
					msg_formatoTempo.setText("Sem números negativos!");
				} else if (playlist.getCurrSize() == 1000) {
					msg_formatoTempo.setText("Sem espaço!");
				} else {
					Thread adicionar_musica = new Thread(new Adicionar(lock, inUse, notInUse, playlist, nome_musica, nome_artista, duracao_msc));
					adicionar_musica.start();
				}
				deleteSongText = true;
				deleteArtistText = true;
				deleteDuracaoMusica = true;
				nomeMusica.setText("Música");
				nomeArtista.setText("Artista");
				duracaoMusica.setText("Duração");
			} catch (NumberFormatException e1) {
				deleteSongText = true;
				deleteArtistText = true;
				deleteDuracaoMusica = true;
				nomeMusica.setText("Música");
				nomeArtista.setText("Artista");
				duracaoMusica.setText("Duração");
				msg_formatoTempo.setText("Apenas números no formato x.y!");
			}
		} else if (command.equals("rem_act")) { //remover
			String num_musica = indexMusica.getText();
			msg_apenasNumeros.setText("");
			try {
				int num_musica_int = Integer.parseInt(num_musica);
				if (num_musica_int == msc_atual) {
					msg_apenasNumeros.setText("Não é possível remover a música selecionada");
				} else if (num_musica_int < 0 || num_musica_int >= playlist.getCurrSize()){
					msg_apenasNumeros.setText("Música inexistente");
				} else {
					Thread remover_musica = new Thread(new Remover(lock, inUse, notInUse, playlist, num_musica_int, msc_atual));
					remover_musica.start();
				}
				deleteIndexText = true;
				indexMusica.setText("Número da música que deseja remover");
			} catch (NumberFormatException e1) {
				deleteIndexText = true;
				indexMusica.setText("Número da música para remover");
				msg_apenasNumeros.setText("Apenas números!");
			}
		} else if (command.equals("playpause_act")) { //play-pause
			if (!play) { 
				play = true; //muda estado para play
				if (playlist.getCurrSize() == 0) { //lista vazia
					msc_atual = -1;
				} else {
					if (msc_atual == -1) //inicia a execução na musica zero. para trocar, apenas selecionando ANTERIOR ou PRÓXIMO
						msc_atual = 0;
					new Thread() {
						public void run() {
							playagain = false;
							if (novaMusica) {
								counter1 = 0;
								tamanho_msc_atual = playlist.getSong(msc_atual).getSongTime(); //em segundos
								musicaExecucao.setValue(0);
								musicaExecucao.setString("0");
							}
							playpauseButton.setText("PAUSE");
							msg_mscExecutada.setText("Tocando agora: "+playlist.getSong(msc_atual).getSongName() + " - " + playlist.getSong(msc_atual).getArtistName());
							while ((counter1 < tamanho_msc_atual) && play) {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
								counter1 ++;
								musicaExecucao.setValue((int) (counter1*100)/tamanho_msc_atual);
								musicaExecucao.setString(Integer.toString(counter1));
							}
							if (!(counter1 < tamanho_msc_atual)) //significa que o tempo da música acabou.
								playagain = true;
							playpauseButton.setText("PLAY");
							if (novaMusica) {
								try {
									Thread.sleep(500); //espera um pouco para mostrar a progressbar completa
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
								msg_mscExecutada.setText("");
								counter1 = 0;
								musicaExecucao.setValue(0);
								musicaExecucao.setString("0");
							}
						}
					}.start();
				}
			} else {
				play = false;
				novaMusica = playagain; //playagain = true se música tiver acabado. playagain = false se música ainda nao tiver acabado.
			}
		} else if (command.equals("next_act")) { //próxima música
			play = false;
			if (!isShuffle) {
				if (msc_atual < playlist.getCurrSize() - 1) {
					msc_atual ++;
				} else {
					msc_atual = -1;
				}
			} else {
				Random random = new Random();
				msc_atual = random.nextInt(playlist.getCurrSize());
			}
			novaMusica = true;
		} else if (command.equals("back_act")) { //música anterior
			play = false;
			if (!isShuffle) {
				if (msc_atual <= 0) {
					msc_atual = -1;
				} else {
					msc_atual --;
				}
			} else {
				Random random = new Random();
				msc_atual = random.nextInt(playlist.getCurrSize());
			}
			novaMusica = true;
		} else if (command.equals("show_act")) { //exibir playlist
			new Thread () {
				public void run() {
					int currsize = playlist.getCurrSize();
					String musicas = "";
					for (int i = 0; i < currsize; i++) {
						musicas += "(" +i+ ") "+ playlist.getSong(i).getSongName() + " - " + playlist.getSong(i).getArtistName() + "\n";
					}
					lista_musicas.setText(musicas);
				}
			}.start();
		} else if (command.equals("shuffle_act")) { //ativar/desativar o modo aleatório
			if (!isShuffle) {
				shuffleButton.setText("Modo aleatório ON");
				isShuffle = true;
			} else {
				shuffleButton.setText("Modo aleatório OFF");
				isShuffle = false;
			}
		}
	}

	@Override
	public void focusGained(FocusEvent e) { //para setar os textos informativos dentro das caixas de entrada
		if (nomeMusica.hasFocus() && deleteSongText) {
			nomeMusica.setText("");
			deleteSongText = false;
		} else if (nomeArtista.hasFocus() && deleteArtistText) {
			nomeArtista.setText("");
			deleteArtistText = false;
		} else if (indexMusica.hasFocus() && deleteIndexText) {
			indexMusica.setText("");
			deleteIndexText = false;
		} else if (duracaoMusica.hasFocus() && deleteDuracaoMusica) {
			duracaoMusica.setText("");
			deleteDuracaoMusica = false;
		}
		
	}

	@Override
	public void focusLost(FocusEvent e) { //para setar os textos informativos dentro das caixas de entrada
		if (!nomeMusica.hasFocus() && nomeMusica.getText().equals("")) {
			nomeMusica.setText("Música");
			deleteSongText = true;
		} else if (!nomeArtista.hasFocus() && nomeArtista.getText().equals("")) {
			nomeArtista.setText("Artista");
			deleteArtistText = true;
		} else if (!indexMusica.hasFocus() && indexMusica.getText().equals("")) {
			indexMusica.setText("Número da música para remover");
			deleteIndexText = true;
		} else if (!duracaoMusica.hasFocus() && duracaoMusica.getText().equals("")) {
			duracaoMusica.setText("Duração");
			deleteDuracaoMusica = true;
		}
		
	}

}
