package net.laby.application;

import net.laby.utils.Connection;
import net.laby.utils.ConnectionsLoader;

import java.io.File;
import java.util.ArrayList;

public class Application {

	private ConnectionsLoader connectionsLoader = new ConnectionsLoader(new File("config.json"));
	
	private static Application instance;
	
	public Application() {
		instance = this;
		connectionsLoader.loadConnections();
	}

	public static Application getInstance() {
		return instance;
	}

	public ConnectionsLoader getConnectionsLoader( ) {
		return connectionsLoader;
	}
	
	public static void main(String[] args) {
		new Application();
		ControlFrame.openGUI();
	}


	public ArrayList<Connection> getConnections( ) {
		return this.connectionsLoader.getList().getConnections();
	}

	public static void connect( Connection connection ) {

	}
}
