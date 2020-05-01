#![allow(non_camel_case_types)]
#![allow(non_upper_case_globals)]
#![allow(dead_code)]

use std::os::raw::{c_char, c_int, c_short, c_uchar};

#[repr(C)]
#[derive(Debug, Copy, Clone)]
pub struct lame_global_struct {
    _unused: [u8; 0],
}

pub(super) type lame_global_flags = lame_global_struct;
pub(super) type lame_t = *mut lame_global_flags;

pub(super) type vbr_mode_e = u32;
pub(super) use vbr_mode_e as vbr_mode;

pub(super) const vbr_mode_e_vbr_off: vbr_mode_e = 0;
pub(super) const vbr_mode_e_vbr_mt: vbr_mode_e = 1;
pub(super) const vbr_mode_e_vbr_rh: vbr_mode_e = 2;
pub(super) const vbr_mode_e_vbr_abr: vbr_mode_e = 3;
pub(super) const vbr_mode_e_vbr_mtrh: vbr_mode_e = 4;
pub(super) const vbr_mode_e_vbr_max_indicator: vbr_mode_e = 5;
pub(super) const vbr_mode_e_vbr_default: vbr_mode_e = 4;

extern "C" {
    pub(super) fn get_lame_version() -> *const c_char;

    pub(super) fn lame_init() -> *mut lame_global_flags;

    pub(super) fn lame_close(arg1: *mut lame_global_flags) -> c_int;

    pub(super) fn lame_set_num_channels(arg1: *mut lame_global_flags, arg2: c_int) -> c_int;

    pub(super) fn lame_set_in_samplerate(arg1: *mut lame_global_flags, arg2: c_int) -> c_int;

    pub(super) fn lame_set_brate(arg1: *mut lame_global_flags, arg2: c_int) -> c_int;

    pub(super) fn lame_set_quality(arg1: *mut lame_global_flags, arg2: c_int) -> c_int;

    pub(super) fn lame_set_VBR(arg1: *mut lame_global_flags, arg2: vbr_mode) -> c_int;

    pub(super) fn lame_init_params(arg1: *mut lame_global_flags) -> c_int;

    pub(super) fn lame_encode_buffer(
        gfp: *mut lame_global_flags,
        buffer_l: *const c_short,
        buffer_r: *const c_short,
        nsamples: c_int,
        mp3buf: *mut c_uchar,
        mp3buf_size: c_int,
    ) -> c_int;

    pub(super) fn lame_encode_buffer_interleaved(
        gfp: *mut lame_global_flags,
        pcm: *mut c_short,
        num_samples: c_int,
        mp3buf: *mut c_uchar,
        mp3buf_size: c_int,
    ) -> c_int;

    pub(super) fn lame_encode_buffer_ieee_float(
        gfp: lame_t,
        pcm_l: *const f32,
        pcm_r: *const f32,
        nsamples: c_int,
        mp3buf: *mut c_uchar,
        mp3buf_size: c_int,
    ) -> c_int;

    pub(super) fn lame_encode_buffer_interleaved_ieee_float(
        gfp: lame_t,
        pcm: *const f32,
        nsamples: c_int,
        mp3buf: *mut c_uchar,
        mp3buf_size: c_int,
    ) -> c_int;

    pub(super) fn lame_encode_flush(
        gfp: *mut lame_global_flags,
        mp3buf: *mut c_uchar,
        size: c_int,
    ) -> c_int;
}
