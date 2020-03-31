
#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include <string.h>
#include <errno.h>
#include <android/log.h> 
#include "lame.h"

#define LOG_TAG "LAME_ENCODER"
#define LOGD(format, args...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, format, ##args);
#define BUFFER_SIZE 8192

void throwException(JNIEnv *env, char *message) {

    char *className = "com/redridgeapps/libmp3lame/JniException";

    jclass exClass = (*env) -> FindClass(env, className);

    (*env)->ThrowNew(env, exClass, message);
}

void checkFOpenError(JNIEnv *env, FILE *file) {

    if (file == NULL) {
        throwException(env, strerror(errno));
    }
}

FILE *openFile(JNIEnv *env, char *filename, char *mode) {

    FILE *file = fopen(filename, mode);
    checkFOpenError(env, file);

    return file;
}

void checkEncodingError(JNIEnv *env, int write) {

    switch(write) {
        case -1:
            throwException(env, "mp3buf was too small");
            break;
        case -2:
            throwException(env, "malloc() problem");
            break;
        case -3:
            throwException(env, "lame_init_params() not called");
            break;
        case -4:
            throwException(env, "psycho acoustic problems");
            break;
    }
}

lame_t setupLame(JNIEnv *env, int num_channels, int sample_rate, float bitrate, int quality) {

    lame_t lame = lame_init();
    lame_set_num_channels(lame, num_channels);
    lame_set_in_samplerate(lame, sample_rate);
    lame_set_brate(lame, bitrate);
    lame_set_quality(lame, quality);
    lame_set_VBR(lame, vbr_default);
    int ret_code = lame_init_params(lame);

    if (ret_code < 0)
            throwException(env, "Lame init failed!");

	LOGD("Init parameters:");
	LOGD("Number of channels: %d", num_channels);
	LOGD("Sample rate: %d", sample_rate);
	LOGD("Bitrate: %f", bitrate);
	LOGD("Quality: %d", quality);

    return lame;
}

void Java_com_redridgeapps_libmp3lame_Mp3Encoder_convertWavPcm16ToMP3(JNIEnv *env, jobject jobj, jint num_channels, jint sample_rate, jfloat bitrate, jint quality, jstring wav_path, jstring mp3_path) {

    LOGD("Converting PCM 16");

    int read, write;

    const char *pcm_file = (*env)->GetStringUTFChars(env, wav_path, NULL);
    const char *mp3_file = (*env)->GetStringUTFChars(env, mp3_path, NULL);

    FILE *pcm = openFile(env, pcm_file, "rb");
    FILE *mp3 = openFile(env, mp3_file, "wb");

    (*env)->ReleaseStringUTFChars(env, wav_path, pcm_file);
    (*env)->ReleaseStringUTFChars(env, mp3_path, mp3_file);

    // Skip Wav header
    fseek(pcm, 44, 0);

    short pcm_buffer[BUFFER_SIZE * num_channels];
    unsigned char mp3_buffer[BUFFER_SIZE];

    lame_t lame = setupLame(env, num_channels, sample_rate, bitrate, quality);

    LOGD("Encoding started");

    do {

        // Read Pcm data into pcm_buffer
        read = fread(pcm_buffer, num_channels * sizeof(short), BUFFER_SIZE, pcm);

        if (read == 0) // Encode final bits
            write = lame_encode_flush(lame, mp3_buffer, BUFFER_SIZE);
        else if (num_channels == 1) // Mono Encoding
            write = lame_encode_buffer(lame, pcm_buffer, NULL, read, mp3_buffer, BUFFER_SIZE);
        else if (num_channels == 2) // Stereo encoding
            write = lame_encode_buffer_interleaved(lame, pcm_buffer, read, mp3_buffer, BUFFER_SIZE);

        checkEncodingError(env, write);

        // Write mp3_buffer to file
        fwrite(mp3_buffer, write, 1, mp3);

    } while (read > 0);

    LOGD("Encoding finished");

    lame_close(lame);
    fclose(mp3);
    fclose(pcm);
}

void Java_com_redridgeapps_libmp3lame_Mp3Encoder_convertWavPcmFloatToMP3(JNIEnv *env, jobject jobj, jint num_channels, jint sample_rate, jfloat bitrate, jint quality, jstring wav_path, jstring mp3_path) {

    LOGD("Converting PCM Float");

    int read, write;

    const char *pcm_file = (*env)->GetStringUTFChars(env, wav_path, NULL);
    const char *mp3_file = (*env)->GetStringUTFChars(env, mp3_path, NULL);

    FILE *pcm = openFile(env, pcm_file, "rb");
    FILE *mp3 = openFile(env, mp3_file, "wb");

    (*env)->ReleaseStringUTFChars(env, wav_path, pcm_file);
    (*env)->ReleaseStringUTFChars(env, mp3_path, mp3_file);

    // Skip Wav header
    fseek(pcm, 44, 0);

    float pcm_buffer[BUFFER_SIZE * num_channels];
    unsigned char mp3_buffer[BUFFER_SIZE];

    lame_t lame = setupLame(env, num_channels, sample_rate, bitrate, quality);

    LOGD("Encoding started");

    do {

        // Read pcm data into pcm_buffer
        read = fread(pcm_buffer, num_channels * sizeof(float), BUFFER_SIZE, pcm);

        if (read == 0) // Encode final bits
            write = lame_encode_flush(lame, mp3_buffer, BUFFER_SIZE);
        else if (num_channels == 1) // Mono Encoding
            write = lame_encode_buffer_ieee_float(lame, pcm_buffer, NULL, read, mp3_buffer, BUFFER_SIZE);
        else if (num_channels == 2) // Stereo encoding
            write = lame_encode_buffer_interleaved_ieee_float(lame, pcm_buffer, read, mp3_buffer, BUFFER_SIZE);

        checkEncodingError(env, write);

        // Write mp3_buffer to file
        fwrite(mp3_buffer, write, 1, mp3);

    } while (read > 0);

    LOGD("Encoding finished");

    lame_close(lame);
    fclose(mp3);
    fclose(pcm);
}
