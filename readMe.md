![](https://jitpack.io/v/amitclickapps/facebook-util.svg?style=flat-square)

**How to Use Library**

`FbConnect` is used to call every Method.
Currently this library support only following service.
- Login
- Logout
- Profile

----
`Model` used in above service



##### Login

  `LoginResult` - LoginResult

  `FBException` - FaceBook Exception

##### Logout

`Result` - N/A

`Error` - N/A

##### Profile

`ProfileResult` - Profile Information

`FBException` - FaceBook Exception


----
#### have to set in AndroidManifest
```xml
<!-- Facebook Integration -->
        <activity
            android:name="com.facebook.FacebookActivity"
            android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
            android:label="@string/app_name"
            android:screenOrientation="portrait" />

        <meta-data
            android:name="com.facebook.sdk.ApplicationId"
            android:value="@string/fb_app_id" />
        <meta-data
            android:name="com.facebook.sdk.ApplicationName"
            android:value="@string/app_name" />
```
`Sample` Example

To get `KeyHash`

```
FbConnect.get().getKeyHash(this);
```

To check for active `token`

```
FbConnect.get().getToken(this)
```

`Login Sample`

```
if (TextUtils.isEmpty(FbConnect.get().getToken(this))) {
            FbConnect.with(this, Param.FBAction.LOGIN)
                    .callback(new OnFBCallback<LoginResult, FBException>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.i(getLocalClassName(), "LoginResult = " + loginResult.getAccessToken().getToken());
                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(FBException error) {
                            error.printStackTrace();
                        }
                    })
                    .build();
        }
```


`Profile Sample`

```
Map<String, String> map = new LinkedHashMap<>();
//        map.put(WebEndPoint.ALBUM_ID_KEY, "1549869761707679");  // pass Id of other user if want to check other user profile
        FbConnect.with(this, Param.FBAction.PROFILE)
                .requestMap(map)
                .callback(new FBCallback<ProfileResult, FBException>() {
                    @Override
                    public void onSuccess(ProfileResult profileResult) {
                        Log.i(getLocalClassName(), "Email = " + profileResult.getEmail());
                    }

                    @Override
                    public void onError(FBException fBException) {
                    }
                })
                .build();
```

`OnActivityResult`

```
if (resultCode == Activity.RESULT_OK) {
            if (FacebookSdk.isFacebookRequestCode(requestCode)) {
                //Facebook activity result
                //Do your stuff here
                //Further you can also check if it's login or Share etc by using
                //CallbackManagerImpl as explained by rajath's answer here
                if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
                    //login
                    Log.i(getLocalClassName(), "Login");
                    FbConnect.get().onActivityResult(requestCode, resultCode, data);
                }
            }
        }
```

Download
--------
Add the JitPack repository to your root build.gradle:

![](https://jitpack.io/v/amitclickapps/facebook-util.svg?style=flat-square)


```groovy
	allprojects {
		repositories {
			maven { url "https://jitpack.io" }
		}
	}
```
App Gradle
```groovy
  repositories {
    jcenter()
}
```
Add the Gradle dependency:
```groovy
	dependencies {
		compile 'com.github.amitclickapps:facebook-util:latest'
	}
```