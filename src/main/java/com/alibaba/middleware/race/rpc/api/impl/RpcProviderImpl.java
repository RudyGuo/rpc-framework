package com.alibaba.middleware.race.rpc.api.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.middleware.race.rpc.api.RpcProvider;

public class RpcProviderImpl extends RpcProvider {

	Class<?> serviceInterface;
	String version;
	Object serviceInstance;
	int timeout;
	String serializeType;

	public RpcProviderImpl() {
	}

	/**
	 * init Provider
	 */
	private void init() {
		// TODO
	}

	/**
	 * set the interface which this provider want to expose as a service
	 * 
	 * @param serviceInterface
	 */
	public RpcProvider serviceInterface(Class<?> serviceInterface) {
		// TODO
		this.serviceInterface = serviceInterface;
		return this;
	}

	/**
	 * set the version of the service
	 * 
	 * @param version
	 */
	public RpcProvider version(String version) {
		// TODO
		this.version = version;
		return this;
	}

	/**
	 * set the instance which implements the service's interface
	 * 
	 * @param serviceInstance
	 */
	public RpcProvider impl(Object serviceInstance) {
		// TODO
		this.serviceInstance = serviceInstance;
		return this;
	}

	/**
	 * set the timeout of the service
	 * 
	 * @param timeout
	 */
	public RpcProvider timeout(int timeout) {
		// TODO
		this.timeout = timeout;
		return this;
	}

	/**
	 * set serialize type of this service
	 * 
	 * @param serializeType
	 */
	public RpcProvider serializeType(String serializeType) {
		// TODO
		this.serializeType = serializeType;
		return this;
	}

	/**
	 * publish this service if you want to publish your service , you need a
	 * registry server. after all , you cannot write servers' ips in config file
	 * when you have 1 million server. you can use ZooKeeper as your registry
	 * server to make your services found by your consumers.
	 */
	public void publish() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(8888);
			ExecutorService pool = Executors.newCachedThreadPool();
			while (true) {
				final Socket socket = serverSocket.accept();
				Thread thread = new Thread() {
					@Override
					public void run() {
						try {
							ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
							ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
							String name = ois.readUTF();
							Class<?>[] parameterTypes = (Class<?>[]) ois.readObject();
							Object[] arguments = (Object[]) ois.readObject();
							Method method = serviceInterface.getDeclaredMethod(name, parameterTypes);
							Object result = method.invoke(serviceInstance,arguments);
							oos.writeObject(result);
						} catch (IOException | ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				};
				pool.submit(thread);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
