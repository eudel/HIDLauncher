package at.hid.hidlauncher;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import at.hid.hidlauncher.screens.MainMenu;
import at.hid.hidlauncher.screens.Login;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.shephertz.app42.paas.sdk.java.App42API;
import com.shephertz.app42.paas.sdk.java.ServiceAPI;
import com.shephertz.app42.paas.sdk.java.session.Session;
import com.shephertz.app42.paas.sdk.java.session.SessionService;
import com.shephertz.app42.paas.sdk.java.storage.StorageService;
import com.shephertz.app42.paas.sdk.java.user.User;
import com.shephertz.app42.paas.sdk.java.user.UserService;

public class HIDLauncher extends Game {
	
	public static final String TITLE = "HIDLauncher", VERSION = "0.0.1-alpha";
	public static boolean DEBUG;
	public static Profile profile = new Profile();
	public static PlayerProfile playerProfile = new PlayerProfile();
	public static GameProfile gameProfile = new GameProfile();
	public static I18NBundle langBundle;
	public static ServiceAPI serviceAPI;
	public static UserService userService;
	public static User user;
	public static StorageService storageService;
	public static SessionService sessionService;
	public static Session session;

	/**
	 * creates the language bundle
	 */
	public static void createLangBundle(String lang) {
		FileHandle fhLang = Gdx.files.internal("lang/Language");
		Locale locale = new Locale(lang);
		langBundle = I18NBundle.createBundle(fhLang, locale);
	}

	/**
	 * @return the langBundle
	 */
	public static I18NBundle getLangBundle() {
		return langBundle;
	}

	public static void log(String tag, String message) {
		FileHandle fhLog = Gdx.files.external(".hidlauncher/logs/latest.log");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss: ");
		Gdx.app.log(tag, message);
		fhLog.writeString(sdf.format(date) + tag + ": " + message + "\n", true, "UTF-8");
	}

	public static void debug(String tag, String message) {
		FileHandle fhLog = Gdx.files.external(".hidlauncher/logs/latest.log");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss: ");
		Gdx.app.debug(tag, message);
		if (HIDLauncher.DEBUG) {
			fhLog.writeString(sdf.format(date) + tag + ": Debug: " + message + "\n", true, "UTF-8");
		}
	}

	public static void error(String tag, String message, Throwable t) {
		FileHandle fhLog = Gdx.files.external(".hidlauncher/logs/latest.log");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss: ");
		Gdx.app.error(tag, message, t);
		fhLog.writeString(sdf.format(date) + tag + ": ERROR: " + message + "\n", true, "UTF-8");
		fhLog.writeString(t + "\n", true, "UTF-8");
	}

	public void logrotate() {
		FileHandle fhLog = Gdx.files.external(".hidlauncher/logs/latest.log");
		fhLog.parent().mkdirs();
		if (fhLog.exists()) {
			byte[] buffer = new byte[1024];
			try {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
				FileOutputStream fos = new FileOutputStream(Gdx.files.external(".hidlauncher/logs/" + sdf.format(date) + ".zip").file());
				ZipOutputStream zos = new ZipOutputStream(fos);
				ZipEntry ze = new ZipEntry("latest.log");
				zos.putNextEntry(ze);
				FileInputStream in = new FileInputStream(Gdx.files.external(".hidlauncher/logs/latest.log").file());

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
				zos.closeEntry();
				zos.close();
				fhLog.delete();
			} catch (Exception e) {
				error(this.getClass().getName(), "error rotating log file", e);
			}
		}
	}

	@Override
	public void create() {
		App42API.initialize("3208bd2374491ab3f1be0dad5e69e451f2dfa814013d3ab17590361f57348745", "f7200050ace4b5395d3697d7cf45d66de64a5c62702a01e65100e5c74723cd0a");
		userService = App42API.buildUserService();
		storageService = App42API.buildStorageService();
		
		DEBUG = Gdx.app.getPreferences(TITLE).getBoolean("debug");
		logrotate();
		if (DEBUG) {
			Gdx.app.setLogLevel(Application.LOG_DEBUG); // show debug logs 
		} else {
			Gdx.app.setLogLevel(Application.LOG_INFO);
		}
		if (Gdx.app.getPreferences(TITLE).contains("lang")) { // load saved language preferences
			String[] data = Gdx.app.getPreferences(TITLE).getString("lang").split("_");
			Locale.setDefault(new Locale(data[0], data[1]));
		} else {
			Gdx.app.getPreferences(TITLE).putString("lang", Locale.getDefault().toString());
			Gdx.app.getPreferences(TITLE).putBoolean("debug", false);
			Gdx.app.getPreferences(TITLE).putBoolean("fullscreen", false);
			Gdx.app.getPreferences(TITLE).putBoolean("vsync", true);
			Gdx.app.getPreferences(TITLE).flush();
		}

		createLangBundle(Locale.getDefault().getLanguage());

		FileHandle fhLauncherProfiles = Gdx.files.external(".hidlauncher/launcher_profiles.json");
		if (fhLauncherProfiles.exists()) {
			profile = profile.loadProfile(".hidlauncher/launcher_profiles.json");
			
			if ((profile.getSelectedUser() != null) && (!profile.getSelectedUser().equals(""))) {
				playerProfile = playerProfile.loadPlayerProfile(profile.getSelectedUser());
				gameProfile = gameProfile.loadGameProfile(profile.getSelectedProfile());
				user = userService.getUser(profile.getSelectedUser());
				user.setSessionId(profile.getClientToken());
				setScreen(new MainMenu());
			} else {
				setScreen(new Login());
			}
		} else {
			setScreen(new Login());
		}
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
