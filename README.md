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



### Initialize APi
Call initialize Api to create new chat url, user name and password must be used from the result of the register Api

```gradle
purisitChat.initialize(p_username, p_password, GCM_ID, new ResponseListener() {
                @Override
                public void onSuccessResponse(ServerResponse response) {
                    vRemoveProgressDialog();
                    JSONObject result = ((JSONObject) response.getData()).optJSONObject("result");
                    String chatUrl = result.optString("chat_url");
                }

                @Override
                public void onFailedResponse(ServerResponse response) {
                    vRemoveProgressDialog();
                    Toast.makeText(MainActivity.this, "Initializing Failed", Toast.LENGTH_SHORT).show();
                }
            });
```


### Chat Url Builder
Url builder is used to generate url to use in the ChatView

```gradle
  PurisitChat.URLBuilder builder = new PurisitChat.URLBuilder(chat_url)
                    .setGCM_Id(GCM_ID)
                    .setRoomlistEnabled(chSelectDepartment.isChecked())
                    .setRoom("Sales")
                    .setHeadercolor(getResources().getColor(R.color.colorPrimary));
```


### Using the ChatView
add the ChatView to the Xml of your activity

```gradle
  <puristit.com.widget.ChatView
        android:id="@+id/chatView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

then load the Chat url obtained from the URL builder to start the chatview
```gradle
ChatView chatView = (ChatView)findViewById(R.id.chatView);
        chatView.setChatViewListener(new ChatViewListener() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // called when the Page starts loading
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // called when the Page finish loading
            }

            @Override
            public void onChatViewDismiss() {
                //called when the Done button is called to indicate that the Chat view must be closed
            }

            @Override
            public void onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams                                  fileChooserParams) {
                //
            }

            @Override
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

            }
        });
        chatView.loadUrl(chatUrl);
```
