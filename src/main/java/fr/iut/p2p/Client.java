package fr.iut.p2p;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;




public class Client {

	private final static int _dgLength = 1500;
	private DatagramSocket dgSocket;
	private DatagramPacket dgPacket;
	public String uuid;
	private InetAddress address;
	private Integer port;
	public ArrayList<Fichier> otherFichiers;
	private  Hashtable<String, PeerInfo> peers;	
	/**
	 * Constructeur d'un client
	 * 
	 * @param address
	 *            adresse du client
	 * @param port
	 *            numero de port du client
	 * @throws IOException
	 */
	public Client(InetAddress address, Integer port) {
		try {
			dgSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.address = address;
		this.port = port;
		this.otherFichiers = new ArrayList<Fichier>();
	}

	public int initialisation() throws IOException {
		this.register();
		if (this.receiveUuid() == 0)
			return 0;
		this.initInformations();
		this.receiveList();
		return 1;
	}

	/**
	 * Methode permettant de recevoir un message du serveur
	 * 
	 * @return
	 * @throws IOException
	 */
	public String receive() {
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		try {
			dgSocket.receive(dgPacket);
		} catch (IOException e) {
			return null;
		}
		String s = new String(dgPacket.getData(), dgPacket.getOffset(),
				dgPacket.getLength());
		System.out.println("receive :" + s);
		return s;
	}

	/**
	 * Reception de la confirmation du serveur du quittage
	 * 
	 * @throws IOException
	 */
	private int receiveQuit() throws IOException {
		String reponse = this.receive();
		if (reponse == null)
			return 0;
		if (reponse.equals("OK")) {
			System.out.println("le serveur a bien quitter");
		} else if (reponse.equals("ERROR")) {
			System.out
					.println("le serveur n'a pas pu quitter suite a une erreur");
		}
		return 1;
	}

	/**
	 * Reception de l'identifiant unique
	 * 
	 * @throws IOException
	 */
	private int receiveUuid() throws IOException {
		String reponse = this.receive();
		if (reponse == null)
			return 0;
		String[] tmp = reponse.split(":");
		String uuid = tmp[1];
		this.uuid = uuid;
		return 1;
	}

	/**
	 * Methode permettant d'envoyer un message au serveur en UDP
	 * 
	 * @param msg
	 *            message a envoyer
	 * @param address
	 *            adresse du serveur
	 * @param port
	 *            numero de port du serveur
	 * @throws IOException
	 */
	private int send(String msg, InetAddress address, int port) {
		//System.out.println("send :" + msg);
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(address);
		dgPacket.setPort(port);
		try {
			dgSocket.send(dgPacket);
		} catch (IOException e) {
			return 0;
		}
		return 1;
	}

	/**
	 * Demande de quittage du reseau au serveur
	 * 
	 * @throws IOException
	 */
	public int sendQuit() throws IOException {
		return this.send("QUIT:" + this.uuid, this.address, this.port);
	}

	/**
	 * Demande d'identifiant au serveur
	 * 
	 * @throws IOException
	 */
	private int register() throws IOException {
		return this.send("RGTR", this.address, this.port);
	}

	/**
	 * Demande des liste des pairs et des fichiers au serveur
	 * 
	 * @throws IOException
	 */
	private int initInformations() throws IOException {
		return this.send("RTRV:" + this.uuid, this.address, this.port);
	}

	/**
	 * Reception de la liste des pairs et des fichiers
	 * 
	 * @throws IOException
	 */
	private void receiveList() throws IOException {
		String[] reponse = this.receive().split("[:]");
		int nb = Integer.parseInt(reponse[1]);
		for (int i = 0; i < nb; i++) {
			String[] list = this.receive().split("[:]");
			this.peers.put(list[0], new PeerInfo(list[0], list[1], list[2]));
		}

		reponse = this.receive().split("[:]");
		nb = Integer.parseInt(reponse[1]);
		for (int i = 0; i < nb; i++) {
			String[] list = this.receive().split("[|]");
			this.otherFichiers.add(new Fichier(list[0], Integer
					.parseInt(list[1]), list[2]));
		}
	}
	
	public String getList(){
		String liste = new String();
		for (int i = 0; i < this.otherFichiers.size(); i++) {
			liste += otherFichiers.get(i).toString() + "\n";
		}
		return liste;
	}



	/**
	 * Suite d'instruction lancant un client
	 * 
	 * @param args
	 *            String Adresse + int port
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Client client = new Client(InetAddress.getByName("localhost"), 5001);
		client.register();
		client.receiveUuid();
		client.initInformations();
		client.receiveList();
	}
}
