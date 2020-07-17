package hu.blackbelt.judo.tatami.asm2sdk;

import java.io.InputStream;

public class Asm2SDKBundleStreams {

	private final InputStream sdkBundleStream;
	private final InputStream internalBundleStream;

	public Asm2SDKBundleStreams(InputStream sdkBundleStream, InputStream internalBundleStream) {
		this.sdkBundleStream = sdkBundleStream;
		this.internalBundleStream = internalBundleStream;
	}

	public InputStream getSdkBundleStream() {
		return sdkBundleStream;
	}

	public InputStream getInternalBundleStream() {
		return internalBundleStream;
	}

}
