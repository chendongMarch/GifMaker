package com.march.gifmaker.extend;

import com.march.gifmaker.base.LZWEncoder;

import java.io.ByteArrayOutputStream;

/**
 * CreateAt : 7/12/17
 * Describe :
 * 持有当前编码完成的 LZWEncoder 和 该次编码顺序，后面用来拼合输出流
 *
 * @author chendong
 */
public class LZWEncoderOrderHolder implements Comparable<LZWEncoderOrderHolder> {

    private int                   mOrder;
    private LZWEncoder            mLZWEncoder;
    private ByteArrayOutputStream mByteArrayOutputStream;

    LZWEncoderOrderHolder(LZWEncoder lzwEncoder, int order) {
        this.mLZWEncoder = lzwEncoder;
        this.mOrder = order;
    }

    public LZWEncoderOrderHolder(LZWEncoder lzwEncoder, int order, ByteArrayOutputStream out) {
        this.mLZWEncoder = lzwEncoder;
        this.mOrder = order;
        this.mByteArrayOutputStream = out;
    }


    @Override
    public int compareTo(LZWEncoderOrderHolder another) {
        return this.mOrder - another.mOrder;
    }

    public LZWEncoder getLZWEncoder() {
        return mLZWEncoder;
    }

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return mByteArrayOutputStream;
    }

    public void setByteArrayOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
        this.mByteArrayOutputStream = byteArrayOutputStream;
    }
}
