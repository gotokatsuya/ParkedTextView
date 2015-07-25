package com.goka.parkedtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by katsuyagoto on 15/07/22.
 */
public class ParkedTextView extends EditText {

    private static final String TAG = ParkedTextView.class.getSimpleName();
    private static final String DEFAULT_TEXT_COLOR = "FFFFFF";

    // Able to set
    private String mParkedText = "";
    private boolean mIsBoldParkedText = true;
    private String mParkedTextColor = DEFAULT_TEXT_COLOR;
    private String mParkedHintColor = DEFAULT_TEXT_COLOR;

    // Unable to set
    private String mText = null;
    private enum TypingState {
        Start, Typed
    }
    private TypingState mTypingState = TypingState.Start;

    public ParkedTextView(Context context) {
        super(context);
        init();
    }

    public ParkedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParkedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ParkedTextView, defStyleAttr, 0);

        mParkedText = a.getString(R.styleable.ParkedTextView_parkedText);
        if (mParkedText == null) {
            mParkedText = "";
        }

        String hint = a.getString(R.styleable.ParkedTextView_parkedHint);

        mParkedTextColor = a.getString(R.styleable.ParkedTextView_parkedTextColor);
        if (mParkedTextColor == null) {
            mParkedTextColor = ParkedTextView.DEFAULT_TEXT_COLOR;
        }

        mParkedHintColor = a.getString(R.styleable.ParkedTextView_parkedHintColor);
        if (mParkedHintColor == null) {
            mParkedHintColor = ParkedTextView.DEFAULT_TEXT_COLOR;
        }

        mIsBoldParkedText = a.getBoolean(R.styleable.ParkedTextView_parkedTextBold, true);

        init();

        if (hint != null) {
            setPlaceholderText(hint);
        }

        a.recycle();
    }

    private void init() {
        mText = "";
        observeText();

        mTypingState = TypingState.Start;
        addTextChangedListener(new ParkedTextViewWatcher(this));
    }

    public String getParkedText() {
        return mParkedText;
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
        observeText();

        textChanged();
    }

    private void setEmptyText() {
        setTypedText("");
    }

    public void setPlaceholderText(String placeholderText) {
        Spanned hint = null;
        String parkedTextColor = reformatColor(mParkedTextColor);
        String parkedHintColor = reformatColor(mParkedHintColor);
        if (mIsBoldParkedText) {
            hint = Html.fromHtml(String.format("<font color=\"#%s\">%s</font><font color=\"#%s\"><b>%s</b></font>", parkedHintColor, placeholderText, parkedTextColor, mParkedText));
        } else {
            hint = Html.fromHtml(String.format("<font color=\"#%s\">%s</font><font color=\"#%s\">%s</font>", parkedHintColor, placeholderText, parkedTextColor, mParkedText));
        }
        super.setHint(hint);
    }

    // Call when TypedText is changed
    private String observeText() {
        return mText = getTypedText() + mParkedText;
    }

    private String reformatColor(String color) {
        if (color.startsWith("#")) {
            color = color.substring(1);
        }

        if (color.length() > 6) {
            return color.substring(2);
        }
        return color;
    }

    private Spanned getHtmlText() {
        String parkedTextColor = reformatColor(mParkedTextColor);
        if (mIsBoldParkedText) {
            return Html.fromHtml(String.format("<font color=\"#%s\">%s</font><font color=\"#%s\"><b>%s</b></font>", parkedTextColor, getTypedText(), parkedTextColor, mParkedText));
        }
        return Html.fromHtml(String.format("<font color=\"#%s\">%s</font>", parkedTextColor, getTypedText() + mParkedText));
    }

    private void textChanged() {
        switch (mTypingState) {
            case Start:
                if (mText.length() <= 0) {
                    return;
                }
                setText(getHtmlText(), BufferType.SPANNABLE);
                goToBeginningOfParkedText();

                mTypingState = TypingState.Typed;

            case Typed:
                if (mText.equals(mParkedText)) {
                    mTypingState = TypingState.Start;
                    setText(getHtmlText(), BufferType.SPANNABLE);
                    return;
                }

                setText(getHtmlText(), BufferType.SPANNABLE);

                goToBeginningOfParkedText();

            default:
                break;
        }
    }

    public boolean isBoldParkedText() {
        return mIsBoldParkedText;
    }

    public void setBoldParkedText(boolean boldParkedText) {
        mIsBoldParkedText = boldParkedText;
    }

    public String getParkedTextColor() {
        return mParkedTextColor;
    }

    public void setParkedTextColor(String parkedTextColor) {
        mParkedTextColor = parkedTextColor;
    }

    public String getParkedHintColor() {
        return mParkedHintColor;
    }

    public void setParkedHintColor(String parkedHintColor) {
        mParkedHintColor = parkedHintColor;
    }

    private static class ParkedTextViewWatcher implements TextWatcher {

        private ParkedTextView mParkedTextView;
        private boolean mIsDeleteText;

        public ParkedTextViewWatcher(ParkedTextView parkedTextView) {
            this.mParkedTextView = parkedTextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (before > 0) {
                mIsDeleteText = true;
            } else {
                mIsDeleteText = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            mParkedTextView.removeTextChangedListener(this);

            String text = s.toString();
            if (mIsDeleteText) {

                if (text.length() < mParkedTextView.getParkedText().length()) {
                    mParkedTextView.setEmptyText();
                } else {
                    String parkedText = text.substring(mParkedTextView.getBeginningPositionOfParkedText() - 1);
                    if (!parkedText.equals(mParkedTextView.getParkedText())) {
                        mParkedTextView.setEmptyText();
                    } else {
                        mParkedTextView.setTypedText(text);
                    }
                }
            } else {
                mParkedTextView.setTypedText(text);
            }

            mParkedTextView.addTextChangedListener(this);
        }
    }

}
