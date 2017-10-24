package com.thomsonreuters.trtn.wisteria.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;

import com.thomsonreuters.trtn.wisteria.util.ResourceHelper;

public abstract class SslSupport {
	private ByteBuffer networkInputDataBuffer;
	private ByteBuffer networkOutputDataBuffer;
	private ByteBuffer applicationInputDataBuffer;
	private SSLContext sslContext;
	private SSLEngine sslEngine;
	private SslConfiguration configuration;
	
	private static final ByteBuffer emptyByteBuffer = ByteBuffer.allocate(0); 
	
	private boolean handshakeCompleted;
	
	private static final int DEFAULT_BUFFER_SIZE = 1024;
	
	
	public SslSupport(SslConfiguration configuration, InetSocketAddress peer) throws GeneralSecurityException, IOException{
		this.configuration = configuration;
		init(peer);
		this.networkInputDataBuffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE);
		this.networkOutputDataBuffer = ByteBuffer.allocateDirect(this.sslEngine.getSession().getPacketBufferSize());
		try {
			this.sslEngine.beginHandshake();
		} catch (SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void init(InetSocketAddress peer) throws GeneralSecurityException, IOException{
		
		if(this.configuration.useClientMode()){
			this.sslContext = createServerSSLContext(this.configuration);
		}else{
			this.sslContext = createClientSSLContext(this.configuration);
		}
		
		if(peer == null){
			this.sslEngine = this.sslContext.createSSLEngine(); 
		}else{
			this.sslEngine = this.sslContext.createSSLEngine(peer.getHostName(), peer.getPort());
		}
		
		this.sslEngine.setUseClientMode(this.configuration.useClientMode());
		if(!this.configuration.useClientMode()){
			this.sslEngine.setNeedClientAuth(this.configuration.needClientAuth());
			this.sslEngine.setWantClientAuth(this.configuration.wantClientAuth());
		}
		
		if(this.configuration.enabledCipherSuites() != null)
			this.sslEngine.setEnabledCipherSuites(this.configuration.enabledCipherSuites());		
		
		if(this.configuration.enabledProtocols() == null)
			this.sslEngine.setEnabledProtocols(this.configuration.enabledProtocols());
		
	}
	
	private ByteBuffer expand(ByteBuffer buf){		
		ByteBuffer b = ByteBuffer.allocateDirect(buf.capacity() << 1);
		buf.compact();
		buf.flip();
		b.put(buf);
		return b;
	}
	
	public Result unwrap(ByteBuffer src, ByteBuffer dest) throws SSLException{
		int count = 0;
		//If the buffer has remainning, the remainning data will be put before the position,
		//and the buffer can continue to be written without the remainning data being overwritten.
		//If the buffer has no remaining, the behaviore is same as clear()
		//TODO
		if(this.networkInputDataBuffer == null ){
			this.networkInputDataBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
		}/*else if(this.networkInputDataBuffer.hasRemaining()){
			this.networkInputDataBuffer.compact();
		}*/
		
		
		while(networkInputDataBuffer.remaining() < src.remaining()){
			networkInputDataBuffer = expand(networkInputDataBuffer);
		}
		networkInputDataBuffer.put(src);
		
		
		//TODO
		if(this.applicationInputDataBuffer == null){
			this.applicationInputDataBuffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE);
		}else if(this.applicationInputDataBuffer.hasRemaining()){
			this.applicationInputDataBuffer.compact();
		}else{
			this.applicationInputDataBuffer.clear();
		}

		if(!this.handshakeCompleted){
			handshake();
		}else{
			SSLEngineResult result = null;
			networkInputDataBuffer.flip();
			while(true){				
				result = sslEngine.unwrap(networkInputDataBuffer, this.applicationInputDataBuffer);
				if(result.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW){					
					this.applicationInputDataBuffer = expand(this.applicationInputDataBuffer);
					continue;
				}
				break;
			}
			if(networkInputDataBuffer.hasRemaining()){
				networkInputDataBuffer.compact();
			}else{
				networkInputDataBuffer.clear();
			}
			renegotiateIfNecessary(result);
		}		
		
		this.applicationInputDataBuffer.flip();
		while(this.applicationInputDataBuffer.hasRemaining()){
			if(dest.hasRemaining()){
				dest.put(this.applicationInputDataBuffer.get());
				count++;
			}else{
				return Result.OVERFLOW;
			}
		}
		//applicationInputDataBuffer ends with read status in this method.
		return Result.OK;
	}
	
	public Result wrap(ByteBuffer src, ByteBuffer dest) throws SSLException{		
		if(!handshakeCompleted){
			throw new SSLException("Handshake not completed. ");
		}
		
		int count = 0;
		
		if(this.networkOutputDataBuffer == null){
			this.networkOutputDataBuffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE);
		}else if(this.networkOutputDataBuffer.hasRemaining()){
			this.networkOutputDataBuffer.compact();
		}else{
			this.networkOutputDataBuffer.clear();
		}
		
		SSLEngineResult result = null;
		while(true){
			result = this.sslEngine.wrap(src, this.networkOutputDataBuffer);
			if(result.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW){
				this.networkOutputDataBuffer = expand(this.networkOutputDataBuffer);
				continue;
			}
			break;
		}
		
		this.networkOutputDataBuffer.flip();
		while(this.networkOutputDataBuffer.hasRemaining()){
			if(dest.hasRemaining()){
				dest.put(this.networkOutputDataBuffer.get());
				count++;
			}else{
				return Result.OVERFLOW;
			}
		}
		return Result.OK;
	}
	
	private void handshake() throws SSLException{
		/*SSLEngineResult.HandshakeStatus.FINISHED can only be generated by wrap and unwrap, but not generated
		by SSLEngine.getHandshakeStatus()*/
		SSLEngineResult result = null;
		SSLEngineResult.HandshakeStatus handshakeStatus = this.sslEngine.getHandshakeStatus();			
		try {
			while(true){
				switch(handshakeStatus){
				case NEED_WRAP:
					while(true){
						result = this.sslEngine.wrap(emptyByteBuffer, networkOutputDataBuffer);
						handshakeStatus = result.getHandshakeStatus();
						if(result.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW){
							ByteBuffer b = ByteBuffer.allocateDirect(networkOutputDataBuffer.capacity() << 1);
							networkOutputDataBuffer.flip();
							b.put(networkOutputDataBuffer);
							networkOutputDataBuffer = b;
							continue;
						}
						break;
					}
					networkOutputDataBuffer.flip();
					try {
						sendWrappedHandshakeData(networkOutputDataBuffer);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(networkOutputDataBuffer.hasRemaining()){
						//networkOutputDataBuffer.compact();
					}else{
						networkOutputDataBuffer.clear();
					}
					break;
				case NEED_UNWRAP:
					if(networkInputDataBuffer != null){
						networkInputDataBuffer.flip();
					}else{
						return;
					}
					
					while(true){
						if(this.networkInputDataBuffer.hasRemaining()){
							try {
								
								for(int i=0; i < 2; i++){
									System.out.println(this.networkInputDataBuffer.get(i));
								}
								
								result = this.sslEngine.unwrap(this.networkInputDataBuffer, this.applicationInputDataBuffer);
								if(this.networkInputDataBuffer.hasRemaining()){
									this.networkInputDataBuffer.compact();
								}else{
									this.networkInputDataBuffer = null;
								}
								handshakeStatus = result.getHandshakeStatus();
								if(result.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW){
									break;
								}
								
								renegotiateIfNecessary(result);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return;
							}
							break;
						}else{
							this.networkInputDataBuffer.clear();
							return;
						}
					}
					break;
					
					
				case NEED_TASK:
					Runnable task = null;
					while((task = this.sslEngine.getDelegatedTask()) != null){
						task.run();
					}
					handshakeStatus = this.sslEngine.getHandshakeStatus();
					break;
				case FINISHED:
					this.handshakeCompleted = true;
					if(networkOutputDataBuffer.hasRemaining()){
						networkOutputDataBuffer.compact();
					}else{
						networkOutputDataBuffer = null;
					}
					onHandshakeCompleted();
					return;
				default:
					System.out.println("Handshake failed. Unexpected status: " + handshakeStatus);
					return;
				}
			}
		} finally {
			this.applicationInputDataBuffer.flip();
		}
	}	
	
	public boolean isHandshakeCompleted(){
		return handshakeCompleted;
	}

	public enum Result{
		OK, OVERFLOW, UNDERFLOW;
	}
	
	protected abstract void sendWrappedHandshakeData(ByteBuffer buffer) throws IOException;
	protected abstract void onHandshakeCompleted();
	
	private void renegotiateIfNecessary(SSLEngineResult unwrapResult) throws SSLException{
		if ( ( unwrapResult.getStatus() != SSLEngineResult.Status.CLOSED ) && 
	             ( unwrapResult.getStatus() != SSLEngineResult.Status.BUFFER_UNDERFLOW ) &&
	             ( unwrapResult.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING ) ) {
	            // Renegotiation required.
	            handshakeCompleted = false;
	            handshake();
	        }
	}
	
/*	public static SSLContext newServerSSLContext(SslConfiguration configuration){
		try {
			char[] password = "password".toCharArray();
			SSLContext sslContext = SSLContext.getInstance("TLS");
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			KeyStore ks = KeyStore.getInstance("JKS");
			KeyStore ts = null;
			
			InputStream is = SSLHelper.class.getResourceAsStream("/com/alanx/study/ssl/alan.cert");
			System.out.println(is == null);
			
			ks.load(is, password);
			kmf.init(ks, password);			
			tmf.init(ks);
			sslContext.init(kmf.getKeyManagers(), BogusTrustManagerFactory.X509_MANAGERS, null);
			
			return sslContext;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}*/
	
	public static SSLContext createServerSSLContext(SslConfiguration configuration) throws GeneralSecurityException, IOException{
		SSLContext sslContext = SSLContext.getInstance(configuration.sslContextProtocol());
		
		KeyManager[] kms = null;
		TrustManager[] tms = null;
		
		kms = createKeyManagerFactory(configuration).getKeyManagers();
		
		if(configuration.isTrustAll()){
			tms = BogusTrustManagerFactory.X509_MANAGERS;
		}else{
			tms = createTrustManagerFactory(configuration).getTrustManagers();
		}
		
		sslContext.init(kms, tms, null);
		return sslContext;
	}
	
	public static SSLContext createClientSSLContext(SslConfiguration configuration) throws GeneralSecurityException, IOException{
		SSLContext sslContext = SSLContext.getInstance(configuration.sslContextProtocol());
		
		TrustManager[] tms = null;
		if(configuration.isTrustAll()){
			tms = BogusTrustManagerFactory.X509_MANAGERS;
		}else{
			tms = createTrustManagerFactory(configuration).getTrustManagers();
		}
		
		sslContext.init(null, tms, null);
		return sslContext;
	}
	
	public static TrustManagerFactory createTrustManagerFactory(SslConfiguration configuration) throws GeneralSecurityException, IOException{
		String keyStore = configuration.trustStore();
		char[] keyStorePassphase = configuration.trustStorePassphase();
		String trustManagerFactoryAlgorithm = configuration.trustManagerFactoryAlgorithm();
		
		KeyStore ks = KeyStore.getInstance("JKS");
		String trustKeyStore = keyStore;
		if(trustKeyStore == null){
			throw new IllegalArgumentException("Invalid trusteStore. ");
		}
		ks.load(ResourceHelper.load(trustKeyStore), keyStorePassphase);
		trustManagerFactoryAlgorithm = (trustManagerFactoryAlgorithm == null || trustManagerFactoryAlgorithm.isEmpty()) ?
				TrustManagerFactory.getDefaultAlgorithm() : trustManagerFactoryAlgorithm;
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(trustManagerFactoryAlgorithm);
		tmf.init(ks);
		return tmf;
	}
	
	public static KeyManagerFactory createKeyManagerFactory(SslConfiguration configuration) throws GeneralSecurityException, IOException{
		String keyStore = configuration.keyStore(); 
		char[] keyStorePassphase = configuration.keyStorePassphase();
		String trustManagerFactoryAlgorithm = configuration.trustManagerFactoryAlgorithm();
		
		KeyStore ks = KeyStore.getInstance("JKS");
		String trustKeyStore = keyStore;
		if(trustKeyStore == null){
			throw new IllegalArgumentException("Invalid keyStore. ");
		}
		ks.load(ResourceHelper.load(trustKeyStore), keyStorePassphase);
		trustManagerFactoryAlgorithm = (trustManagerFactoryAlgorithm == null || trustManagerFactoryAlgorithm.isEmpty()) ?
				TrustManagerFactory.getDefaultAlgorithm() : trustManagerFactoryAlgorithm;
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(trustManagerFactoryAlgorithm);
		kmf.init(ks, keyStorePassphase);
		return kmf;
	}
	
	
	
	
	
	
	
	public static void main(String args[]){
		ByteBuffer b = ByteBuffer.allocate(10);
		System.out.println(b.position());
		for(int i = 0; i<5; i++){
			b.put((byte)i);
		}
		System.out.println(b.position());
		System.out.println(b);
		b.flip();
		b.compact();
		//b.flip();
		System.out.println(b);
		for(int i = 0; i<5; i++){
			System.out.println(b.get());
		}		
	}
	

}

class BogusTrustManagerFactory extends TrustManagerFactorySpi {

    static final X509TrustManager X509 = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] x509Certificates,
                String s) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] x509Certificates,
                String s) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    static final TrustManager[] X509_MANAGERS = new TrustManager[] { X509 };

    public BogusTrustManagerFactory() {
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return X509_MANAGERS;
    }

    @Override
    protected void engineInit(KeyStore keystore) throws KeyStoreException {
        // noop
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters)
            throws InvalidAlgorithmParameterException {
        // noop
    }
}