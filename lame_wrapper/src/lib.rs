mod lame;

#[cfg(target_os = "android")]
#[allow(non_snake_case)]
pub mod android {

    use crate::lame::{self, lame_global_flags};
    use jni::objects::{JByteBuffer, JClass};
    use jni::sys::{jfloat, jint, jlong, jshort, jstring};
    use jni::JNIEnv;
    use std::ffi::CStr;

    const LAME_EXCEPTION_CLASS: &str = "com/redridgeapps/mp3encoding/LameException";

    #[no_mangle]
    pub extern "system" fn Java_com_redridgeapps_mp3encoder_Lame_getLameVersion(
        env: JNIEnv,
        _: JClass,
    ) -> jstring {
        let version = unsafe {
            let version_ptr = lame::get_lame_version();
            CStr::from_ptr(version_ptr)
        }
        .to_string_lossy()
        .into_owned();

        let output = env
            .new_string(version)
            .expect("Couldn't create java string!");

        output.into_inner()
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_redridgeapps_mp3encoder_Lame_lameInit(
        _env: JNIEnv,
        _class: JClass,
    ) -> jlong {
        lame::lame_init() as jlong
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_redridgeapps_mp3encoder_Lame_lameClose(
        _env: JNIEnv,
        _class: JClass,
        lame_pointer: jlong,
    ) -> jint {
        let lame_t = &mut *(lame_pointer as *mut lame_global_flags);
        lame::lame_close(lame_t)
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_redridgeapps_mp3encoder_Lame_lameSetNumChannels(
        _env: JNIEnv,
        _class: JClass,
        lame_pointer: jlong,
        num_channels: jint,
    ) -> jint {
        let lame_t = &mut *(lame_pointer as *mut lame_global_flags);
        lame::lame_set_num_channels(lame_t, num_channels) as jint
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_redridgeapps_mp3encoder_Lame_lameSetInSampleRate(
        _env: JNIEnv,
        _class: JClass,
        lame_pointer: jlong,
        sample_rate: jint,
    ) -> jint {
        let lame_t = &mut *(lame_pointer as *mut lame_global_flags);
        lame::lame_set_in_samplerate(lame_t, sample_rate) as jint
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_redridgeapps_mp3encoder_Lame_lameSetBitRate(
        _env: JNIEnv,
        _class: JClass,
        lame_pointer: jlong,
        bit_rate: jint,
    ) -> jint {
        let lame_t = &mut *(lame_pointer as *mut lame_global_flags);
        lame::lame_set_brate(lame_t, bit_rate) as jint
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_redridgeapps_mp3encoder_Lame_lameSetQuality(
        _env: JNIEnv,
        _class: JClass,
        lame_pointer: jlong,
        quality: jint,
    ) -> jint {
        let lame_t = &mut *(lame_pointer as *mut lame_global_flags);
        lame::lame_set_quality(lame_t, quality) as jint
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_redridgeapps_mp3encoder_Lame_lameSetVbr(
        _env: JNIEnv,
        _class: JClass,
        lame_pointer: jlong,
        vbr_mode: jint,
    ) -> jint {
        let lame_t = &mut *(lame_pointer as *mut lame_global_flags);
        lame::lame_set_VBR(lame_t, vbr_mode as u32) as jint
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_redridgeapps_mp3encoder_Lame_lameInitParams(
        _env: JNIEnv,
        _class: JClass,
        lame_pointer: jlong,
    ) -> jint {
        let lame_t = &mut *(lame_pointer as *mut lame_global_flags);
        lame::lame_init_params(lame_t) as jint
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_redridgeapps_mp3encoder_Lame_lameEncodeBuffer(
        env: JNIEnv,
        _class: JClass,
        lame_pointer: jlong,
        pcm_left: JByteBuffer,
        pcm_right: JByteBuffer,
        num_samples: jint,
        mp3_buffer: JByteBuffer,
        mp3_buffer_size: jint,
    ) -> jint {
        let lame_t = &mut *(lame_pointer as *mut lame_global_flags);

        let pcm_left = match env.get_direct_buffer_address(pcm_left) {
            Ok(value) => value.as_ptr() as *const jshort,
            Err(err) => {
                throw_lame_exception(env, format!("{}", err.0));
                return -1;
            }
        };

        let pcm_right = match env.get_direct_buffer_address(pcm_right) {
            Ok(value) => value.as_ptr() as *const jshort,
            Err(err) => {
                throw_lame_exception(env, format!("{}", err.0));
                return -1;
            }
        };

        let mp3_buffer = match env.get_direct_buffer_address(mp3_buffer) {
            Ok(value) => value.as_mut_ptr(),
            Err(err) => {
                throw_lame_exception(env, format!("{}", err.0));
                return -1;
            }
        };

        lame::lame_encode_buffer(
            lame_t,
            pcm_left,
            pcm_right,
            num_samples,
            mp3_buffer,
            mp3_buffer_size,
        ) as jint
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_redridgeapps_mp3encoder_Lame_lameEncodeBufferInterleaved(
        env: JNIEnv,
        _class: JClass,
        lame_pointer: jlong,
        pcm_buffer: JByteBuffer,
        num_samples: jint,
        mp3_buffer: JByteBuffer,
        mp3_buffer_size: jint,
    ) -> jint {
        let lame_t = &mut *(lame_pointer as *mut lame_global_flags);

        let pcm_buffer = match env.get_direct_buffer_address(pcm_buffer) {
            Ok(value) => value.as_mut_ptr() as *mut jshort,
            Err(err) => {
                throw_lame_exception(env, format!("{}", err.0));
                return -1;
            }
        };

        let mp3_buffer = match env.get_direct_buffer_address(mp3_buffer) {
            Ok(value) => value.as_mut_ptr(),
            Err(err) => {
                throw_lame_exception(env, format!("{}", err.0));
                return -1;
            }
        };

        lame::lame_encode_buffer_interleaved(
            lame_t,
            pcm_buffer,
            num_samples,
            mp3_buffer,
            mp3_buffer_size,
        ) as jint
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_redridgeapps_mp3encoder_Lame_lameEncodeBufferIeeeFloat(
        env: JNIEnv,
        _class: JClass,
        lame_pointer: jlong,
        pcm_left: JByteBuffer,
        pcm_right: JByteBuffer,
        num_samples: jint,
        mp3_buffer: JByteBuffer,
        mp3_buffer_size: jint,
    ) -> jint {
        let lame_t = &mut *(lame_pointer as *mut lame_global_flags);

        let pcm_left = match env.get_direct_buffer_address(pcm_left) {
            Ok(value) => value.as_ptr() as *const jfloat,
            Err(err) => {
                throw_lame_exception(env, format!("{}", err.0));
                return -1;
            }
        };

        let pcm_right = match env.get_direct_buffer_address(pcm_right) {
            Ok(value) => value.as_ptr() as *const jfloat,
            Err(err) => {
                throw_lame_exception(env, format!("{}", err.0));
                return -1;
            }
        };

        let mp3_buffer = match env.get_direct_buffer_address(mp3_buffer) {
            Ok(value) => value.as_mut_ptr(),
            Err(err) => {
                throw_lame_exception(env, format!("{}", err.0));
                return -1;
            }
        };

        lame::lame_encode_buffer_ieee_float(
            lame_t,
            pcm_left,
            pcm_right,
            num_samples,
            mp3_buffer,
            mp3_buffer_size,
        ) as jint
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_redridgeapps_mp3encoder_Lame_lameEncodeBufferInterleavedIeeeFloat(
        env: JNIEnv,
        _class: JClass,
        lame_pointer: jlong,
        pcm_buffer: JByteBuffer,
        num_samples: jint,
        mp3_buffer: JByteBuffer,
        mp3_buffer_size: jint,
    ) -> jint {
        let lame_t = &mut *(lame_pointer as *mut lame_global_flags);

        let pcm_buffer = match env.get_direct_buffer_address(pcm_buffer) {
            Ok(value) => value.as_ptr() as *const jfloat,
            Err(err) => {
                throw_lame_exception(env, format!("{}", err.0));
                return -1;
            }
        };

        let mp3_buffer = match env.get_direct_buffer_address(mp3_buffer) {
            Ok(value) => value.as_mut_ptr(),
            Err(err) => {
                throw_lame_exception(env, format!("{}", err.0));
                return -1;
            }
        };

        lame::lame_encode_buffer_interleaved_ieee_float(
            lame_t,
            pcm_buffer,
            num_samples,
            mp3_buffer,
            mp3_buffer_size,
        ) as jint
    }

    #[no_mangle]
    pub unsafe extern "system" fn Java_com_redridgeapps_mp3encoder_Lame_lameEncodeFlush(
        env: JNIEnv,
        _class: JClass,
        lame_pointer: jlong,
        mp3_buffer: JByteBuffer,
        mp3_buffer_size: jint,
    ) -> jint {
        let lame_t = &mut *(lame_pointer as *mut lame_global_flags);

        let mp3_buffer = match env.get_direct_buffer_address(mp3_buffer) {
            Ok(value) => value.as_mut_ptr(),
            Err(err) => {
                throw_lame_exception(env, format!("{}", err.0));
                return -1;
            }
        };

        lame::lame_encode_flush(lame_t, mp3_buffer, mp3_buffer_size) as jint
    }

    fn throw_lame_exception(env: JNIEnv, msg: String) {
        env.throw_new(LAME_EXCEPTION_CLASS, msg).unwrap_or(());
    }
}
