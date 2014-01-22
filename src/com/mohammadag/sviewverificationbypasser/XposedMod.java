package com.mohammadag.sviewverificationbypasser;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.setBooleanField;;

public class XposedMod implements IXposedHookZygoteInit, IXposedHookLoadPackage {

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		Class<?> CoverManagerService = findClass("com.android.server.cover.CoverManagerService", null);

		/* Hook constructor and set value there */
		XposedBridge.hookAllConstructors(CoverManagerService, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				setBooleanField(param.thisObject, "mIsCoverVerified", true);
				setBooleanField(param.thisObject, "sIsDeviceSupportVerityCoverQueried", true);
			}
		});

		findAndHookMethod(CoverManagerService, "isCoverVerfied", XC_MethodReplacement.returnConstant(true));
		findAndHookMethod(CoverManagerService, "isDeviceSupportCoverVerify", XC_MethodReplacement.returnConstant(true));
		findAndHookMethod(CoverManagerService, "updateCoverVerificationLocked", boolean.class, new XC_MethodReplacement() {
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
				setBooleanField(param.thisObject, "mIsCoverVerified", true);
				return null;
			}
		});

		findAndHookMethod(CoverManagerService, "getDefaultTypeOfCover", XC_MethodReplacement.returnConstant(1));

		findAndHookMethod(CoverManagerService, "getCoverState", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				Object result = param.getResult();
				XposedHelpers.setIntField(result, "type", 1);
				param.setResult(result);
			}
		});
	}

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.android.settings"))
			return;

		/* Samsung, you have an API you made, use it! Consider firing some developers for keeping old code. */
		Class<?> LockscreenMenuSettings =
				findClass("com.android.settings.LockscreenMenuSettings", lpparam.classLoader);
		findAndHookMethod(LockscreenMenuSettings, "isCoverVerified", XC_MethodReplacement.returnConstant(true));
		findAndHookMethod(LockscreenMenuSettings, "getTypeOfCover", XC_MethodReplacement.returnConstant(1));
	}
}
