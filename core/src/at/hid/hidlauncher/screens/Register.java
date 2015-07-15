/**
 * 
 */
package at.hid.hidlauncher.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import at.hid.hidlauncher.HIDLauncher;
import at.hid.hidlauncher.PlayerProfile;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * @author dunkler_engel
 *
 */
public class Register implements Screen {

	private Stage stage;
	private TextureAtlas atlas;
	private Skin skin;
	private Table table;
	private Label lblHeading, lblMail, lblPass, lblDisplayName;
	private TextButton btnLogin, btnRegister;
	private TextField txtMail, txtPass, txtDisplayName;

	private String mail, pass;

	public Register(String mail, String pass) {
		this.mail = mail;
		this.pass = pass;
	}

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
		HIDLauncher.debug(this.getClass().toString(), "creating Register screen");
		stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

		Gdx.input.setInputProcessor(stage);

		// creating skin
		HIDLauncher.debug(this.getClass().toString(), "creating skin");
		atlas = new TextureAtlas("ui/atlas.pack");
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);

		table = new Table(skin);
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// creating heading
		HIDLauncher.debug(this.getClass().toString(), "creating heading");
		lblHeading = new Label(HIDLauncher.getLangBundle().format("Register.lblHeading.text"), skin);

		// creating labels
		HIDLauncher.debug(this.getClass().toString(), "creating labels");
		lblMail = new Label(HIDLauncher.getLangBundle().format("Register.lblMail.text"), skin);
		lblPass = new Label(HIDLauncher.getLangBundle().format("Register.lblPass.text"), skin);
		lblDisplayName = new Label(HIDLauncher.getLangBundle().format("Register.lblDisplayName.text"), skin);

		// creating textfields
		txtMail = new TextField(mail, skin);
		txtPass = new TextField(pass, skin);
		txtPass.setPasswordMode(true);
		txtPass.setPasswordCharacter('*');
		txtDisplayName = new TextField("", skin);

		// creating buttons
		HIDLauncher.debug(this.getClass().toString(), "creating buttons");
		btnLogin = new TextButton(HIDLauncher.getLangBundle().format("Register.btnLogin.text"), skin);
		btnLogin.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new Login(txtMail.getText(), txtPass.getText()));
			}
		});
		btnLogin.pad(10);

		btnRegister = new TextButton(HIDLauncher.getLangBundle().format("Register.btnRegister.text"), skin);
		btnRegister.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				HIDLauncher.debug(this.getClass().toString(), "registering user " + txtDisplayName.getText());
				if (HIDLauncher.inetConnection()) {
					Gdx.app.getPreferences(HIDLauncher.TITLE).putString("pass", Base64Coder.encodeString(txtPass.getText()));
					Gdx.app.getPreferences(HIDLauncher.TITLE).flush();
					doRegister(txtMail.getText(), txtPass.getText(), txtDisplayName.getText());
					((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
				}
			}
		});
		btnRegister.pad(10);

		HIDLauncher.app42.userServiceGetAllUsers();
		for (int i = 0; i < HIDLauncher.app42.userServiceGetUserCount(); i++) {
			if (HIDLauncher.app42.userlistGetUserMail(i).equals(txtMail.getText())) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new Login(mail, pass));
			}
		}

		// building ui
		HIDLauncher.debug(this.getClass().toString(), "building ui");
		table.add(lblHeading).spaceBottom(100).row();
		table.add(lblMail).spaceBottom(15).row();
		table.add(txtMail).width(500).spaceBottom(15).row();
		table.add(lblPass).spaceBottom(15).row();
		table.add(txtPass).width(500).spaceBottom(15).row();
		table.add(lblDisplayName).spaceBottom(15).row();
		table.add(txtDisplayName).width(500).spaceBottom(15).row();
		HorizontalGroup hg1 = new HorizontalGroup();
		hg1.addActor(btnRegister);
		hg1.addActor(btnLogin);
		table.add(hg1).spaceBottom(15).row();
		if (HIDLauncher.DEBUG) {
			table.debug(); // draw debug lines
		}
		stage.addActor(table);
		stage.setKeyboardFocus(txtMail);
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
		HIDLauncher.debug(this.getClass().toString(), "cleaning up Register screen");
		stage.dispose();
		atlas.dispose();
		skin.dispose();
	}

	public void doRegister(String mail, String pass, String displayName) {
		String user = UUID.randomUUID().toString();

		HIDLauncher.app42.createUser(user, pass, mail);
		HIDLauncher.app42.getUserByMail(mail);
		HIDLauncher.app42.userSetFirstName(displayName);
		HIDLauncher.app42.userServiceCreateOrUpdateProfile();

		HIDLauncher.profile.setSelectedUser(user);
		HashMap<String, String> otherMetaHeaders = new HashMap<String, String>();
		otherMetaHeaders.put("emailAuth", "true");
		otherMetaHeaders.put("userProfile", "true");
		HIDLauncher.app42.userServiceSetOtherMetaHeaders(otherMetaHeaders);
		HIDLauncher.app42.userServiceAuthenticate(txtMail.getText(), txtPass.getText());
		HIDLauncher.profile.setClientToken(HIDLauncher.app42.userGetSessionId());
		HIDLauncher.playerProfile.setDisplayName(displayName);

		ArrayList<PlayerProfile> authenticationDB = new ArrayList<PlayerProfile>();

		FileHandle fhLauncherProfiles = null;
		if (Gdx.files.isExternalStorageAvailable()) {
			fhLauncherProfiles = Gdx.files.external(".hidlauncher/launcher_profiles.json");
		} else {
			fhLauncherProfiles = Gdx.files.internal(".hidlauncher/launcher_profiles.json");
		}
		if (fhLauncherProfiles.exists()) {
			try {
				JSONObject launcherProfilesJSON = new JSONObject(fhLauncherProfiles.readString("UTF-8"));
				JSONArray authenticationDBJSON = new JSONArray(launcherProfilesJSON.get("authenticationDB").toString());
				for (int i = 0; i < authenticationDBJSON.length(); i++) {
					JSONObject authenticationDBEntryJSON = authenticationDBJSON.getJSONObject(i);
					authenticationDB.add(new PlayerProfile(authenticationDBEntryJSON.getString("username"), authenticationDBEntryJSON.getString("accessToken"), authenticationDBEntryJSON.getString("uuid"), authenticationDBEntryJSON.getString("displayName")));
				}
			} catch (Exception e) {
				HIDLauncher.error(this.getClass().toString(), "error reading authenticationDB", e);
			}
		}

		authenticationDB.add(new PlayerProfile(displayName, "", user, displayName));
		HIDLauncher.profile.setAuthenticationDB(authenticationDB);

		HIDLauncher.profile.saveProfile();
	}
}
