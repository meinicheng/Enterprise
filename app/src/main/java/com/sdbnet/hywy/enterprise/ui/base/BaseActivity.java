package com.sdbnet.hywy.enterprise.ui.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.sdbnet.hywy.enterprise.ActivityStackManager;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.ui.UserLoginActivity;
import com.sdbnet.hywy.enterprise.ui.widget.DialogLoading;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;

import java.lang.reflect.Field;

public class BaseActivity extends Activity {
	protected final String TAG = getClass().getSimpleName();
	protected AlertDialog mAlertDialog;
	protected AsyncTask mRunningTask;
	private DialogLoading mLoadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityStackManager.getStackManager().pushActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ActivityStackManager.getStackManager().popActivity(this);
		if (mRunningTask != null && mRunningTask.isCancelled() == false) {
			mRunningTask.cancel(false);
			mRunningTask = null;
		}
		if (mAlertDialog != null) {
			mAlertDialog.dismiss();
			mAlertDialog = null;
		}
	}

	public void recommandToYourFriend(String url, String shareTitle) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, shareTitle + "   " + url);

		Intent itn = Intent.createChooser(intent, "分享");
		startActivity(itn);
	}

	protected void showShortToast(int pResId) {
		showShortToast(getString(pResId));
	}

	protected void showLongToast(int pResId) {
		showLongToast(getString(pResId));
	}

	protected void showLongToast(String pMsg) {
		Toast.makeText(this, pMsg, Toast.LENGTH_LONG).show();
	}

	protected void showShortToast(String pMsg) {
		Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show();
	}

	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	protected void openActivity(Class<?> pClass, Bundle pBundle) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	protected void openActivity(String pAction) {
		openActivity(pAction, null);
	}

	protected void openActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	/**
	 * 通过反射来设置对话框是否要关闭，在表单校验时很管用， 因为在用户填写出错时点确定时默认Dialog会消失， 所以达不到校验的效果
	 * 而mShowing字段就是用来控制是否要消失的，而它在Dialog中是私有变量， 所有只有通过反射去解决此问题
	 *
	 * @param pDialog
	 * @param pIsClose
	 */
	public void setAlertDialogIsClose(DialogInterface pDialog, Boolean pIsClose) {
		try {
			Field field = pDialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(pDialog, pIsClose);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AlertDialog showAlertDialog(String TitleID, String Message) {
		mAlertDialog = new AlertDialog.Builder(this).setTitle(TitleID)
				.setMessage(Message).show();
		return mAlertDialog;
	}

	public AlertDialog showAlertDialog(int pTitelResID, String pMessage,
			DialogInterface.OnClickListener pOkClickListener) {
		String title = getResources().getString(pTitelResID);
		return showAlertDialog(title, pMessage, pOkClickListener, null, null);
	}

	public AlertDialog showAlertDialog(String pTitle, String pMessage,
			DialogInterface.OnClickListener pOkClickListener,
			DialogInterface.OnClickListener pCancelClickListener,
			DialogInterface.OnDismissListener pDismissListener) {
		mAlertDialog = new AlertDialog.Builder(this)
				.setTitle(pTitle)
				.setMessage(pMessage)
				.setPositiveButton(android.R.string.ok, pOkClickListener)
				.setNegativeButton(android.R.string.cancel,
						pCancelClickListener).show();
		if (pDismissListener != null) {
			mAlertDialog.setOnDismissListener(pDismissListener);
		}
		return mAlertDialog;
	}

	public AlertDialog showAlertDialog(String pTitle, String pMessage,
			String pPositiveButtonLabel, String pNegativeButtonLabel,
			DialogInterface.OnClickListener pOkClickListener,
			DialogInterface.OnClickListener pCancelClickListener,
			DialogInterface.OnDismissListener pDismissListener) {
		mAlertDialog = new AlertDialog.Builder(this).setTitle(pTitle)
				.setMessage(pMessage)
				.setPositiveButton(pPositiveButtonLabel, pOkClickListener)
				.setNegativeButton(pNegativeButtonLabel, pCancelClickListener)
				.show();
		if (pDismissListener != null) {
			mAlertDialog.setOnDismissListener(pDismissListener);
		}
		return mAlertDialog;
	}

	public ProgressDialog showProgressDialog(int pTitelResID, String pMessage,
			DialogInterface.OnCancelListener pCancelClickListener) {
		String title = getResources().getString(pTitelResID);
		return showProgressDialog(title, pMessage, pCancelClickListener);
	}

	public ProgressDialog showProgressDialog(String pTitle, String pMessage,
			DialogInterface.OnCancelListener pCancelClickListener) {
		mAlertDialog = ProgressDialog.show(this, pTitle, pMessage, true, true);
		mAlertDialog.setOnCancelListener(pCancelClickListener);
		return (ProgressDialog) mAlertDialog;
	}

	protected void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	public void defaultFinish() {
		super.finish();
	}

	public void returnLogin(final Context context, String msg, final Dialog dia) {
		PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS, false);
		new AlertDialog.Builder(context)
				.setTitle(getString(R.string.offline_notification))
				.setMessage(msg)
				.setPositiveButton(getString(R.string.reLogin),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 点击ok，退出当前帐号
								PreferencesUtil.clearLocalData(PreferenceManager
										.getDefaultSharedPreferences(context));
								Intent intent = new Intent(context,
										UserLoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
										| Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
								if (dia != null && dia.isShowing())
									dia.dismiss();
							}

						}).show();
	}

	// /**
	// * 得到自定义的progressDialog
	// *
	// * @param context
	// * @param msg
	// * @return
	// */
	// public Dialog createLoadingDialog(Context context, String msg) {
	// LayoutInflater inflater = LayoutInflater.from(context);
	// View v = inflater.inflate(R.layout.loading, null);// 得到加载view
	// RelativeLayout layout = (RelativeLayout) v
	// .findViewById(R.id.dialog_view);// 加载布局
	// TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);//
	// 提示文字
	// tipTextView.setText(msg);// 设置加载信息
	//
	// final Dialog loadingDialog = new Dialog(context,
	// R.style.MyDialogStyle);// 创建自定义样式dialog
	// loadingDialog.setCancelable(true);// 可以用“返回键”取消
	// loadingDialog.setContentView(layout, new RelativeLayout.LayoutParams(
	// LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));// 设置布局
	//
	// ImageView iv_close = (ImageView) v.findViewById(R.id.iv_close);
	// iv_close.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// loadingDialog.dismiss();
	// }
	// });
	// return loadingDialog;
	// }

	public interface CallBackHandler {
		public void onClick(View v);

		public void onProgress(TextView tipTextView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registBroadCast();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unRegistBroadCast();
	}

	// zhangyu

	protected MyBroadCastReceiver myBroadCastReceiver;
	protected final String BROADCAST_SHOW_EXIT_DIALOG = "broadcast_show_exit_dialog";
	protected final String BROADCAST_MSG_EXIT = "broadcast_msg_exit";

	protected void registBroadCast() {
		// 生成广播处理
		myBroadCastReceiver = new MyBroadCastReceiver();
		// 实例化过滤器并设置要过滤的广播
		IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(BROADCAST_SHOW_EXIT_DIALOG);
		// 注册广播
		registerReceiver(myBroadCastReceiver, intentFilter);
	}

	protected void unRegistBroadCast() {
		if (myBroadCastReceiver != null) {
			unregisterReceiver(myBroadCastReceiver);
		}
	}

	protected class MyBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BROADCAST_SHOW_EXIT_DIALOG.equals(action)) {

				String msg = intent.getExtras().getString(BROADCAST_MSG_EXIT);
				showExitDialog(msg);
			}
		}
	}

	private AlertDialog.Builder mExitDialog;
	private boolean isShowExitDialog = false;

	protected void showExitDialog(String msg) {
		PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
				Constants.Value.WORKED);
		if (isShowExitDialog) {
			return;
		}

		if (mExitDialog == null) {
			mExitDialog = new AlertDialog.Builder(this)
					.setTitle(this.getString(R.string.system_tip))
					.setMessage(msg)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									exitApp(BaseActivity.this);
									mExitDialog = null;
									isShowExitDialog = false;
								}
							})
					.setOnCancelListener(
							new DialogInterface.OnCancelListener() {

								@Override
								public void onCancel(DialogInterface dialog) {
									exitApp(BaseActivity.this);
									mExitDialog = null;
									isShowExitDialog = false;
								}
							});
		}
		mExitDialog.show();
		isShowExitDialog = true;

	}

	private void exitApp(Activity activity) {
		PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
				Constants.Value.WORKED);
		// 点击ok，退出当前帐号
		PreferencesUtil.clearLocalData(PreferenceManager
				.getDefaultSharedPreferences(activity));
		Intent intent = new Intent(activity, UserLoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(intent);
	}

	protected void initDialog() {
		if (mLoadingDialog == null)
			mLoadingDialog = new DialogLoading(this);
	}

	protected void showLoading(String msg) {
		showLoading(msg,true);
	}

	protected void showLoading(int resId) {
		showLoading(resId,true);
	}
	protected void showLoading(String msg,boolean cancelable) {
		initDialog();
		mLoadingDialog.setMsg(msg);
		mLoadingDialog.setCancelable(cancelable);
		mLoadingDialog.show();
	}

	protected void showLoading(int resId,boolean cancelable) {
		initDialog();
		mLoadingDialog.setMsg(resId);
		mLoadingDialog.setCancelable(cancelable);
		mLoadingDialog.show();
	}
	protected void dismissLoading() {
		if (mLoadingDialog != null)
			mLoadingDialog.dismiss();
	}

	protected void setCancelable(boolean cancelable) {
		initDialog();
		mLoadingDialog.setCancelable(cancelable);
	}
}
