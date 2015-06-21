/**
 * 
 */
package at.hid.hidlauncher.screens;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import at.hid.hidlauncher.GameProfile;
import at.hid.hidlauncher.HIDLauncher;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * @author dunkler_engel
 *
 */
public class MainMenu implements Screen {

	private Stage stage;
	private TextureAtlas atlas;
	private Skin skin;
	private Table table, table0, table1, table2, table3;
	private Label lblProfile, lblInfo, lblUpdates;
	private TextButton btnTabUpdates, btnTabLog, btnTabProfiles, btnTabGames, btnNewProfile, btnEditProfile, btnPlay, btnSwitchUser, btnOptions;
	private SelectBox<String> selProfile;
	private ScrollPane spLog;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(new ExtendViewport(width, height));
		stage.getViewport().update(width, height, true);
		table.invalidateHierarchy();
		table.setSize(width, height);
	}

	@Override
	public void show() {
		HIDLauncher.debug(this.getClass().toString(), "creating MainMenu screen");
		stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

		Gdx.input.setInputProcessor(stage);

		// creating skin
		HIDLauncher.debug(this.getClass().toString(), "creating skin");
		atlas = new TextureAtlas("ui/atlas.pack");
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);

		table = new Table(skin);
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		table0 = new Table(skin);
		table1 = new Table(skin);
		table2 = new Table(skin);
		table3 = new Table(skin);
		spLog = new ScrollPane(table1);

		//creating labels
		HIDLauncher.debug(this.getClass().toString(), "creating labels");
		lblProfile = new Label(HIDLauncher.getLangBundle().format("MainMenu.lblProfile.text"), skin);
		lblInfo = new Label(HIDLauncher.getLangBundle().format("MainMenu.lblInfo.text") + " " + HIDLauncher.playerProfile.getDisplayName() + "\n", skin);

		// creating tabs
		HIDLauncher.debug(this.getClass().toString(), "creating tabs");
		btnTabUpdates = new TextButton(HIDLauncher.getLangBundle().format("MainMenu.btnTabUpdates.text"), skin, "tab");
		btnTabUpdates.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				btnTabUpdates.setDisabled(true);
				btnTabLog.setDisabled(false);
				btnTabProfiles.setDisabled(false);
				btnTabGames.setDisabled(false);
				table1.clear();
				lblUpdates = new Label("", skin);
				HttpRequest httpRequest = new HttpRequest(HttpMethods.GET);
				httpRequest.setUrl("https://hidlauncher.wordpress.com/");
				HttpResponseListener httpResponseListener = new HttpResponseListener() {

					@Override
					public void handleHttpResponse(HttpResponse httpResponse) {
						//						lblUpdates.setText(httpResponse.getResultAsString());
					}

					@Override
					public void failed(Throwable t) {

					}

					@Override
					public void cancelled() {

					}
				};
				Gdx.net.sendHttpRequest(httpRequest, httpResponseListener);
				table1.add(lblUpdates);
			}
		});
		btnTabUpdates.setDisabled(true);

		btnTabLog = new TextButton(HIDLauncher.getLangBundle().format("MainMenu.btnTabLog.text"), skin, "tab");
		btnTabLog.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				btnTabUpdates.setDisabled(false);
				btnTabLog.setDisabled(true);
				btnTabProfiles.setDisabled(false);
				btnTabGames.setDisabled(false);
				table1.clear();
				FileHandle fhLog = Gdx.files.external(".hidlauncher/logs/latest.log");
				table1.add(fhLog.readString("UTF-8"));
			}
		});

		btnTabProfiles = new TextButton(HIDLauncher.getLangBundle().format("MainMenu.btnTabProfiles.text"), skin, "tab");
		btnTabProfiles.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				btnTabUpdates.setDisabled(false);
				btnTabLog.setDisabled(false);
				btnTabProfiles.setDisabled(true);
				btnTabGames.setDisabled(false);
				table1.clear();
				table1.add(HIDLauncher.getLangBundle().format("MainMenu.table1.name")).width(table.getWidth() / 3);
				table1.add(HIDLauncher.getLangBundle().format("MainMenu.table1.game")).width(table.getWidth() / 3);
				table1.add(HIDLauncher.getLangBundle().format("MainMenu.table1.version")).width(table.getWidth() / 3).row();
				for (int i = 0; i < HIDLauncher.profile.getProfiles().size(); i++) {
					String labelName = HIDLauncher.profile.getProfiles().get(i).getName();
					String gameName = HIDLauncher.profile.getProfiles().get(i).getGameName();
					String gameVersion = HIDLauncher.profile.getProfiles().get(i).getUseVersion();
					if (gameVersion == null) {
						gameVersion = "Latest version";
					}

					Label lblName = new Label(labelName, skin);
					Label lblGame = new Label(gameName, skin);
					Label lblVersion = new Label(gameVersion, skin);
					lblName.setName(labelName);
					lblGame.setName(labelName);
					lblVersion.setName(labelName);
					table1.add(lblName);
					table1.add(lblGame);
					table1.add(lblVersion).row();
				}

				table1.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						HIDLauncher.debug(this.getClass().toString(), "switching to EditProfile screen for profile " + table1.hit(x, y, true).toString());
						((Game) Gdx.app.getApplicationListener()).setScreen(new EditProfile(table1.hit(x, y, true).toString()));
					}
				});
			}
		});

		btnTabGames = new TextButton(HIDLauncher.getLangBundle().format("MainMenu.btnTabGames.text"), skin, "tab");
		btnTabGames.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				btnTabUpdates.setDisabled(false);
				btnTabLog.setDisabled(false);
				btnTabProfiles.setDisabled(false);
				btnTabGames.setDisabled(true);
				table1.clear();
				table1.add(HIDLauncher.getLangBundle().format("MainMenu.table1.name")).width(table.getWidth() / 3);
				table1.add(HIDLauncher.getLangBundle().format("MainMenu.table1.version")).width(table.getWidth() / 3);
				table1.add(HIDLauncher.getLangBundle().format("MainMenu.table1.local")).width(table.getWidth() / 3).row();

				String dbName = "HIDLAUNCHER";
				String collectionName = "gameList";
				HIDLauncher.app42.storageServiceFindAllDocuments(dbName, collectionName);
				HIDLauncher.app42.storageGetJsonDocList();
				ArrayList<String> saveNames = HIDLauncher.app42.storageGetSaveValues("name");
				ArrayList<String> saveVersions = HIDLauncher.app42.storageGetSaveValues("version");

				int i;
				for (i = 0; i < saveNames.size(); i++) {
					String lokal = "";
					FileHandle fhGameVersion = Gdx.files.external(".hidlauncher/" + saveNames.get(i) + "/version.txt");
					if (fhGameVersion.exists()) {
						lokal = fhGameVersion.readString();
					}
					Label lblName = new Label(saveNames.get(i), skin);
					Label lblVersion = new Label(saveVersions.get(i), skin);
					Label lblLokal = new Label(lokal, skin);
					lblName.setName(saveNames.get(i));
					lblVersion.setName(saveNames.get(i));
					lblLokal.setName(saveNames.get(i));

					table1.add(lblName);
					table1.add(lblVersion);
					table1.add(lblLokal).row();
				}

				HIDLauncher.log(this.getClass().toString(), "Loaded " + i + " Game(s)");

				table1.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						String gameName = table1.hit(x, y, true).toString();
						HIDLauncher.debug(this.getClass().toString(), "selected game: " + gameName);
						ArrayList<GameProfile> profiles = HIDLauncher.profile.getProfiles();
						for (int i = 0; i < profiles.size(); i++) {
							if (profiles.get(i).getName().equals(HIDLauncher.profile.getSelectedProfile())) {
								profiles.get(i).setGameName(gameName);
								HIDLauncher.gameProfile.setGameName(gameName);
								HIDLauncher.profile.saveProfile(HIDLauncher.profile);
								FileHandle fhGame = Gdx.files.external(".hidlauncher/" + gameName + "/version.txt");
								if (fhGame.exists()) {
									lblInfo.setText(lblInfo.getText() + HIDLauncher.getLangBundle().format("MainMenu.lblInfo.play") + "\n" + gameName);
								} else {
									lblInfo.setText(lblInfo.getText() + HIDLauncher.getLangBundle().format("MainMenu.lblInfo.download") + "\n" + gameName);
								}
							}
						}
						HIDLauncher.app42.userServiceGetAllUsers();
						int usercount = 0;
						usercount = HIDLauncher.app42.userServiceGetUserCount();
						boolean userCreated = false;
						if (usercount != 0) {
							for (int i = 0; i < usercount; i++) {
								if (HIDLauncher.app42.userlistGetUserName(i).equals(HIDLauncher.app42.userGetUserName())) {
									userCreated = true;
								}
							}
						}
						if (userCreated == false) {
							String pwd = Base64Coder.decodeString(Gdx.app.getPreferences(HIDLauncher.TITLE).getString("pass"));
							HIDLauncher.app42.createUser(HIDLauncher.app42.userGetUserName(), pwd, HIDLauncher.app42.userGetEmail());
							HIDLauncher.app42.userServiceAuthenticate(HIDLauncher.app42.userGetUserName(), pwd);
							HIDLauncher.app42.setSessionId(HIDLauncher.app42.userGetSessionId());
							HIDLauncher.app42.userServiceCreateOrUpdateProfile();
							Gdx.app.getPreferences(HIDLauncher.TITLE).putString("sessionIdTts", HIDLauncher.app42.userGetSessionId());
							Gdx.app.getPreferences(HIDLauncher.TITLE).flush();
							HIDLauncher.log(this.getClass().toString(), "created TableTopSimulator user " + HIDLauncher.app42.userGetUserName());
						} else {
							HIDLauncher.app42.getUser(HIDLauncher.app42.userGetUserName());
							HIDLauncher.app42.setSessionId(Gdx.app.getPreferences(HIDLauncher.TITLE).getString("sessionIdTts"));
							HIDLauncher.app42.userServiceCreateOrUpdateProfile();
							HIDLauncher.log(this.getClass().toString(), "updated TableTopSimulator user " + HIDLauncher.app42.userGetUserName());
						}
					}
				});
			}
		});

		// creating selectbox
		HIDLauncher.debug(this.getClass().toString(), "creating selectbox");
		selProfile = new SelectBox<String>(skin);
		ArrayList<String> newItems = new ArrayList<String>();

		if (HIDLauncher.profile.getProfiles() != null) {
			for (int i = 0; i < HIDLauncher.profile.getProfiles().size(); i++) {
				newItems.add(HIDLauncher.profile.getProfiles().get(i).getName());
			}
			String[] data = new String[newItems.size()];
			selProfile.setItems(newItems.toArray(data));
		}

		// creating buttons
		HIDLauncher.debug(this.getClass().toString(), "creating buttons");
		btnNewProfile = new TextButton(HIDLauncher.getLangBundle().format("MainMenu.btnNewProfile.text"), skin);
		btnNewProfile.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				HIDLauncher.debug(this.getClass().toString(), "switching to NewProfile screen");
				dispose();
				((Game) Gdx.app.getApplicationListener()).setScreen(new NewProfile());
			}
		});
		btnNewProfile.pad(10);

		btnOptions = new TextButton(HIDLauncher.getLangBundle().format("MainMenu.btnOptions.text"), skin);
		btnOptions.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				HIDLauncher.debug(this.getClass().toString(), "switching to Options screen");
				dispose();
				((Game) Gdx.app.getApplicationListener()).setScreen(new Options());
			}
		});
		btnOptions.pad(10);

		btnEditProfile = new TextButton(HIDLauncher.getLangBundle().format("MainMenu.btnEditProfile.text"), skin);
		btnEditProfile.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				HIDLauncher.debug(this.getClass().toString(), "switching to EditProfile screen");
				dispose();
				((Game) Gdx.app.getApplicationListener()).setScreen(new EditProfile(HIDLauncher.profile.getSelectedProfile()));
			}
		});
		btnEditProfile.pad(10);

		if (HIDLauncher.gameProfile.getGameName() == null) {
			btnPlay = new TextButton(HIDLauncher.getLangBundle().format("MainMenu.btnPlay.text"), skin);
		} else {
			FileHandle fhGame = Gdx.files.external(".hidlauncher/" + HIDLauncher.gameProfile.getGameName() + "/version.txt");
			if (fhGame.exists()) {
				btnPlay = new TextButton(HIDLauncher.getLangBundle().format("MainMenu.btnPlay.text2"), skin);
				lblInfo.setText(lblInfo.getText() + HIDLauncher.getLangBundle().format("MainMenu.lblInfo.play") + "\n" + HIDLauncher.gameProfile.getGameName());
			} else {
				btnPlay = new TextButton(HIDLauncher.getLangBundle().format("MainMenu.btnPlay.text1"), skin);
				lblInfo.setText(lblInfo.getText() + HIDLauncher.getLangBundle().format("MainMenu.lblInfo.download") + "\n" + HIDLauncher.gameProfile.getGameName());
			}
		}
		btnPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (btnPlay.getText().toString().equals(HIDLauncher.getLangBundle().format("MainMenu.btnPlay.text"))) {
					HIDLauncher.debug(this.getClass().toString(), "switching to select game");
					btnTabUpdates.setDisabled(false);
					btnTabLog.setDisabled(false);
					btnTabProfiles.setDisabled(false);
					btnTabGames.setDisabled(true);
					table1.clear();
					table1.add(HIDLauncher.getLangBundle().format("MainMenu.table1.name")).width(table.getWidth() / 3);
					table1.add(HIDLauncher.getLangBundle().format("MainMenu.table1.version")).width(table.getWidth() / 3);
					table1.add(HIDLauncher.getLangBundle().format("MainMenu.table1.local")).width(table.getWidth() / 3).row();

					String dbName = "HIDLAUNCHER";
					String collectionName = "gameList";
					HIDLauncher.app42.storageServiceFindAllDocuments(dbName, collectionName);
					HIDLauncher.app42.storageGetJsonDocList();
					ArrayList<String> saveNames = HIDLauncher.app42.storageGetSaveValues("name");
					ArrayList<String> saveVersions = HIDLauncher.app42.storageGetSaveValues("version");

					int i;
					for (i = 0; i < saveNames.size(); i++) {
						String lokal = "";

						FileHandle fhGameVersion = Gdx.files.external(".hidlauncher/" + saveNames.get(i) + "/version.txt");
						if (fhGameVersion.exists()) {
							lokal = fhGameVersion.readString();
						}
						Label lblName = new Label(saveNames.get(i), skin);
						Label lblVersion = new Label(saveVersions.get(i), skin);
						Label lblLokal = new Label(lokal, skin);
						lblName.setName(saveNames.get(i));
						lblVersion.setName(saveNames.get(i));
						lblLokal.setName(saveNames.get(i));

						table1.add(lblName);
						table1.add(lblVersion);
						table1.add(lblLokal).row();
					}

					HIDLauncher.log(this.getClass().toString(), "Loaded " + i + " Game(s)");

					table1.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							String gameName = table1.hit(x, y, true).toString();
							HIDLauncher.debug(this.getClass().toString(), "selected game: " + gameName);
							ArrayList<GameProfile> profiles = HIDLauncher.profile.getProfiles();
							for (int i = 0; i < profiles.size(); i++) {
								if (profiles.get(i).getName().equals(HIDLauncher.profile.getSelectedProfile())) {
									profiles.get(i).setGameName(gameName);
									HIDLauncher.gameProfile.setGameName(gameName);
									HIDLauncher.profile.saveProfile(HIDLauncher.profile);
									FileHandle fhGame = Gdx.files.external(".hidlauncher/" + gameName + "/version.txt");
									if (fhGame.exists()) {
										lblInfo.setText(lblInfo.getText() + HIDLauncher.getLangBundle().format("MainMenu.lblInfo.play") + "\n" + gameName);
									} else {
										lblInfo.setText(lblInfo.getText() + HIDLauncher.getLangBundle().format("MainMenu.lblInfo.download") + "\n" + gameName);
									}
								}
							}
						}
					});

				} else if (btnPlay.getText().toString().equals(HIDLauncher.getLangBundle().format("MainMenu.btnPlay.text1"))) {
					HIDLauncher.debug(this.getClass().toString(), "starting download and game");
					String dbName = "HIDLAUNCHER";
					String collectionName = "gameList";
					HIDLauncher.app42.storageServiceFindAllDocuments(dbName, collectionName);
					HIDLauncher.app42.storageGetJsonDocList();
					ArrayList<String> saveNames = HIDLauncher.app42.storageGetSaveValues("name");
					ArrayList<String> saveVersions = HIDLauncher.app42.storageGetSaveValues("version");
					int indexSelectedGame = 0;

					for (int i = 0; i < saveNames.size(); i++) {
						try {
							String name = "", version = "";
							HIDLauncher.app42.storageGetJsonDocList();
							JSONObject json = new JSONObject(HIDLauncher.app42.storageGetJsonDoc(i));
							name = json.getString("name");
							if (name.equals(HIDLauncher.gameProfile.getGameName())) {
								indexSelectedGame = i;
								version = json.getString("version");
								FileHandle fhGamePath = Gdx.files.external(".hidlauncher/" + name);
								fhGamePath.mkdirs();
								FileHandle fhGameVersion = Gdx.files.external(".hidlauncher/" + name + "/version.txt");
								fhGameVersion.writeString(version, false, "UTF-8");
							}
						} catch (Exception e) {
							HIDLauncher.error(this.getClass().toString(), "error creating games list", e);
						}
					}

					String name = "";
					try {
						name = new JSONObject(HIDLauncher.app42.storageGetJsonDoc(indexSelectedGame)).getString("name");
					} catch (JSONException e1) {
						HIDLauncher.error(this.getClass().toString(), "error getting selected game name", e1);
					}
					FileHandle fhGame = Gdx.files.external(".hidlauncher/" + name + "/" + name + ".jar");
					try {
						URL url = null;
						if (name.equals("TableTopSimulator")) {
							url = new URL("https://db.tt/wRWXtYa3");
						}
						HIDLauncher.log(this.getClass().toString(), "downloading new game version: " + fhGame.path());
						Files.copy(url.openStream(), fhGame.file().toPath(), StandardCopyOption.REPLACE_EXISTING);
					} catch (Exception e) {
						HIDLauncher.error(this.getClass().toString(), "error downloading new game version: " + fhGame.path(), e);
					}

					try {
						Process process = Runtime.getRuntime().exec("java -jar " + fhGame.file().getPath());
						process.waitFor();
					} catch (Exception e) {
						HIDLauncher.error(this.getClass().toString(), "error starting game", e);
					}

				} else if (btnPlay.getText().toString().equals(HIDLauncher.getLangBundle().format("MainMenu.btnPlay.text2"))) {
					FileHandle fhGameTts = Gdx.files.external(".hidlauncher/TableTopSimulator/TableTopSimulator.jar");
					try {
						@SuppressWarnings("unused")
						Process process = Runtime.getRuntime().exec("java -jar " + fhGameTts.file().getPath());
					} catch (Exception e) {
						HIDLauncher.error(this.getClass().toString(), "error starting game", e);
					}
				}
			}
		});
		btnPlay.pad(10);

		btnSwitchUser = new TextButton(HIDLauncher.getLangBundle().format("MainMenu.btnSwitchUser.text"), skin);
		btnSwitchUser.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				HIDLauncher.debug(this.getClass().toString(), "switching to Login screen");
				HIDLauncher.app42.userServiceLogout(HIDLauncher.profile.getClientToken());
				HIDLauncher.profile.setSelectedUser(null);
				HIDLauncher.profile.setClientToken(null);
				HIDLauncher.profile.saveProfile(HIDLauncher.profile);
				((Game) Gdx.app.getApplicationListener()).setScreen(new Login());
				dispose();
			}
		});
		btnSwitchUser.pad(10);

		// building ui
		HIDLauncher.debug(this.getClass().toString(), "building ui");
		table.add(table0).colspan(3).width(table.getWidth() / 3);
		table0.add(btnTabUpdates);
		table0.add(btnTabLog);
		table0.add(btnTabProfiles);
		table0.add(btnTabGames);
		table0.add(btnOptions);
		table.row();
		table.add(spLog).colspan(3).expandY().top().row();
		table.add(table2);
		table2.add(lblProfile);
		table2.add(selProfile);
		table.add(lblInfo).colspan(2).row();
		table.add(table3);
		table3.add(btnNewProfile);
		table3.add(btnEditProfile);
		table.add(btnPlay).spaceBottom(15).width(table.getWidth() / 3);
		table.add(btnSwitchUser).right().bottom().spaceBottom(15).width(table.getWidth() / 3);

		if (HIDLauncher.DEBUG) {
			table.debug(); // draw debug lines 
		}
		stage.addActor(table);
	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		HIDLauncher.debug(this.getClass().toString(), "cleaning up MainMenu screen");
		stage.dispose();
		atlas.dispose();
		skin.dispose();
	}

}
