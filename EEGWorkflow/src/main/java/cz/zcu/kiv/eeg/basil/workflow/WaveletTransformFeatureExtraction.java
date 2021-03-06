package cz.zcu.kiv.eeg.basil.workflow;

import cz.zcu.kiv.eegdsp.common.ISignalProcessingResult;
import cz.zcu.kiv.eegdsp.common.ISignalProcessor;
import cz.zcu.kiv.eegdsp.main.SignalProcessingFactory;
import cz.zcu.kiv.eegdsp.wavelet.discrete.WaveletResultDiscrete;
import cz.zcu.kiv.eegdsp.wavelet.discrete.WaveletTransformationDiscrete;
import cz.zcu.kiv.eegdsp.wavelet.discrete.algorithm.wavelets.WaveletDWT;

import java.io.Serializable;

/**
 * 
 * Features extraction based on discrete wavelet transformation using eegdsp
 * library
 * 
 * @author Jaroslav Klaus
 *
 */
public class WaveletTransformFeatureExtraction implements IFeatureExtraction, Serializable {

	/**
	 * Subsampling factor
	 */
	private int downSmplFactor = 1;

	/**
	 * Name of the wavelet
	 */
	private int NAME;

	/**
	 * Size of feature vector
	 */
	private int FEATURE_SIZE = 16;

	private int numberOfChannels = 0;

	private ISignalProcessor dwt;

	/**
	 * Constructor for the wavelet transform feature extraction with default
	 * wavelet
	 */
	public WaveletTransformFeatureExtraction() {
		this.NAME = 8;
		setupDwt();
	}
	
	public WaveletTransformFeatureExtraction(int name, int featureSize, int downSmplFactor) {
		setWaveletName(name);
		this.FEATURE_SIZE = featureSize;
		this.downSmplFactor = downSmplFactor;
		setupDwt();
	}

	/**
	 * Constructor for the wavelet transform feature extraction with user
	 * defined wavelet
	 * 
	 * @param name
	 *            - name of the wavelet transform method
	 */
	public WaveletTransformFeatureExtraction(int name) {
		setWaveletName(name);
		setupDwt();
	}

	private void setupDwt(){
		dwt = SignalProcessingFactory.getInstance()
				.getWaveletDiscrete();
		String[] names = ((WaveletTransformationDiscrete) dwt)
				.getWaveletGenerator().getWaveletNames();
		WaveletDWT wavelet = null;
		try {
			wavelet = ((WaveletTransformationDiscrete) dwt)
					.getWaveletGenerator().getWaveletByName(names[NAME]);
		} catch (Exception e) {
			System.out
					.println("Exception loading wavelet " + names[NAME] + ".");
		}
		((WaveletTransformationDiscrete) dwt).setWavelet(wavelet);
	}

	/**
	 * Method that creates a wavelet by a name using SignalProcessingFactory and
	 * processes the signal
	 * 
	 * @param data
	 *            - source epochs
	 * @return - normalized feature vector with only approximation coefficients
	 */
	@Override
	public FeatureVector extractFeatures(EEGDataPackage data) {
		double[][] epoch = data.getData();

		ISignalProcessingResult res;
		numberOfChannels = epoch.length;
		double[] features = new double[FEATURE_SIZE * numberOfChannels];
		int i = 0;
		for (double[] channel : epoch) {
			res = dwt.processSignal(channel);
			for (int j = 0; j < FEATURE_SIZE; j++) {
				features[i * FEATURE_SIZE + j] = ((WaveletResultDiscrete) res)
						.getDwtCoefficients()[j];
			}
			i++;
		}

		features = SignalProcessing.normalize(features);
		FeatureVector fv = new FeatureVector(features);
		return fv;
	}

	/**
	 * Gets feature vector dimension
	 * 
	 * @return - feature vector dimension
	 */
	@Override
	public int getFeatureDimension() {
		return FEATURE_SIZE * numberOfChannels / downSmplFactor;
	}

	/**
	 * Sets wavelet name
	 * 
	 * @param name
	 *            - number that indicates the wavelet name
	 */
	private void setWaveletName(int name) {
		if (name >= 0 && name <= 17) {
			this.NAME = name;
		} else
			throw new IllegalArgumentException(
					"Wavelet Name must be >= 0 and <= 17");
	}

	@Override
	public String toString() {
		/*
	  Number of samples to be used - Fs = 1000 Hz expected
	 */
		int epochSize = 0;
		return "DWT: EPOCH_SIZE: " + epochSize +
				" FEATURE_SIZE: " + this.FEATURE_SIZE +
				" WAVELETNAME: " + this.NAME + "\n";
	}
}
