package at.hid.hidlauncher.desktop.api;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import at.hid.hidlauncher.HIDLauncher;
import at.hid.hidlauncher.api.App42;

import com.shephertz.app42.paas.sdk.java.App42API;
import com.shephertz.app42.paas.sdk.java.App42Exception;
import com.shephertz.app42.paas.sdk.java.ServiceAPI;
import com.shephertz.app42.paas.sdk.java.storage.Storage;
import com.shephertz.app42.paas.sdk.java.storage.StorageService;
import com.shephertz.app42.paas.sdk.java.user.User;
import com.shephertz.app42.paas.sdk.java.user.UserService;


public class DesktopApp42 implements App42 {
	private UserService userService;
	private StorageService storageService;
	private ArrayList<User> userlist = null;
	private User user;
	private Storage storage = null;
	private ArrayList<Storage.JSONDocument> jsonDocList = null;
	private ServiceAPI serviceAPITts;
	private UserService userServiceTts;
	private ArrayList<User> allUsersTts;
	private User userTts;
	private ServiceAPI serviceAPIHvr;
	private UserService userServiceHvr;
	private ArrayList<User> allUsersHvr;
	private User userHvr;

	public DesktopApp42() {
	}

	@Override
	public void initialize(String apiKey, String secretKey) {
		App42API.initialize(apiKey, secretKey);
	}

	@Override
	public void buildUserService() {
		userService = App42API.buildUserService();
	}
	
	@Override
	public void buildStorageService() {
		 storageService = App42API.buildStorageService();
	}
	
	@Override
	public void getUser(String username) {
		user = userService.getUser(username);
	}
	
	@Override
	public void getUserByMail(String mail) {
		user = userService.getUserByEmailId(mail);
	}
	
	@Override
	public void createUser(String uName, String pwd, String emailAddress) {
		user = userService.createUser(uName, pwd, emailAddress);
	}
	
	@Override
	public void setSessionId(String sid) {
		user.setSessionId(sid);
		userService.setSessionId(sid);
		storageService.setSessionId(sid);
	}
	
	@Override
	public void userServiceSetOtherMetaHeaders(HashMap<String, String> otherMetaHeaders) {
		userService.setOtherMetaHeaders(otherMetaHeaders);
	}
	
	@Override
	public boolean userServiceAuthenticate(String uName, String pwd) {
		try {
		HashMap<String, String> otherMetaHeaders = new HashMap<String, String>();
		otherMetaHeaders.put("emailAuth", "true");
		userService.setOtherMetaHeaders(otherMetaHeaders);
			user = userService.authenticate(uName, pwd);
			HIDLauncher.profile.setClientToken(user.getSessionId());
		} catch (App42Exception e) {
			return false;
		}
		if (user != null && user.getEmail() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void userServiceLogout(String sessionId) {
		userService.logout(sessionId);
	}
	
	@Override
	public void userServiceGetAllUsers() {
		try {
			userlist = userService.getAllUsers();
		} catch (App42Exception e) {
			int errorCode = e.getAppErrorCode();
			switch (errorCode) {
				case 2006:

					break;
			}
		}
	}
	
	@Override
	public int userServiceGetUserCount() {
		if (userlist != null) {
			return userlist.size();
		} else {
			return 0;
		}
	}
	
	@Override
	public void userServiceCreateOrUpdateProfile() {
		userService.createOrUpdateProfile(user);
	}
	
	@Override
	public String userGetSessionId() {
		return user.getSessionId();
	}
	
	@Override
	public String userGetUserName() {
		return user.getUserName();
	}
	
	@Override
	public String userGetEmail() {
		return user.getEmail();
	}
	
	@Override
	public String userGetFirstName() {
		return user.getProfile().getFirstName();
	}
	
	@Override
	public void userSetFirstName(String displayName) {
		user.getProfile().setFirstName(displayName);
	}
	
	@Override
	public String userlistGetUserName(int i) {
		return userlist.get(i).getUserName();
	}
	
	@Override
	public String userlistGetUserMail(int i) {
		return userlist.get(i).getEmail();
	}
	
	@Override
	public String userlistGetUserDisplayName(int i) {
		return userlist.get(i).getProfile().getFirstName();
	}
	
	@Override
	public void storageServiceFindDocumentByKeyValue(String dbName, String collectionName, String key, String value) {
		storage = storageService.findDocumentByKeyValue(dbName, collectionName, key, value);
	}
	
	@Override
	public void storageServiceDeleteDocumentsByKeyValue(String dbName, String collectionName, String key, String value) {
		storageService.deleteDocumentsByKeyValue(dbName, collectionName, key, value);
	}
	
	@Override
	public void storageServiceFindAllDocuments(String dbName, String collectionName) {
		storage = storageService.findAllDocuments(dbName, collectionName);
	}
	
	@Override
	public void storageGetJsonDocList() {
		if (storage != null) {
			jsonDocList = storage.getJsonDocList();
		}
	}
	
	@Override
	public ArrayList<String> storageGetSaveValues(String key) {
		ArrayList<String> saveValues = new ArrayList<String>();
		for (int i = 0; i < jsonDocList.size(); i++) {
			JSONObject json;
			try {
				json = new JSONObject(jsonDocList.get(i).getJsonDoc());
				saveValues.add(json.getString(key));
			} catch (JSONException e) {
				HIDLauncher.error(this.getClass().toString(), "error reading JSONObject", e);
			}
		}
		return saveValues;
	}
	
	@Override
	public void storageServiceInsertJSONDocument(String dbName, String collectionName, JSONObject json) {
		storageService.insertJSONDocument(dbName, collectionName, json);
	}
	
	@Override
	public void storageServiceUpdateDocumentByKeyValue(String dbName, String collectionName, String key, String value, JSONObject newJsonDoc) {
		storageService.updateDocumentByKeyValue(dbName, collectionName, key, value, newJsonDoc);
	}
	
	@Override
	public String storageGetJsonDoc(int i) {
		return storage.getJsonDocList().get(i).getJsonDoc();
	}
	
	@Override
	public void serviceAPITts() {
		serviceAPITts = new ServiceAPI("_e4afdcbaee7153a5a87a561eec51f33c87cbf5758beb30b39926b4135d37e3fe", "cad8aef024fe2ab6d0d2ecb6258b0d0a49d6185fde89abca519aac1226c8c5c6");
	}

	@Override
	public void buildUserServiceTts() {
		userServiceTts = serviceAPITts.buildUserService();
	}
	
	@Override
	public void userServiceTtsGetAllUsers() {
		try {
			allUsersTts = userServiceTts.getAllUsers();
		} catch (App42Exception e) {
			int errorCode = e.getAppErrorCode();
			switch (errorCode) {
				case 2006:

					break;
			}
		}
	}
	
	@Override
	public boolean userServiceTtsIsUserCreated() {
		if (allUsersTts != null) {
			for (int i = 0; i < allUsersTts.size(); i++) {
				if (allUsersTts.get(i).getUserName().equals(user.getUserName())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void userServiceTtsCreateUser(String uName, String pwd, String emailAddress) {
		userTts = userServiceTts.createUserWithProfile(uName, pwd, emailAddress, user.getProfile());
	}
	
	@Override
	public void userServiceTtsAuthenticate(String uName, String pwd) {
		userTts = userServiceTts.authenticateAndCreateSession(uName, pwd);
	}
	
	@Override
	public void userServiceTtsSetSessionId(String sid) {
		userServiceTts.setSessionId(sid);
	}
	
	@Override
	public void userServiceTtsCreateOrUpdateProfile() {
		userTts = userServiceTts.createOrUpdateProfile(user);
	}
	
	@Override
	public String userServiceTtsGetSessionId() {
		return userServiceTts.getSessionId();
	}
	
	@Override
	public void userServiceTtsGetUser(String uName) {
		userTts = userServiceTts.getUser(uName);
	}
	
	@Override
	public void serviceAPIHvr() {
		serviceAPIHvr = new ServiceAPI("_4d60dcb2c8d364ccb28f551571b3a76e55d1136011575699db2d0525c9387d52", "050720a0c872c6103432575d5e5873b4468a1e9336d48908b1ce831ea6cc8395");
	}

	@Override
	public void buildUserServiceHvr() {
		userServiceHvr = serviceAPIHvr.buildUserService();
	}
	
	@Override
	public void userServiceHvrGetAllUsers() {
		try {
			allUsersHvr = userServiceHvr.getAllUsers();
		} catch (App42Exception e) {
			int errorCode = e.getAppErrorCode();
			switch (errorCode) {
				case 2006:
					
					break;
			}
		}
	}
	
	@Override
	public boolean userServiceHvrIsUserCreated() {
		if (allUsersHvr != null) {
			for (int i = 0; i < allUsersHvr.size(); i++) {
				if (allUsersHvr.get(i).getUserName().equals(user.getUserName())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void userServiceHvrCreateUser(String uName, String pwd, String emailAddress) {
		userHvr = userServiceHvr.createUserWithProfile(uName, pwd, emailAddress, user.getProfile());
	}
	
	@Override
	public void userServiceHvrAuthenticate(String uName, String pwd) {
		userHvr = userServiceHvr.authenticateAndCreateSession(uName, pwd);
	}
	
	@Override
	public void userServiceHvrSetSessionId(String sid) {
		userServiceHvr.setSessionId(sid);
	}
	
	@Override
	public void userServiceHvrCreateOrUpdateProfile() {
		userHvr = userServiceHvr.createOrUpdateProfile(user);
	}
	
	@Override
	public String userServiceHvrGetSessionId() {
		return userServiceHvr.getSessionId();
	}
	
	@Override
	public void userServiceHvrGetUser(String uName) {
		userHvr = userServiceHvr.getUser(uName);
	}
	
	public UserService getUserService() {
		return userService;
	}
	
	public StorageService getStorageService() {
		return storageService;
	}
	
	public User getUser() {
		return user;
	}
	
	public Storage getStorage() {
		return storage;
	}
	
	public ServiceAPI getServiceAPITts() {
		return serviceAPITts;
	}
	
	public User getUserTts() {
		return userTts;
	}
	
	public ServiceAPI getServiceAPIHvr() {
		return serviceAPIHvr;
	}
	
	public User getUserHvr() {
		return userHvr;
	}
}
