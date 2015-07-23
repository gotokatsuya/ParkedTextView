package com.goka.parkedtextview;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by katsuyagoto on 15/07/22.
 */
public class ParkedTextView extends EditText {

    private static final String TAG = ParkedTextView.class.getSimpleName();

    // Able to set
    private String mParkedText = "";

    // Unable to set
    private String mText = "";
    private enum TypingState {
        Start, Typed
    }
    private TypingState mTypingState = TypingState.Start;

    public ParkedTextView(Context context) {
        super(context);
        init();
    }

    public ParkedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParkedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTypingState = TypingState.Start;
        addTextChangedListener(new Watcher(this));
    }

    public void setParkedText(String parkedText) {
        if (!TextUtils.isEmpty(mText)) {
            String typed = mText.substring(0, getBeginningPositionOfParkedText());
            mText = typed + parkedText;
            mParkedText = parkedText;

            textChanged();
        } else {
            mParkedText = parkedText;
        }
    }

    private int getBeginningPositionOfParkedText() {
        int position = mText.length() - mParkedText.length();
        if (position < 0) {
            return 0;
        }
        return position;
    }

    private void goToBeginningOfParkedText() {
        setSelection(getBeginningPositionOfParkedText());
    }

    private String getTypedText() {
        if (mText.endsWith(mParkedText)) {
            return mText.substring(0, getBeginningPositionOfParkedText());
        }
        return mText;
    }

    private void setTypedText(String typedText) {
        mText = typedText;
        mText = getTypedText() + mParkedText;
        textChanged();
    }

    public void setPlaceholderText(String placeholderText) {
        super.setHint(Html.fromHtml("<font color=\"#CCCCCC\">" +placeholderText + "</font>" + "<b>" + mParkedText + "</b>"));
    }

    private void textChanged() {
        switch (mTypingState) {
            case Start:
                if (mText.length() <= 0) {
                    return;
                }
                mText = getTypedText() + mParkedText;
                setText(Html.fromHtml(getTypedText() + "<b>" + mParkedText + "</b>"));
                goToBeginningOfParkedText();

                mTypingState = TypingState.Typed;

            case Typed:
                if (mText.equals(mParkedText)) {
                    mTypingState = TypingState.Start;
                    mText = "";
                    mText = getTypedText() + mParkedText;
                    setText(Html.fromHtml(getTypedText() + "<b>" + mParkedText + "</b>"));
                    return;
                }

                goToBeginningOfParkedText();

            default:
                break;
        }
    }

    private static class Watcher implements TextWatcher {

        private ParkedTextView mParkedTextView;

        public Watcher(ParkedTextView parkedTextView) {
            this.mParkedTextView = parkedTextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mParkedTextView.removeTextChangedListener(this);
            mParkedTextView.setTypedText(s.toString());
            mParkedTextView.addTextChangedListener(this);
        }
    }

}
