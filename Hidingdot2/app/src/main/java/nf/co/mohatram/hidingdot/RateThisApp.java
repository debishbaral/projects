/*
 * Copyright 2013-2017 Keisuke Kobayashi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nf.co.mohatram.hidingdot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

/**
 * RateThisApp<br>
 * A library to show the app rate dialog
 * @author Keisuke Kobayashi (k.kobayashi.122@gmail.com)
 *
 */
public class RateThisApp {

    private static Config sConfig = new Config();
    private static Callback sCallback = null;

    private static WeakReference<AlertDialog> sDialogRef = null;


    /**
     * Initialize RateThisApp configuration.
     * @param config Configuration object.
     */
    public static void init(Config config) {
        sConfig = config;
    }

    /**
     * Set callback instance.
     * The callback will receive yes/no/later events.
     * @param callback
     */
    public static void setCallback(Callback callback) {
        sCallback = callback;
    }





    /**
    /**
     * Show the rate dialog
     * @param context
     */
    public static void showRateDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        showRateDialog(context, builder);
    }

    /**
     * Show the rate dialog
     * @param context
     * @param themeId
     */
    /*public static void showRateDialog(final Context context, int themeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, themeId);
        showRateDialog(context, builder);
    }*/



    private static void showRateDialog(final Context context, AlertDialog.Builder builder) {
        if (sDialogRef != null && sDialogRef.get() != null) {
            // Dialog is already present
            return;
        }

        int titleId = sConfig.mTitleId != 0 ? sConfig.mTitleId : R.string.rta_dialog_title;
        int messageId = sConfig.mMessageId != 0 ? sConfig.mMessageId : R.string.rta_dialog_message;
        int cancelButtonID = sConfig.mCancelButton != 0 ? sConfig.mCancelButton : R.string.rta_dialog_cancel;
        int thanksButtonID = sConfig.mNoButtonId != 0 ? sConfig.mNoButtonId : R.string.rta_dialog_no;
        int rateButtonID = sConfig.mYesButtonId != 0 ? sConfig.mYesButtonId : R.string.rta_dialog_ok;
        builder.setTitle(titleId);
        builder.setMessage(messageId);
        builder.setCancelable(sConfig.mCancelable);
        builder.setPositiveButton(rateButtonID, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (sCallback != null) {
                    sCallback.onYesClicked();
                }
                String appPackage = context.getPackageName();
                String url = "market://details?id=" + appPackage;
                if (!TextUtils.isEmpty(sConfig.mUrl)) {
                    url = sConfig.mUrl;
                }
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                }
            }
        });
        builder.setNeutralButton(cancelButtonID, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (sCallback != null) {
                    sCallback.onCancelClicked();
                }
            }
        });
        builder.setNegativeButton(thanksButtonID, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (sCallback != null) {
                    sCallback.onNoClicked();
                }
            }
        });
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (sCallback != null) {
                    sCallback.onCancelClicked();
                }
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sDialogRef.clear();
            }
        });
        sDialogRef = new WeakReference<>(builder.show());
    }




    /**
     * RateThisApp configuration.
     */
    public static class Config {
        private String mUrl = null;
        private int mTitleId = 0;
        private int mMessageId = 0;
        private int mYesButtonId = 0;
        private int mNoButtonId = 0;
        private int mCancelButton = 0;
        private boolean mCancelable = true;

        public void setTitle(@StringRes int stringId) {
            this.mTitleId = stringId;
        }

        /**
         * Set message string ID.
         * @param stringId
         */
       /* public void setMessage(@StringRes int stringId) {
            this.mMessageId = stringId;
        }
*/
        /**
         * Set rate now string ID.
         * @param stringId
         */
        /*public void setYesButtonText(@StringRes int stringId) {
            this.mYesButtonId = stringId;
        }*/

        /**
         * Set no thanks string ID.
         * @param stringId
         */
        /*public void setNoButtonText(@StringRes int stringId) {
            this.mNoButtonId = stringId;
        }*/

        /**
         * Set cancel string ID.
         * @param stringId
         */
        /*public void setCancelButtonText(@StringRes int stringId) {
            this.mCancelButton = stringId;
        }*/

        /**
         * Set navigation url when user clicks rate button.
         * Typically, url will be https://play.google.com/store/apps/details?id=PACKAGE_NAME for Google Play.
         * @param url
         */
        /*public void setUrl(String url) {
            this.mUrl = url;
        }*/

        /*public void setCancelable(boolean cancelable) {
            this.mCancelable = cancelable;
        }*/
    }

    /**
     * Callback of dialog click event
     */
    public interface Callback {
        /**
         * "Rate now" event
         */
        void onYesClicked();

        /**
         * "No, thanks" event
         */
        void onNoClicked();

        /**
         * "Later" event
         */
        void onCancelClicked();
    }
}
