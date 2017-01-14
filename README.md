# ChatSupportAndroid
# Purist-it www.puristit.com 
## Android version of using Purist-it Chat Support API


## Downloads
 * **[Puristit SDK.jar](https://github.com/puristsoft/ChatSupportAndroid/tree/master/library/release/PuristitSDK.jar)**


## Usage

Create new Instance of the SDK

```gradle
PurisitChat purisitChat = PurisitChat.getInstance(context, "<YOUR_PURIST_CLIENT_KEY>");
```
### Register APi
Call register Api to create new user on Purist account

```gradle
purisitChat.register("UserName", "Password", new ResponseListener() {
            @Override
            public void onSuccessResponse(ServerResponse response) {
                JSONObject result = ((JSONObject)response.getData()).optJSONObject("result");
                String userName = result.optString("p_username");
                String pass = result.optString("p_password");
            }

            @Override
            public void onFailedResponse(ServerResponse response) {
                super.onFailedResponse(response);
                Toast.makeText(MainActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        });
```

