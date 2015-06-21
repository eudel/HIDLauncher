/**
 * 
 */
package at.hid.hidlauncher.screens;

import java.awt.Desktop;
import java.io.IOException;
import java.util.ArrayList;

import at.hid.hidlauncher.GameProfile;
import at.hid.hidlauncher.HIDLauncher;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * @author dunkler_engel
 *
 */
public class EditProfile implements Screen {

	private Stage stage;
	private TextureAtlas atlas;
	private Skin skin;
	private Table table;
	private Label lblHeading, lblName, lblGame;
	private TextButton btnCancel, btnOpenDir, btnSaveProfile;
	private TextField txtName, txtResX, txtResY;
	private SelectBox<String> sbGame, sbLauncherVisibility, sbUseVersion;
	private CheckBox cbResolution, cbAskAssistance, cbLauncherVisibility, cbUseVersion;

	private boolean launcherVisibilityDisabled = true, useVersionDisabled = true;
	private String nameProfile;
	private GameProfile profile;

	public EditProfile(String name) {
		profile = HIDLauncher.gameProfile.loadGameProfile(name);
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
		HIDLauncher.debug(this.getClass().toString(), "creating EditProfile screen");
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
		lblHeading = new Label(HIDLauncher.getLangBundle().format("EditProfile.lblHeading.text"), skin);

		// creating labels
		HIDLauncher.debug(this.getClass().toString(), "creating labels");
		lblName = new Label(HIDLauncher.getLangBundle().format("EditProfile.lblName.text"), skin);
		lblGame = new Label(HIDLauncher.getLangBundle().format("EditProfile.lblGame.text"), skin);

		// creating textfields
		txtName = new TextField(profile.getName(), skin);
		if (profile.getResolutionX() != 0) {
			txtResX = new TextField(Integer.toString(profile.getResolutionX()), skin);
			txtResY = new TextField(Integer.toString(profile.getResolutionY()), skin);
		} else {
			txtResX = new TextField("960", skin);
			txtResY = new TextField("540", skin);
		}

		// creating selectboxes
		HIDLauncher.debug(this.getClass().toString(), "creating selectboxes");
		sbGame = new SelectBox<String>(skin);
		sbLauncherVisibility = new SelectBox<String>(skin);
		sbUseVersion = new SelectBox<String>(skin);

		ArrayList<String> newItems = new ArrayList<String>();
		String dbName = "HIDLAUNCHER";
		String collectionName = "gameList";
		HIDLauncher.app42.storageServiceFindAllDocuments(dbName, collectionName);
		HIDLauncher.app42.storageGetJsonDocList();
		newItems = HIDLauncher.app42.storageGetSaveValues("name");
		int indexSelectedGame = 0;
		
		for (int i = 0; i < newItems.size(); i++) {
			if(newItems.get(i).equals(HIDLauncher.gameProfile.getGameName())) {
				indexSelectedGame = i;
			}
		}
		
		String[] data = new String[newItems.size()];
		sbGame.setItems(newItems.toArray(data));
		sbGame.setSelectedIndex(indexSelectedGame);
		
		
		newItems.clear();
		newItems.add(HIDLauncher.getLangBundle().format("EditProfile.sbLauncherVisibility.text"));
		newItems.add(HIDLauncher.getLangBundle().format("EditProfile.sbLauncherVisibility.text1"));
		newItems.add(HIDLauncher.getLangBundle().format("EditProfile.sbLauncherVisibility.text2"));
		data = new String[newItems.size()];
		sbLauncherVisibility.setItems(newItems.toArray(data));
		if (profile.getLauncherVisibility() != 0) {
			sbLauncherVisibility.setSelectedIndex(profile.getLauncherVisibility());
		} else {
			sbLauncherVisibility.setSelectedIndex(0);
		}

		newItems.clear();
		newItems.add(HIDLauncher.getLangBundle().format("EditProfile.sbUseVersion.text"));
		data = new String[newItems.size()];
		sbUseVersion.setItems(newItems.toArray(data));

		// creating checkboxes
		HIDLauncher.debug(this.getClass().toString(), "creating checkboxes");
		cbResolution = new CheckBox(HIDLauncher.getLangBundle().format("EditProfile.cbResolution.text"), skin);
		cbAskAssistance = new CheckBox(HIDLauncher.getLangBundle().format("EditProfile.cbAskAssistance.text"), skin);
		cbLauncherVisibility = new CheckBox(HIDLauncher.getLangBundle().format("EditProfile.cbLauncherVisibility.text"), skin);
		cbUseVersion = new CheckBox(HIDLauncher.getLangBundle().format("EditProfile.cbUseVersion.text"), skin);

		if (profile.getResolutionX() == 0) {
			txtResX.setDisabled(true);
		} else {
			cbResolution.setChecked(true);
		}
		if (profile.getResolutionY() == 0) {
			txtResY.setDisabled(true);
		} else {
			cbResolution.setChecked(true);
		}
		if (profile.isAskAssistance()) {
			cbAskAssistance.setChecked(true);
		}
		if (profile.getLauncherVisibility() == 0) {
			sbLauncherVisibility.setDisabled(true);
		} else {
			cbLauncherVisibility.setChecked(true);
		}
		if (profile.getUseVersion() == null) {
			sbUseVersion.setDisabled(true);
		} else {
			cbUseVersion.setChecked(true);
		}

		cbResolution.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!txtResX.isDisabled()) {
					txtResX.setDisabled(true);
					txtResY.setDisabled(true);
				} else {
					txtResX.setDisabled(false);
					txtResY.setDisabled(false);
				}
			}
		});

		cbLauncherVisibility.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (launcherVisibilityDisabled == false) {
					sbLauncherVisibility.setDisabled(true);
					launcherVisibilityDisabled = true;
				} else {
					sbLauncherVisibility.setDisabled(false);
					launcherVisibilityDisabled = false;
				}
			}
		});

		cbUseVersion.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (useVersionDisabled == false) {
					sbUseVersion.setDisabled(true);
					useVersionDisabled = true;
				} else {
					sbUseVersion.setDisabled(false);
					useVersionDisabled = false;
				}
			}
		});

		// creating buttons
		HIDLauncher.debug(this.getClass().toString(), "creating buttons");
		btnCancel = new TextButton(HIDLauncher.getLangBundle().format("EditProfile.btnCancel.text"), skin);
		btnCancel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				HIDLauncher.debug(this.getClass().toString(), "switching to MainMenu screen");
				((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
			}
		});
		btnCancel.pad(10);

		btnOpenDir = new TextButton(HIDLauncher.getLangBundle().format("EditProfile.btnOpenDir.text"), skin);
		btnOpenDir.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				HIDLauncher.debug(this.getClass().toString(), "open launcher dir");
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().open(Gdx.files.external(".hidlauncher/").file());
					} catch (IOException e) {
						HIDLauncher.error(this.getClass().toString(), "error opening Launcher Dir", e);
					}
				}
			}
		});
		btnOpenDir.pad(10);
		if (!Desktop.isDesktopSupported()) {
			btnOpenDir.setVisible(false);
		}

		btnSaveProfile = new TextButton(HIDLauncher.getLangBundle().format("EditProfile.btnSaveProfile.text"), skin);
		btnSaveProfile.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				HIDLauncher.debug(this.getClass().toString(), "saving profile, switching to MainMenu screen");
				profile.setName(txtName.getText());
				profile.setGameName(sbGame.getSelected());
				if (cbResolution.isChecked()) {
					if (!txtResX.getText().equals("960")) {
						profile.setResolutionX(Integer.parseInt(txtResX.getText()));
					} else {
						profile.setResolutionX(0);
					}
					if (!txtResY.getText().equals("540")) {
						profile.setResolutionY(Integer.parseInt(txtResY.getText()));
					} else {
						profile.setResolutionY(0);
					}
				} else {
					profile.setResolutionX(0);
					profile.setResolutionY(0);
				}
				if (cbAskAssistance.isChecked()) {
					profile.setAskAssistance(true);
				} else {
					profile.setAskAssistance(false);
				}
				if (cbLauncherVisibility.isChecked()) {
					if (!sbLauncherVisibility.getSelected().equals(HIDLauncher.getLangBundle().format("EditProfile.sbLauncherVisibility.text"))) {
						profile.setLauncherVisibility(sbLauncherVisibility.getSelectedIndex());
					} else {
						profile.setLauncherVisibility(0);
					}
				} else {
					profile.setLauncherVisibility(0);
				}
				if (cbUseVersion.isChecked()) {
					if (!sbUseVersion.getSelected().equals(HIDLauncher.getLangBundle().format("EditProfile.sbUseVersion.text"))) {
						profile.setUseVersion(sbUseVersion.getSelected());
					} else {
						profile.setUseVersion(null);
					}
				} else {
					profile.setUseVersion(null);
				}
				for (int i = 0; i < HIDLauncher.profile.getProfiles().size(); i++) {
					if (HIDLauncher.profile.getProfiles().get(i).getName().equals(nameProfile)) {
						HIDLauncher.profile.getProfiles().set(i, profile);
					}
				}
				HIDLauncher.profile.saveProfile(HIDLauncher.profile);
				dispose();
				((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
			}
		});
		btnSaveProfile.pad(10);

		// building ui
		HIDLauncher.debug(this.getClass().toString(), "building ui");
		table.add().width(table.getWidth() / 2);
		table.add().width(table.getWidth() / 4);
		table.add().width(table.getWidth() / 4).row();
		table.add(lblHeading).colspan(3).spaceBottom(15).row();
		table.add(lblName).left().spaceBottom(15);
		table.add(txtName).colspan(2).width(400).spaceBottom(15).row();
		table.add(lblGame).left().spaceBottom(15);
		table.add(sbGame).width(400).colspan(2).spaceBottom(15).row();
		table.add(cbResolution).left().spaceBottom(15);
		table.add(txtResX).spaceBottom(15);
		table.add(txtResY).spaceBottom(15).row();
		table.add(cbAskAssistance).left().colspan(3).spaceBottom(15).row();
		table.add(cbLauncherVisibility).left().spaceBottom(15);
		table.add(sbLauncherVisibility).width(400).colspan(2).spaceBottom(15).row();
		table.add(cbUseVersion).left().spaceBottom(15);
		table.add(sbUseVersion).width(400).colspan(2).spaceBottom(15).row();
		table.add(btnCancel).spaceBottom(15);
		table.add(btnOpenDir).spaceBottom(15);
		table.add(btnSaveProfile).spaceBottom(15);
		if (HIDLauncher.DEBUG) {
			table.debug(); // draw debug lines
		}
		stage.addActor(table);
		stage.setKeyboardFocus(txtName);
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
		HIDLauncher.debug(this.getClass().toString(), "cleaning up EditProfile screen");
		stage.dispose();
		atlas.dispose();
		skin.dispose();
	}

}
