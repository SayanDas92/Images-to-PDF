package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Font;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.interfaces.OnItemClickListner;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.PageSizeUtils;

import static swati4star.createpdf.util.Constants.DEFAULT_COMPRESSION;
import static swati4star.createpdf.util.SettingsOptions.ImageEnhancementOptionsUtils.getEnhancementOptions;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class SettingsFragment extends Fragment implements OnItemClickListner {

    @BindView(R.id.settings_list)
    RecyclerView mEnhancementOptionsRecycleView;

    private Activity mActivity;
    private SharedPreferences mSharedPreferences;

    public SettingsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, root);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        showSettingsOptions();
        return root;
    }

    private void showSettingsOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        ArrayList<EnhancementOptionsEntity> mEnhancementOptionsEntityArrayList = getEnhancementOptions(mActivity);
        EnhancementOptionsAdapter adapter =
                new EnhancementOptionsAdapter(this, mEnhancementOptionsEntityArrayList);
        mEnhancementOptionsRecycleView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                changeCompressImage();
                break;
            case 1:
                setPageSize();
                break;
            case 2:
                editFontSize();
                break;
            case 3:
                changeFontFamily();
                break;
        }
    }

    /**
     * To modify default image compression value
     */
    private void changeCompressImage() {

        new MaterialDialog.Builder(mActivity)
                .title(mActivity.getString(R.string.compression_image_edit))
                .customView(R.layout.compress_image_default, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive((dialog1, which) -> {
                    final EditText qualityInput = dialog1.getCustomView().findViewById(R.id.quality);
                    int check;
                    try {
                        check = Integer.parseInt(String.valueOf(qualityInput.getText()));
                        if (check > 100 || check < 0) {
                            showSnackbar(mActivity, R.string.invalid_entry);
                        } else {
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putInt(DEFAULT_COMPRESSION, check);
                            editor.apply();
                            showSettingsOptions();
                        }
                    } catch (NumberFormatException e) {
                        showSnackbar(mActivity, R.string.invalid_entry);
                    }
                }).show();
    }


    /**
     * To modify font size
     */
    private void editFontSize() {
        new MaterialDialog.Builder(mActivity)
                .title(mActivity.getString(R.string.font_size_edit))
                .customView(R.layout.dialog_font_size_default, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    final EditText fontInput = dialog.getCustomView().findViewById(R.id.fontInput);
                    try {
                        int check = Integer.parseInt(String.valueOf(fontInput.getText()));
                        if (check > 1000 || check < 0) {
                            showSnackbar(mActivity, R.string.invalid_entry);
                        } else {
                            showSnackbar(mActivity, R.string.font_size_changed);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putInt(Constants.DEFAULT_FONT_SIZE_TEXT, check);
                            editor.apply();
                            showSettingsOptions();

                        }
                    } catch (NumberFormatException e) {
                        showSnackbar(mActivity, R.string.invalid_entry);
                    }
                })
                .show();
    }

    /**
     * To modify font family
     */
    private void changeFontFamily() {
        String fontFamily = mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY);
        int ordinal = Font.FontFamily.valueOf(fontFamily).ordinal();
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .title(mActivity.getString(R.string.font_family_edit))
                .customView(R.layout.dialog_font_family_default, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    View view = dialog.getCustomView();
                    RadioGroup radioGroup = view.findViewById(R.id.radio_group_font_family);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = view.findViewById(selectedId);
                    String fontFamily1 = radioButton.getText().toString();
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(Constants.DEFAULT_FONT_FAMILY_TEXT, fontFamily1);
                    editor.apply();
                    showSettingsOptions();
                })
                .build();
        RadioGroup radioGroup = materialDialog.getCustomView().findViewById(R.id.radio_group_font_family);
        RadioButton rb = (RadioButton) radioGroup.getChildAt(ordinal);
        rb.setChecked(true);
        materialDialog.show();
    }

    /**
     * To modify page size
     */
    public void setPageSize() {
        PageSizeUtils utils = new PageSizeUtils(mActivity);
        MaterialDialog materialDialog = utils.showPageSizeDialog(R.layout.set_page_size_dialog, true);
        materialDialog.setOnDismissListener(dialog -> showSettingsOptions());
    }

}
