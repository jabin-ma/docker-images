package top.misec.utils;

public class PushData {
        private static PushData instance;

        private PushData() {
        }


        public static PushData getInstance() {
            if (instance == null) {
                instance = new PushData();
            }
            return instance;
        }
    }