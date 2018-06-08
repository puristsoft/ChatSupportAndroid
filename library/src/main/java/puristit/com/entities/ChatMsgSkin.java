package puristit.com.entities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

/**
 * Created by Anas on 4/21/2018.
 */

public class ChatMsgSkin {

    int backgroundColor = Color.parseColor("#03A9F4");
    Drawable backgroundDrawable = null;
    int textColor = Color.parseColor("#FFFFFF");

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public ChatMsgSkin setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Drawable getBackgroundDrawable() {
        return backgroundDrawable;
    }

    public ChatMsgSkin setBackgroundDrawable(Drawable backgroundDrawable) {
        this.backgroundDrawable = backgroundDrawable;
        return this;
    }

    public int getTextColor() {
        return textColor;
    }

    public ChatMsgSkin setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }
}
