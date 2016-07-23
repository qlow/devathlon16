package net.laby.application;

import net.laby.utils.Connection;
import net.laby.utils.ConnectionsLoader;

import java.io.File;
import java.util.ArrayList;

public class Application {

	private ConnectionsLoader connectionsLoader = new ConnectionsLoader(this, new File("config.json"));
	
	private static Application instance;
	
	public Application() {
		instance = this;
		connectionsLoader.loadConnections();


		ConnectionsLoader.settings.connections.add( new Connection( "" ) );

	}
	
	public static Application getInstance() {
		return instance;
	}
	
	public ArrayList<Connection> getConnectionList() {
		return ConnectionsLoader.settings.connections;
	}
	
	public static void main(String[] args) {
		new Application();
		ControlFrame.openGUI();
	}
	
	
}
