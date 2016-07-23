package net.laby.utils;

import net.laby.application.Application;

import java.io.File;
import java.io.IOException;

public class ConnectionsLoader {
	
	private File configFile;
	public static Settings settings;
	
	private Application main;
	
	public ConnectionsLoader(Application main, File file) {
		this.main = main;
		this.configFile = file;
	}

	public void loadConnections() {
		if(!this.configFile.exists()) {
			saveConnections(true);
		}
		
		
	}

	private void saveConnections(boolean defaultSettings) {
		if(!this.configFile.exists()) {
			this.configFile.getParentFile().mkdirs();
			try {
				this.configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(defaultSettings) {
			
		}
	}
	
}
