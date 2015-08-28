package laughing.sholder.Utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileIOUtils {

	private FileIOUtils() {
	}

	private static class CommonFactory {
		private static FileIOUtils common = new FileIOUtils();
	}

	public static FileIOUtils getInstance() {
		return CommonFactory.common;
	}

	/**
	 * ��ȡassetд��洢
	 * 
	 * @param context
	 * @param assetName
	 *            file name not include path
	 * @param assetPath
	 *            the file path in asset
	 * @param path
	 *            write path
	 */
	public boolean writeAssets(Context context, String assetName,
			String assetPath, String path) {
		File targetFile = new File(path, assetName);
		if (targetFile.exists()) {
			Log.e("writeAssets", path + "/" + assetName
					+ " has already exists!");
			return false;
		}
		InputStream is = null;
		FileOutputStream fos = null;

		String asset = null;
		if (assetPath != null) {
			asset = assetPath + "/" + assetName;
		} else {
			asset = assetName;
		}

		try {
			is = context.getResources().getAssets().open(asset);

			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			fos = new FileOutputStream(targetFile);

			byte[] buffer = new byte[4 * 1024];
			int len = -1;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.flush();
				is.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return true;
	}

	public void fileSaveAs(String oldPath, String newPath) {
		File targetFile = new File(newPath);
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(oldPath);
			fos = new FileOutputStream(targetFile);
			byte[] buffer = new byte[4 * 1024];
			int len = -1;
			while((len = fis.read(buffer))!=-1){
				fos.write(buffer, 0 ,len);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
