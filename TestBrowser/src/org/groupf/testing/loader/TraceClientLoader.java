package org.groupf.testing.loader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.jdt.internal.jarinjarloader.RsrcURLStreamHandlerFactory;

public class TraceClientLoader {
	public static final String MAIN_CLASS = "org.groupf.testing.Testing";
	public static final String ACTUAL_JAR = "browser_ui.jar";
	
	public static void main(String[] args) throws Throwable {
		ClassLoader cl = getSWTClassloader();
		Thread.currentThread().setContextClassLoader(cl);
		try {
			try {
				System.err.println("Launching Browser UI ...");
				Class<?> c = Class.forName(MAIN_CLASS, true, cl);
				Method main = c.getMethod("main",
						new Class[] { args.getClass() });
				main.invoke((Object) null, new Object[] { args });
			} catch (InvocationTargetException ex) {
				if (ex.getCause() instanceof UnsatisfiedLinkError) {
					System.err.println("Launch failed: (UnsatisfiedLinkError)");
					ex.printStackTrace();
					String arch = getArch();
					if ("x86".equals(arch)) {
						System.err
								.println("Try adding '-d32' to your command line arguments");
					} else if ("x64".equals(arch)) {
						System.err
								.println("Try adding '-d64' to your command line arguments");
					}
				} else {
					throw ex;
				}
			}
		} catch (ClassNotFoundException ex) {
			System.err
					.println("Launch failed: Failed to find main class - " + MAIN_CLASS);
		} catch (NoSuchMethodException ex) {
			System.err.println("Launch failed: Failed to find main method");
		} catch (InvocationTargetException ex) {
			Throwable th = ex.getCause();
			if ((th.getMessage() != null)
					&& th.getMessage().toLowerCase()
							.contains("invalid thread access")) {
				System.err
						.println("Launch failed: (SWTException: Invalid thread access)");
				System.err
						.println("Try adding '-XstartOnFirstThread' to your command line arguments");
			} else {
				throw th;
			}
		}
	}

	private static ClassLoader getSWTClassloader() {
		ClassLoader parent = TraceClientLoader.class.getClassLoader();
		URL.setURLStreamHandlerFactory(new RsrcURLStreamHandlerFactory(parent));
		String swtFileName = getSwtJarName();
		try {
			URL actualFileUrl = new URL("rsrc:" + ACTUAL_JAR);
			URL swtFileUrl = new URL("rsrc:" + swtFileName);
			System.err.println("Using SWT Jar: " + swtFileName);
			ClassLoader cl = new URLClassLoader(new URL[] { actualFileUrl,
					swtFileUrl }, parent);

			try {
				// Check we can now load the SWT class
				Class.forName("org.eclipse.swt.widgets.Layout", true, cl);
				System.out.println("Managed to find org.eclipse.swt.widgets.Layout");
			} catch (ClassNotFoundException exx) {
				System.err
						.println("Launch failed: Failed to load SWT class from jar: "
								+ swtFileName);
				throw new RuntimeException(exx);
			}

			return cl;
		} catch (MalformedURLException exx) {
			throw new RuntimeException(exx);
		}
	}

	private static String getSwtJarName() {
		// Detect OS
		String osName = System.getProperty("os.name").toLowerCase();
		String swtFileNameOsPart = osName.contains("win") ? "win32" : osName
				.contains("mac") ? "osx" : osName.contains("linux")
				|| osName.contains("nix") ? "linux" : "";
		if ("".equals(swtFileNameOsPart)) {
			throw new RuntimeException("Launch failed: Unknown OS name: "
					+ osName);
		}

		// Detect 32bit vs 64 bit
		String swtFileNameArchPart = getArch();

		String swtFileName = "swt_" + swtFileNameOsPart + "_" + swtFileNameArchPart + ".jar";
		return swtFileName;
	}

	private static String getArch() {
		// Detect 32bit vs 64 bit
		String jvmArch = System.getProperty("os.arch").toLowerCase();
		String arch = (jvmArch.contains("64") ? "x64" : "x86");
		return arch;
	}
}
