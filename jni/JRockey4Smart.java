package com.seiri.backup_restore.jni;

public class JRockey4Smart {

    static {
        try {
            // Try to load from library path first
            System.loadLibrary("JRockey4Smart");
        } catch (UnsatisfiedLinkError e) {
            // Fallback to absolute path
            try {
                System.load("/usr/local/lib/libJRockey4Smart.so.0.3");
            } catch (UnsatisfiedLinkError e2) {
                System.err.println("Failed to load ROCKEY4 library from both library path and /usr/local/lib");
                throw e2;
            }
        }
    }

    public native short Rockey(short func, short[] handle, int[] lp1, int[] lp2, short[] p1,
            short[] p2, short[] p3, short[] p4, byte[] buffer);
    public short RY_FIND = 1;
    public short RY_FIND_NEXT = 2;
    public short RY_OPEN = 3;
    public short RY_CLOSE = 4;
    public short RY_READ = 5;
    public short RY_WRITE = 6;
    public short RY_RANDOM = 7;
    public short RY_SEED = 8;
    public short RY_WRITE_USERID = 9;
    public short RY_READ_USERID = 10;
    public short RY_SET_MODULE = 11;
    public short RY_CHECK_MODULE = 12;
    public short RY_WRITE_ARITHMETIC = 13;
    public short RY_CALCULATE1 = 14;
    public short RY_CALCULATE2 = 15;
    public short RY_CALCULATE3 = 16;
    public short RY_DECREASE = 17;
    public short RY_CALCULATE4 = 18;
    public short RY_CALCULATE5 = 19;
    public short RY_SET_COUNTER = 20;
    public short RY_GET_COUNTER = 21;
    public short RY_DEC_COUNTER = 22;
    public short RY_SET_TIMER = 23;
    public short RY_GET_TIMER = 24;
    public short RY_ADJUST_TIMER = 25;
    public short RY_SET_TIMER_ITV = 26;
    public short RY_GET_TIMER_ITV = 27;
    public short RY_DEC_TIMER = 28;
    public short RY_SET_RSAKEY_N = 29;
    public short RY_SET_RSAKEY_D = 30;
    public short RY_UPDATE_GEN_HEADER = 31;
    public short RY_UPDATE_GEN = 32;
    public short RY_UPDATE_CHECK = 33;
    public short RY_UPDATE = 34;
    public short RY_UNPACK = 35;

    public short RY_FREEEPROM = 89;
    public short RY_SET_DES_KEY = 41;
    public short RY_DES_ENC = 42;
    public short RY_DES_DEC = 43;
    public short RY_RSA_ENC = 44;
    public short RY_RSA_DEC = 45;
    public short RY_READ_EX = 46;
    public short RY_WRITE_EX = 47;

    public short RY_SETPASSWORDID = (short) 0xf0;
    public short RY_AGENTBURN = (short) 0xf3;
    public short RY_GETVERSION = (short) 0xf7;

    public short RY_SET_COUNTER_EX = (short) 0xA0;
    public short RY_GET_COUNTER_EX = (short) 0xA1;
    public short RY_SET_TIMER_EX = (short) 0xA2;
    public short RY_GET_TIMER_EX = (short) 0xA3;
    public short RY_ADJUST_TIMER_EX = (short) 0xA4;
    public short RY_UPDATE_EX = (short) 0xA8;
    public short RY_SET_UPDATE_KEY = (short) 0xA9;
    public short RY_ADD_UPDATE_HEADER = (short) 0xAA;
    public short RY_ADD_UPDATE_CONTENT = (short) 0xAB;
    public short RY_GET_TIME_DWORD = (short) 0xAC;
    public short RY_VERSION = 100;

    public short DES_SINGLE_MODE = 0;
    public short DES_TRIPLE_MODE = 1;

    public short RSA_PRIVATE_KEY = 0;
    public short RSA_PUBLIC_KEY = 1;

    public short RSA_ROCKEY_PADDING = 0;
    public short RSA_USER_PADDING = 1;

    public short ERR_SUCCESS = 0;
    public short ERR_NO_PARALLEL_PORT = 1;
    public short ERR_NO_DRIVER = 2;
    public short ERR_NO_ROCKEY = 3;
    public short ERR_INVALID_PASSWORD = 4;
    public short ERR_INVALID_PASSWORD_OR_ID = 5;
    public short ERR_SETID = 6;
    public short ERR_INVALID_ADDR_OR_SIZE = 7;
    public short ERR_UNKNOWN_COMMAND = 8;
    public short ERR_NOTBELEVEL3 = 9;
    public short ERR_READ = 10;
    public short ERR_WRITE = 11;
    public short ERR_RANDOM = 12;
    public short ERR_SEED = 13;
    public short ERR_CALCULATE = 14;
    public short ERR_NO_OPEN = 15;
    public short ERR_OPEN_OVERFLOW = 16;
    public short ERR_NOMORE = 17;
    public short ERR_NEED_FIND = 18;
    public short ERR_DECREASE = 19;

    public short ERR_AR_BADCOMMAND = 20;
    public short ERR_AR_UNKNOWN_OPCODE = 21;
    public short ERR_AR_WRONGBEGIN = 22;
    public short ERR_AR_WRONG_END = 23;
    public short ERR_AR_VALUEOVERFLOW = 24;
    public short ERR_TOOMUCHTHREAD = 25;
    public short ERR_INVALID_RY4S = 30;
    public short ERR_INVALID_PARAMETER = 31;
    public short ERR_INVALID_TIMEVALUE = 32;
    public short ERR_SET_DES_KEY = 40;
    public short ERR_DES_ENCRYPT = 41;
    public short ERR_DES_DECRYPT = 42;
    public short ERR_SET_RSAKEY_N = 43;
    public short ERR_SET_RSAKEY_D = 44;
    public short ERR_RSA_ENCRYPT = 45;
    public short ERR_RSA_DECRYPT = 46;
    public short ERR_INVALID_LENGTH = 47;
    public short ERR_UNKNOWN = -1;
    public short ERR_RECEIVE_NULL = (short) 0x100;
    public short ERR_PRNPORT_BUSY = (short) 0x101;
    public short ERR_UNKNOWN_SYSTEM = (short) 0x102;
    public short ERROR_UNINIT_TIME_UNIT = (short) 0x103;
    public short ERR_XUTEST = (short) 0xAA;
}

