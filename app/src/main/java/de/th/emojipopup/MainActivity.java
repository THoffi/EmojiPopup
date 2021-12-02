package de.th.emojipopup;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import de.th.emoji_popup_library.Actions.EmojIconActions;
import de.th.emoji_popup_library.Helper.EmojiconEditText;
import de.th.emoji_popup_library.Helper.EmojiconTextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View rootView = findViewById(R.id.root_view);

        ImageView emojiButton = findViewById(R.id.emoji_btn);
        ImageView submitButton = findViewById(R.id.submit_btn);

        EmojiconEditText emojiconEditText =  findViewById(R.id.emojicon_edit_text);
        EmojiconTextView textView =  findViewById(R.id.textView);
        EmojIconActions emojIcon = new EmojIconActions(this, rootView, emojiconEditText, emojiButton);
        emojIcon.ShowEmojIcon();

        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });

        submitButton.setOnClickListener(v -> {
            String newText = Objects.requireNonNull(emojiconEditText.getText()).toString();
            textView.setText(newText);
        });
    }
}