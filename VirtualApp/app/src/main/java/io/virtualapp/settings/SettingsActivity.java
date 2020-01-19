package io.virtualapp.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.launcher3.LauncherFiles;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.Constants;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.helper.utils.FileUtils;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.os.VEnvironment;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.virtualapp.R;
import io.virtualapp.VCommends;
import io.virtualapp.XApp;
import io.virtualapp.abs.ui.VUiKit;
import io.virtualapp.gms.FakeGms;
import io.virtualapp.home.ListAppActivity;
import io.virtualapp.utils.DialogUtil;
import io.virtualapp.utils.Misc;
import io.virtualapp.widgets.filepicker.FilePicker;

/**
 * Settings activity for Launcher. Currently implements the following setting: Allow rotation
 */
public class SettingsActivity extends Activity {

    private static final String ADVANCE_SETTINGS_KEY = "settings_advance";
    private static final String ADD_APP_KEY = "settings_add_app";
    private static final String MODULE_MANAGE_KEY = "settings_module_manage";
    private static final String APP_MANAGE_KEY = "settings_app_manage";
    private static final String TASK_MANAGE_KEY = "settings_task_manage";
    private static final String DESKTOP_SETTINGS_KEY = "settings_desktop";
    private static final String FAQ_SETTINGS_KEY = "settings_faq";
    private static final String DONATE_KEY = "settings_donate";
    private static final String ABOUT_KEY = "settings_about";
    private static final String REBOOT_KEY = "settings_reboot";
    private static final String HIDE_SETTINGS_KEY = "advance_settings_hide_settings";
    private static final String DISABLE_INSTALLER_KEY = "advance_settings_disable_installer";
    public static final String ENABLE_LAUNCHER = "advance_settings_enable_launcher";
    private static final String INSTALL_GMS_KEY = "advance_settings_install_gms";
    public static final String DIRECTLY_BACK_KEY = "advance_settings_directly_back";
    private static final String RECOMMEND_PLUGIN = "settings_plugin_recommend";
    private static final String DISABLE_RESIDENT_NOTIFICATION = "advance_settings_disable_resident_notification";
    private static final String ALLOW_FAKE_SIGNATURE = "advance_settings_allow_fake_signature";
    private static final String DISABLE_XPOSED = "advance_settings_disable_xposed";
    private static final String FILE_MANAGE = "settings_file_manage";
    private static final String PERMISSION_MANAGE = "settings_permission_manage";

    /**
     * 选中的文件列表
     */
    private List<String> csFiles;
    /**
     * copy文件计数器
     */
    private int cpNum;
    /**
     * copy文件的无聊等待
     */
    private ProgressDialog cpProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Display the fragment as the main content.
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }

    /**
     * This fragment shows the launcher preferences.
     */
    public static class SettingsFragment extends PreferenceFragment {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName(LauncherFiles.SHARED_PREFERENCES_KEY);
            addPreferencesFromResource(R.xml.settings_preferences);

            // Setup allow rotation preference

            Preference addApp = findPreference(ADD_APP_KEY);
            Preference moduleManage = findPreference(MODULE_MANAGE_KEY);
            Preference recommend = findPreference(RECOMMEND_PLUGIN);
            Preference appManage = findPreference(APP_MANAGE_KEY);
            Preference taskManage = findPreference(TASK_MANAGE_KEY);
            Preference desktop = findPreference(DESKTOP_SETTINGS_KEY);
            Preference faq = findPreference(FAQ_SETTINGS_KEY);
            Preference donate = findPreference(DONATE_KEY);
            Preference about = findPreference(ABOUT_KEY);
            Preference reboot = findPreference(REBOOT_KEY);
            Preference fileMange = findPreference(FILE_MANAGE);
            Preference permissionManage = findPreference(PERMISSION_MANAGE);

            SwitchPreference disableInstaller = (SwitchPreference) findPreference(DISABLE_INSTALLER_KEY);
            SwitchPreference enableLauncher = (SwitchPreference) findPreference(ENABLE_LAUNCHER);
            SwitchPreference disableResidentNotification = (SwitchPreference) findPreference(DISABLE_RESIDENT_NOTIFICATION);
            SwitchPreference allowFakeSignature = (SwitchPreference) findPreference(ALLOW_FAKE_SIGNATURE);
            SwitchPreference disableXposed = (SwitchPreference) findPreference(DISABLE_XPOSED);

            getPreferenceScreen().removePreference(donate);
            addApp.setOnPreferenceClickListener(preference -> {
                ListAppActivity.gotoListApp(getActivity());
                return false;
            });

            moduleManage.setOnPreferenceClickListener(preference -> {
                try {
                    Intent t = new Intent();
                    t.setComponent(new ComponentName("de.robv.android.xposed.installer", "de.robv.android.xposed.installer.WelcomeActivity"));
                    t.putExtra("fragment", 1);
                    int ret = VActivityManager.get().startActivity(t, 0);
                    if (ret < 0) {
                        Toast.makeText(getActivity(), R.string.xposed_installer_not_found, Toast.LENGTH_SHORT).show();
                    }
                } catch (Throwable ignored) {
                    ignored.printStackTrace();
                }
                return false;
            });

            recommend.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), RecommendPluginActivity.class));
                return false;
            });

            boolean xposedEnabled = VirtualCore.get().isXposedEnabled();
            if (!xposedEnabled) {
                getPreferenceScreen().removePreference(moduleManage);
                getPreferenceScreen().removePreference(recommend);
            }

            appManage.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), AppManageActivity.class));
                return false;
            });

            taskManage.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), TaskManageActivity.class));
                return false;
            });

            faq.setOnPreferenceClickListener(preference -> {
                Uri uri = Uri.parse("https://github.com/android-hacker/VAExposed/wiki/FAQ");
                Intent t = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(t);
                return false;
            });

            desktop.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), com.google.android.apps.nexuslauncher.SettingsActivity.class));
                return false;
            });

            donate.setOnPreferenceClickListener(preference -> {
                Misc.showDonate(getActivity());
                return false;
            });
            about.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), AboutActivity.class));
                return false;
            });

            reboot.setOnPreferenceClickListener(preference -> {
                android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(getActivity())
                        .setTitle(R.string.settings_reboot_title)
                        .setMessage(getResources().getString(R.string.settings_reboot_content))
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            VirtualCore.get().killAllApps();
                            Toast.makeText(getActivity(), R.string.reboot_tips_1, Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create();
                try {
                    alertDialog.show();
                } catch (Throwable ignored) {
                }
                return false;
            });

            disableInstaller.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!(newValue instanceof Boolean)) {
                    return false;
                }
                try {
                    boolean disable = (boolean) newValue;
                    PackageManager packageManager = getActivity().getPackageManager();
                    packageManager.setComponentEnabledSetting(new ComponentName(getActivity().getPackageName(), "vxp.installer"),
                            !disable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                    return true;
                } catch (Throwable ignored) {
                    return false;
                }
            });

            enableLauncher.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!(newValue instanceof Boolean)) {
                    return false;
                }
                try {
                    boolean enable = (boolean) newValue;
                    PackageManager packageManager = getActivity().getPackageManager();
                    packageManager.setComponentEnabledSetting(new ComponentName(getActivity().getPackageName(), "vxp.launcher"),
                            enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                    return true;
                } catch (Throwable ignored) {
                    return false;
                }
            });

            Preference installGms = findPreference(INSTALL_GMS_KEY);
            installGms.setOnPreferenceClickListener(preference -> {
                boolean alreadyInstalled = FakeGms.isAlreadyInstalled(getActivity());
                if (alreadyInstalled) {
                    FakeGms.uninstallGms(getActivity());
                } else {
                    FakeGms.installGms(getActivity());
                }
                return true;
            });

            fileMange.setOnPreferenceClickListener(preference -> {
//                OnlinePlugin.openOrDownload(getActivity(), OnlinePlugin.FILE_MANAGE_PACKAGE,
//                        OnlinePlugin.FILE_MANAGE_URL, getString(R.string.install_file_manager_tips));
                AlertDialog cpDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                        .setTitle(getResources().getString(R.string.file_picker_cp_title))
                        .setMessage(getResources().getString(R.string.file_picker_need_cp_message))
                        .setPositiveButton(getResources().getString(R.string.file_picker_need_cp_export), ((dialog1, which1) -> {
                            createPicker(
                                    getActivity(),
                                    VCommends.REQUEST_SELECT_EXPORT_FILES,
                                    VEnvironment.getUserSystemDirectory().getPath(),
                                    R.string.file_picker_Selected,
                                    true);
                        }))
                        .setNegativeButton(getResources().getString(R.string.file_picker_need_cp_import), ((dialog1, which1) -> {
                            createPicker(
                                    getActivity(),
                                    VCommends.REQUEST_SELECT_IMPORT_FILES,
                                    Environment.getExternalStorageDirectory().getPath(),
                                    R.string.file_picker_need_cp_file,
                                    true);
                        }))
                        .create();
                DialogUtil.showDialog(cpDialog);
                return false;
            });

            permissionManage.setOnPreferenceClickListener(preference -> {
                OnlinePlugin.openOrDownload(getActivity(), OnlinePlugin.PERMISSION_MANAGE_PACKAGE,
                        OnlinePlugin.PERMISSION_MANAGE_URL, getString(R.string.install_permission_manager_tips));
                return false;
            });

            disableXposed.setOnPreferenceChangeListener((preference, newValue) -> {

                if (!(newValue instanceof Boolean)) {
                    return false;
                }

                boolean on = (boolean) newValue;

                File disableXposedFile = getActivity().getFileStreamPath(".disable_xposed"); // 文件不存在代表是保守模式
                if (on) {
                    boolean success;
                    try {
                        success = disableXposedFile.createNewFile();
                    } catch (IOException e) {
                        success = false;
                    }
                    return success;
                } else {
                    return !disableXposedFile.exists() || disableXposedFile.delete();
                }
            });

            disableResidentNotification.setOnPreferenceChangeListener(((preference, newValue) -> {

                if (!(newValue instanceof Boolean)) {
                    return false;
                }

                boolean on = (boolean) newValue;

                File flag = getActivity().getFileStreamPath(Constants.NO_NOTIFICATION_FLAG);
                if (on) {
                    boolean success;
                    try {
                        success = flag.createNewFile();
                    } catch (IOException e) {
                        success = false;
                    }
                    return success;
                } else {
                    return !flag.exists() || flag.delete();
                }
            }));

            if (android.os.Build.VERSION.SDK_INT < 25) {
                // Android NR1 below do not need this.
                PreferenceScreen advance = (PreferenceScreen) findPreference(ADVANCE_SETTINGS_KEY);
                advance.removePreference(disableResidentNotification);
            }

            allowFakeSignature.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!(newValue instanceof Boolean)) {
                    return false;
                }

                boolean on = (boolean) newValue;
                File flag = getActivity().getFileStreamPath(Constants.FAKE_SIGNATURE_FLAG);
                if (on) {
                    boolean success;
                    try {
                        success = flag.createNewFile();
                    } catch (IOException e) {
                        success = false;
                    }
                    return success;
                } else {
                    return !flag.exists() || flag.delete();
                }
            });

        }

        private static void dismiss(ProgressDialog dialog) {
            try {
                dialog.dismiss();
            } catch (Throwable ignored) {
            }
        }

        protected int dp2px(float dp) {
            final float scale = getResources().getDisplayMetrics().density;
            return (int) (dp * scale + 0.5f);
        }

        @Override
        public void startActivity(Intent intent) {
            try {
                super.startActivity(intent);
            } catch (Throwable ignored) {
                Toast.makeText(getActivity(), "startActivity failed.", Toast.LENGTH_SHORT).show();
                ignored.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VCommends.REQUEST_SELECT_APP) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }else if(requestCode == VCommends.REQUEST_SELECT_EXPORT_FILES) {
            if(resultCode == RESULT_OK) {
                //文件模式
                List<String> list = data.getStringArrayListExtra("paths");
                String tpath = "/sdcard/VirtualXposed/";
                cpProgressBar(true);
                VUiKit.defer().when(() -> {
                    copyFiles(list, tpath);
                }).done((res) -> {
                    cpProgressBar(false);
                    cpFileDialog(String.format(Locale.ENGLISH, XApp.getApp().getResources().getString(R.string.file_picker_cp_title_msg2), cpNum, tpath));
                }).fail((v) -> {
                    if(cpNum <= 0){
                        cpProgressBar(false);
                        cpFileDialog(getResources().getString(R.string.file_picker_cp_title_msg));}
                });
            }
        }else if(requestCode == VCommends.REQUEST_SELECT_IMPORT_FILES) {
            if(resultCode == RESULT_OK) {
                //文件模式
                csFiles = data.getStringArrayListExtra("paths");
                if(csFiles != null && csFiles.size() > 0) createPicker(
                        this,
                        VCommends.REQUEST_SELECT_IMPORT_FOLDER,
                        VEnvironment.getUserSystemDirectory().getPath(),
                        R.string.file_picker_cp_to_here,
                        false);
            }
        }else if(requestCode == VCommends.REQUEST_SELECT_IMPORT_FOLDER) {
            if(resultCode == RESULT_OK) {
                //文件夹模式
                String path = data.getStringExtra("path");
                cpProgressBar(true);
                VUiKit.defer().when(() -> {
                    copyFiles(csFiles, path);
                }).done((res) -> {
                    cpProgressBar(false);
                    cpFileDialog(String.format(Locale.ENGLISH, XApp.getApp().getResources().getString(R.string.file_picker_cp_title_msg2), cpNum, path));
                }).fail((v) -> {
                    if(cpNum <= 0){
                        cpProgressBar(false);
                        cpFileDialog(getResources().getString(R.string.file_picker_cp_title_msg));}
                });
            }
        }
    }

    private static void createPicker(Activity ac, int requestCode, String rPath, int addText, boolean csmodel){
        try {
            new FilePicker()
                    .withActivity(ac)
                    .withRequestCode(requestCode)
                    .withStartPath(rPath)
                    .withAddText(ac.getResources().getString(addText))
                    .withChooseMode(csmodel)
                    .start();
        } catch (Exception ex) {
            Toast.makeText(ac, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyFiles(List<String> list, String tPath){
        cpNum = 0;
        for (int i = 0; i < list.size(); i++) {
            try {
                if(!tPath.endsWith("/")) tPath = tPath + "/";
                FileUtils.mkdirs(tPath);
                FileUtils.copyFile(list.get(i), tPath + FileUtils.cutNameOfFilepath(list.get(i)));
                cpNum += 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cpProgressBar(boolean open){
        if(!open) {DialogUtil.dismissDialog(cpProgressBar);return;}
        try{
            cpProgressBar = new ProgressDialog(this);
            cpProgressBar.setCancelable(false);
            cpProgressBar.setMessage(getResources().getString(R.string.wait));
            cpProgressBar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cpFileDialog(String message){
        AlertDialog cpDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle(getResources().getString(R.string.file_picker_cp_title))
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create();
        DialogUtil.showDialog(cpDialog);
    }
}
