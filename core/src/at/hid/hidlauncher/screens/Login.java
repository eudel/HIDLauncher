/**
 * 
 */
package at.hid.hidlauncher.screens;

import java.util.ArrayList;
import java.util.HashMap;

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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * @author dunkler_engel
 *
 */
public class Login implements Screen {

	private Stage stage;
	private TextureAtlas atlas;
	private Skin skin;
	private Table table;
	private Label lblHeading, lblMail, lblPass;
	private TextButton btnLogin, btnRegister;
	private TextField txtMail, txtPass;

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
		HIDLauncher.debug(this.getClass().toString(), "creating Login screen");
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
		lblHeading = new Label(HIDLauncher.getLangBundle().format("Login.lblHeading.text"), skin);

		// creating labels
		HIDLauncher.debug(this.getClass().toString(), "creating labels");
		lblMail = new Label(HIDLauncher.getLangBundle().format("Login.lblMail.text"), skin);
		lblPass = new Label(HIDLauncher.getLangBundle().format("Login.lblPass.text"), skin);

		// creating textfields
		txtMail = new TextField("", skin);
		txtPass = new TextField("", skin);
		txtPass.setPasswordMode(true);
		txtPass.setPasswordCharacter('*');

		// creating buttons
		HIDLauncher.debug(this.getClass().toString(), "creating buttons");
		btnLogin = new TextButton(HIDLauncher.getLangBundle().format("Login.btnLogin.text"), skin);
		btnLogin.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.getPreferences(HIDLauncher.TITLE).putString("pass", Base64Coder.encodeString(txtPass.getText()));
				Gdx.app.getPreferences(HIDLauncher.TITLE).flush();
				doLogin(txtMail.getText(), txtPass.getText());
				((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
			}
		});
		btnLogin.pad(10);

		btnRegister = new TextButton(HIDLauncher.getLangBundle().format("Login.btnRegister.text"), skin);
		btnRegister.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				HIDLauncher.debug(this.getClass().toString(), "switching to Register screen");
				Gdx.net.openURI("http://hidlauncher.hid-online.at/register.php");
			}
		});
		btnRegister.pad(10);

		// building ui
		HIDLauncher.debug(this.getClass().toString(), "building ui");
		table.add(lblHeading).spaceBottom(100).row();
		table.add(lblMail).spaceBottom(15).row();
		table.add(txtMail).width(500).spaceBottom(15).row();
		table.add(lblPass).spaceBottom(15).row();
		table.add(txtPass).width(500).spaceBottom(15).row();
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
		HIDLauncher.debug(this.getClass().toString(), "cleaning up Login screen");
		stage.dispose();
		atlas.dispose();
		skin.dispose();
	}

	public void doLogin(String user, String pass) {
		HashMap<String, String> otherMetaHeaders = new HashMap<String, String>();
		otherMetaHeaders.put("emailAuth", "true");
		otherMetaHeaders.put("userProfile", "true");
		HIDLauncher.app42.userServiceSetOtherMetaHeaders(otherMetaHeaders);
		HIDLauncher.app42.userServiceAuthenticate(txtMail.getText(), txtPass.getText());
		HIDLauncher.profile.setClientToken(HIDLauncher.app42.userGetSessionId());

		String uuid = HIDLauncher.app42.userGetUserName();
		if (!uuid.isEmpty()) {
			HIDLauncher.profile.setSelectedUser(uuid);
			HIDLauncher.playerProfile.setDisplayName(HIDLauncher.app42.userGetFirstName());

			ArrayList<PlayerProfile> authenticationDB = new ArrayList<PlayerProfile>();
			authenticationDB.add(new PlayerProfile(HIDLauncher.profile.getSelectedProfile(), "", uuid, HIDLauncher.profile.getSelectedProfile()));
			HIDLauncher.profile.setAuthenticationDB(authenticationDB);
			Json json = new Json();

			try {
				FileHandle fhLauncherProfiles = Gdx.files.external(".hidlauncher/launcher_profiles.json");
				String profileAsText = json.prettyPrint(HIDLauncher.profile);
				fhLauncherProfiles.writeString(profileAsText, false, "UTF-8");
			} catch (Exception e) {
				HIDLauncher.error(this.getClass().getName(), "error writing launcher_profiles.json file", e);
			}

		}
	}
}
