package net.laby.application;

import java.io.File;
import java.util.ArrayList;

import net.laby.utils.Connection;
import net.laby.utils.ConnectionsLoader;

public class Application {

	private ConnectionsLoader connectionsLoader = new ConnectionsLoader(this, new File("config.json"));
	
	private static Application instance;
	
	public Application() {
		instance = this;
		connectionsLoader.loadConnections();
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
