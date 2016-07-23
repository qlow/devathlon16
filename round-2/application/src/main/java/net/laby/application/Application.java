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


		ConnectionsLoader.getList().getConnections().add( new Connection( "1", 0, "2" ) );

		connectionsLoader.saveConnections();

	}
	
	public static Application getInstance() {
		return instance;
	}
	
	public ArrayList<Connection> getConnectionList() {
		return ConnectionsLoader.getList().getConnections();
	}
	
	public static void main(String[] args) {
		new Application();
		ControlFrame.openGUI();
	}
	
	
}
