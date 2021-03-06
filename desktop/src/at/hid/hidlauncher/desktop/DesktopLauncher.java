package at.hid.hidlauncher.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import at.hid.hidlauncher.HIDLauncher;
import at.hid.hidlauncher.desktop.api.DesktopApp42;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = HIDLauncher.TITLE + " " + HIDLauncher.VERSION;
		config.vSyncEnabled = true;
		config.width = 960;
		config.height = 540;
		LwjglApplicationConfiguration.disableAudio = true;
		new LwjglApplication(new HIDLauncher(new DesktopApp42()), config);
	}
}
