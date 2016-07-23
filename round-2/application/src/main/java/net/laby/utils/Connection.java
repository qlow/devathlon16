package net.laby.utils;

public class Connection {

	private String address;
	private int port;
	
	private String password;
	
	public Connection(String address, int port, String password) {
		this.address = address;
		this.port = port;
		this.password = password;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getPassword() {
		return password;
	}
	
	public int getPort() {
		return port;
	}
	
	public static Connection defaultConnection() {
		return new Connection("", 1337, "");
	}
	
}
