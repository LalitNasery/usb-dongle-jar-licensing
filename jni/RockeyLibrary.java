/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seiri.backup_restore.jni;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * JNA interface to libRockey4Smart.so C library This bypasses the broken JNI
 * bindings
 */
public interface RockeyLibrary extends Library {

    // Load the native library (must be static final in interface)
    RockeyLibrary INSTANCE = Native.load("Rockey4Smart", RockeyLibrary.class);

    /**
     * Main Rockey function Native signature: short Rockey(short func, short*
     * handle, int* lp1, int* lp2, short* p1, short* p2, short* p3, short* p4,
     * byte* buffer)
     */
    short Rockey(short func, short[] handle, int[] lp1, int[] lp2,
            short[] p1, short[] p2, short[] p3, short[] p4, byte[] buffer);

    // Function codes (these are implicitly public static final in interfaces)
    short RY_FIND = 1;
    short RY_FIND_NEXT = 2;
    short RY_OPEN = 3;
    short RY_CLOSE = 4;
    short RY_READ = 5;
    short RY_WRITE = 6;
    short RY_RANDOM = 7;
    short RY_SEED = 8;
    short RY_WRITE_USERID = 9;
    short RY_READ_USERID = 10;

    // Error codes
    short ERR_SUCCESS = 0;
    short ERR_NO_ROCKEY = 3;
    short ERR_INVALID_PASSWORD = 4;
    short ERR_INVALID_PASSWORD_OR_ID = 5;
    short ERR_NO_OPEN = 15;
}

