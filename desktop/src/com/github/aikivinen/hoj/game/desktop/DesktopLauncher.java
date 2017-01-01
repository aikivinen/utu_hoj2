package com.github.aikivinen.hoj.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.aikivinen.hoj.game.MyGdxGame;
import com.github.aikivinen.hoj.game.rmi.GameService;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class DesktopLauncher {

    private static MyGdxGame gameInstance;

    public static void main(String[] arg) throws RemoteException {
        gameInstance = new MyGdxGame();

        boolean isServer = arg.length > 0 && arg[0].equals("server");
        System.out.println("Starting game. Server mode: " + isServer);

        initRmi(isServer, gameInstance);

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 640;
        config.width = 800;
        new LwjglApplication(gameInstance, config);
    }

    private static void initRmi(boolean isServer, MyGdxGame instance) {
        if (isServer) {

            try {
                GameService stub = (GameService) UnicastRemoteObject.exportObject(instance, 0);
                Registry registry = LocateRegistry.getRegistry();
                registry.bind("foxgame", stub);

            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (AlreadyBoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Registry registry = LocateRegistry.getRegistry(null); // assume localhost for now
                GameService stub = (GameService) registry.lookup("foxgame");
                gameInstance.setRemote(stub);

                GameService callback = (GameService) UnicastRemoteObject.exportObject(gameInstance, 0);
                stub.setRemoteService(callback);

            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            }

        }
    }
}
