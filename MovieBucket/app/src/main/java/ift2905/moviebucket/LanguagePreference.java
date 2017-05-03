package ift2905.moviebucket;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.preference.DialogPreference;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.LinearLayout;


public class LanguagePreference extends DialogPreference {
    private static final String androidns = "http://schemas.android.com/apk/res/android";

    private TextView mSplashText;
    private Context mContext;

    private String mDialogMessage;

    public LanguagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        mDialogMessage = attrs.getAttributeValue(androidns, "dialogMessage");
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout.LayoutParams params;
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6, 6, 6, 6);

        mSplashText = new TextView(mContext);
        if (mDialogMessage != null)
            mSplashText.setText(mDialogMessage);
        layout.addView(mSplashText);

        return layout;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        //RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radiogroup);
    }
}