package mathInterface;

public class KeyInterpreter {
	// key information
		private static final char ASCII_0 = 48;
		private static final char ASCII_9 = 57;
		private static final char ASCII_BACKSPACE = 8;
		
		
		public static boolean isInt(char key) {
			return key >= ASCII_0 && key <= ASCII_9;
		}

		public static boolean isDelete(char key) {

			return (int) key == ASCII_BACKSPACE;
		}

		public static int toInt(char key) {
			return key - ASCII_0;
		}
}
