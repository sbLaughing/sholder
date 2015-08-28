package laughing.sholder.Utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

public class MyVibrate {
		public static void Vibrate(final Context context, long milliseconds){
			Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
			vib.vibrate(milliseconds);
		}

}
